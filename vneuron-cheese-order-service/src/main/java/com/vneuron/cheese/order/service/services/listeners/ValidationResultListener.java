package com.vneuron.cheese.order.service.services.listeners;

import com.vneuron.cheese.order.service.config.JmsConfig;
import com.vneuron.creamery.model.events.ValidateOrderResult;
import com.vneuron.cheese.order.service.services.CheeseOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Component
public class ValidationResultListener {

    private final CheeseOrderManager cheeseOrderManager;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE)
    public void listen(ValidateOrderResult result){
        final UUID cheeseOrderId = result.getOrderId();

        log.debug("Validation Result for Order Id: " + cheeseOrderId);

        cheeseOrderManager.processValidationResult(cheeseOrderId, result.getIsValid());
    }
}
