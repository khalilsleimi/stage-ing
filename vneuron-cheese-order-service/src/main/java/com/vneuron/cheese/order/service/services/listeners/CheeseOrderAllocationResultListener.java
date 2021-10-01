package com.vneuron.cheese.order.service.services.listeners;

import com.vneuron.cheese.order.service.config.JmsConfig;
import com.vneuron.creamery.model.events.AllocateOrderResult;
import com.vneuron.cheese.order.service.services.CheeseOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class CheeseOrderAllocationResultListener {
    private final CheeseOrderManager cheeseOrderManager;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listen(AllocateOrderResult result){
        if(!result.getAllocationError() && !result.getPendingInventory()){
            //allocated normally
            cheeseOrderManager.cheeseOrderAllocationPassed(result.getCheeseOrderDto());
        } else if(!result.getAllocationError() && result.getPendingInventory()) {
            //pending inventory
            cheeseOrderManager.cheeseOrderAllocationPendingInventory(result.getCheeseOrderDto());
        } else if(result.getAllocationError()){
            //allocation error
            cheeseOrderManager.cheeseOrderAllocationFailed(result.getCheeseOrderDto());
        }
    }

}
