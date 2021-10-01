package com.vneuron.cheese.order.service.services;

import com.vneuron.cheese.order.service.domain.CheeseOrder;
import com.vneuron.cheese.order.service.domain.CheeseOrderEventEnum;
import com.vneuron.cheese.order.service.domain.CheeseOrderStatusEnum;
import com.vneuron.cheese.order.service.repositories.CheeseOrderRepository;
import com.vneuron.creamery.model.CheeseOrderDto;
import com.vneuron.cheese.order.service.sm.CheeseOrderStateChangeInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@RequiredArgsConstructor
@Service
public class CheeseOrderManagerImpl implements CheeseOrderManager {

    public static final String ORDER_ID_HEADER = "ORDER_ID_HEADER";

    private final StateMachineFactory<CheeseOrderStatusEnum, CheeseOrderEventEnum> stateMachineFactory;
    private final CheeseOrderRepository cheeseOrderRepository;
    private final CheeseOrderStateChangeInterceptor cheeseOrderStateChangeInterceptor;

    @Transactional
    @Override
    public CheeseOrder newCheeseOrder(CheeseOrder cheeseOrder) {
        cheeseOrder.setId(null);
        cheeseOrder.setOrderStatus(CheeseOrderStatusEnum.NEW);

        CheeseOrder savedCheeseOrder = cheeseOrderRepository.saveAndFlush(cheeseOrder);
        sendCheeseOrderEvent(savedCheeseOrder, CheeseOrderEventEnum.VALIDATE_ORDER);
        return savedCheeseOrder;
    }

    @Transactional
    @Override
    public void processValidationResult(UUID cheeseOrderId, Boolean isValid) {
        log.debug("Process Validation Result for cheeseOrderId: " + cheeseOrderId + " Valid? " + isValid);

        Optional<CheeseOrder> cheeseOrderOptional = cheeseOrderRepository.findById(cheeseOrderId);

        cheeseOrderOptional.ifPresentOrElse(cheeseOrder -> {
            if(isValid){
                sendCheeseOrderEvent(cheeseOrder, CheeseOrderEventEnum.VALIDATION_PASSED);

                //wait for status change
                awaitForStatus(cheeseOrderId, CheeseOrderStatusEnum.VALIDATED);

                CheeseOrder validatedOrder = cheeseOrderRepository.findById(cheeseOrderId).get();

                sendCheeseOrderEvent(validatedOrder, CheeseOrderEventEnum.ALLOCATE_ORDER);

            } else {
                sendCheeseOrderEvent(cheeseOrder, CheeseOrderEventEnum.VALIDATION_FAILED);
            }
        }, () -> log.error("Order Not Found. Id: " + cheeseOrderId));
    }

    @Override
    public void cheeseOrderAllocationPassed(CheeseOrderDto cheeseOrderDto) {
        Optional<CheeseOrder> cheeseOrderOptional = cheeseOrderRepository.findById(cheeseOrderDto.getId());

        cheeseOrderOptional.ifPresentOrElse(cheeseOrder -> {
            sendCheeseOrderEvent(cheeseOrder, CheeseOrderEventEnum.ALLOCATION_SUCCESS);
            awaitForStatus(cheeseOrder.getId(), CheeseOrderStatusEnum.ALLOCATED);
            updateAllocatedQty(cheeseOrderDto);
        }, () -> log.error("Order Id Not Found: " + cheeseOrderDto.getId() ));
    }

    @Override
    public void cheeseOrderAllocationPendingInventory(CheeseOrderDto cheeseOrderDto) {
        Optional<CheeseOrder> cheeseOrderOptional = cheeseOrderRepository.findById(cheeseOrderDto.getId());

        cheeseOrderOptional.ifPresentOrElse(cheeseOrder -> {
            sendCheeseOrderEvent(cheeseOrder, CheeseOrderEventEnum.ALLOCATION_NO_INVENTORY);
            awaitForStatus(cheeseOrder.getId(), CheeseOrderStatusEnum.PENDING_INVENTORY);
            updateAllocatedQty(cheeseOrderDto);
        }, () -> log.error("Order Id Not Found: " + cheeseOrderDto.getId() ));

    }

