package com.vneuron.cheese.order.service.services.testcomponets;

import com.vneuron.cheese.order.service.config.JmsConfig;
import com.vneuron.creamery.model.events.AllocateOrderRequest;
import com.vneuron.creamery.model.events.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class CheeseOrderAllocationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(Message msg){
        AllocateOrderRequest request = (AllocateOrderRequest) msg.getPayload();
        boolean pendingInventory = false;
        boolean allocationError = false;
        boolean sendResponse = true;

        //set allocation error
        if (request.getCheeseOrderDto().getCustomerRef() != null) {
            if (request.getCheeseOrderDto().getCustomerRef().equals("fail-allocation")){
                allocationError = true;
            }  else if (request.getCheeseOrderDto().getCustomerRef().equals("partial-allocation")) {
                pendingInventory = true;
            } else if (request.getCheeseOrderDto().getCustomerRef().equals("dont-allocate")){
                sendResponse = false;
            }
        }

        boolean finalPendingInventory = pendingInventory;

        request.getCheeseOrderDto().getCheeseOrderLines().forEach(cheeseOrderLineDto -> {
            if (finalPendingInventory) {
                cheeseOrderLineDto.setQuantityAllocated(cheeseOrderLineDto.getOrderQuantity() - 1);
            } else {
                cheeseOrderLineDto.setQuantityAllocated(cheeseOrderLineDto.getOrderQuantity());
            }
        });

        if (sendResponse) {
            jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE,
                    AllocateOrderResult.builder()
                            .cheeseOrderDto(request.getCheeseOrderDto())
                            .pendingInventory(pendingInventory)
                            .allocationError(allocationError)
                            .build());
        }
    }
}
