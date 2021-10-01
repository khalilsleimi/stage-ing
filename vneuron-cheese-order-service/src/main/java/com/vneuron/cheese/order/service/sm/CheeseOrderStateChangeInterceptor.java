package com.vneuron.cheese.order.service.sm;

import com.vneuron.cheese.order.service.domain.CheeseOrder;
import com.vneuron.cheese.order.service.domain.CheeseOrderEventEnum;
import com.vneuron.cheese.order.service.domain.CheeseOrderStatusEnum;
import com.vneuron.cheese.order.service.repositories.CheeseOrderRepository;
import com.vneuron.cheese.order.service.services.CheeseOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class CheeseOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<CheeseOrderStatusEnum, CheeseOrderEventEnum> {

    private final CheeseOrderRepository cheeseOrderRepository;

    @Transactional
    @Override
    public void preStateChange(State<CheeseOrderStatusEnum, CheeseOrderEventEnum> state, Message<CheeseOrderEventEnum> message, Transition<CheeseOrderStatusEnum, CheeseOrderEventEnum> transition, StateMachine<CheeseOrderStatusEnum, CheeseOrderEventEnum> stateMachine) {
        log.debug("Pre-State Change");

        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().getOrDefault(CheeseOrderManagerImpl.ORDER_ID_HEADER, " ")))
                .ifPresent(orderId -> {
                    log.debug("Saving state for order id: " + orderId + " Status: " + state.getId());

                    CheeseOrder cheeseOrder = cheeseOrderRepository.getOne(UUID.fromString(orderId));
                    cheeseOrder.setOrderStatus(state.getId());
                    cheeseOrderRepository.saveAndFlush(cheeseOrder);
                });
    }
}
