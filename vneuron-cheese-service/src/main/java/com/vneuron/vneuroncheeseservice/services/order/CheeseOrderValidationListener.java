package com.vneuron.vneuroncheeseservice.services.order;

import com.vneuron.creamery.model.events.ValidateOrderRequest;
import com.vneuron.creamery.model.events.ValidateOrderResult;
import com.vneuron.vneuroncheeseservice.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class CheeseOrderValidationListener {

    private final CheeseOrderValidator validator;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void listen(ValidateOrderRequest validateOrderRequest){
        Boolean isValid = validator.validateOrder(validateOrderRequest.getCheeseOrder());

        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE,
                ValidateOrderResult.builder()
                    .isValid(isValid)
                    .orderId(validateOrderRequest.getCheeseOrder().getId())
                    .build());
    }
}