    private void updateAllocatedQty(CheeseOrderDto cheeseOrderDto) {
        Optional<CheeseOrder> allocatedOrderOptional = cheeseOrderRepository.findById(cheeseOrderDto.getId());

        allocatedOrderOptional.ifPresentOrElse(allocatedOrder -> {
            allocatedOrder.getCheeseOrderLines().forEach(cheeseOrderLine -> {
                cheeseOrderDto.getCheeseOrderLines().forEach(cheeseOrderLineDto -> {
                    if(cheeseOrderLine.getId() .equals(cheeseOrderLineDto.getId())){
                        cheeseOrderLine.setQuantityAllocated(cheeseOrderLineDto.getQuantityAllocated());
                    }
                });
            });

            cheeseOrderRepository.saveAndFlush(allocatedOrder);
        }, () -> log.error("Order Not Found. Id: " + cheeseOrderDto.getId()));
    }

    @Override
    public void cheeseOrderAllocationFailed(CheeseOrderDto cheeseOrderDto) {
        Optional<CheeseOrder> cheeseOrderOptional = cheeseOrderRepository.findById(cheeseOrderDto.getId());

        cheeseOrderOptional.ifPresentOrElse(cheeseOrder -> {
            sendCheeseOrderEvent(cheeseOrder, CheeseOrderEventEnum.ALLOCATION_FAILED);
        }, () -> log.error("Order Not Found. Id: " + cheeseOrderDto.getId()) );

    }

    @Override
    public void cheeseOrderPickedUp(UUID id) {
        Optional<CheeseOrder> cheeseOrderOptional = cheeseOrderRepository.findById(id);

        cheeseOrderOptional.ifPresentOrElse(cheeseOrder -> {
            //do process
            sendCheeseOrderEvent(cheeseOrder, CheeseOrderEventEnum.CHEESEORDER_PICKED_UP);
        }, () -> log.error("Order Not Found. Id: " + id));
    }

    @Override
    public void cancelOrder(UUID id) {
        cheeseOrderRepository.findById(id).ifPresentOrElse(cheeseOrder -> {
            sendCheeseOrderEvent(cheeseOrder, CheeseOrderEventEnum.CANCEL_ORDER);
        }, () -> log.error("Order Not Found. Id: " + id));
    }

    private void sendCheeseOrderEvent(CheeseOrder cheeseOrder, CheeseOrderEventEnum eventEnum){
        StateMachine<CheeseOrderStatusEnum, CheeseOrderEventEnum> sm = build(cheeseOrder);

        Message msg = MessageBuilder.withPayload(eventEnum)
                .setHeader(ORDER_ID_HEADER, cheeseOrder.getId().toString())
                .build();

        sm.sendEvent(msg);
    }

    private void awaitForStatus(UUID cheeseOrderId, CheeseOrderStatusEnum statusEnum) {

        AtomicBoolean found = new AtomicBoolean(false);
        AtomicInteger loopCount = new AtomicInteger(0);

        while (!found.get()) {
            if (loopCount.incrementAndGet() > 10) {
                found.set(true);
                log.debug("Loop Retries exceeded");
            }

            cheeseOrderRepository.findById(cheeseOrderId).ifPresentOrElse(cheeseOrder -> {
                if (cheeseOrder.getOrderStatus().equals(statusEnum)) {
                    found.set(true);
                    log.debug("Order Found");
                } else {
                    log.debug("Order Status Not Equal. Expected: " + statusEnum.name() + " Found: " + cheeseOrder.getOrderStatus().name());
                }
            }, () -> {
                log.debug("Order Id Not Found");
            });

            if (!found.get()) {
                try {
                    log.debug("Sleeping for retry");
                    Thread.sleep(100);
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
    }

    private StateMachine<CheeseOrderStatusEnum, CheeseOrderEventEnum> build(CheeseOrder cheeseOrder){
        StateMachine<CheeseOrderStatusEnum, CheeseOrderEventEnum> sm = stateMachineFactory.getStateMachine(cheeseOrder.getId());

        sm.stop();

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(cheeseOrderStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(cheeseOrder.getOrderStatus(), null, null, null));
                });

        sm.start();

        return sm;
    }
}
