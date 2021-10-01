package com.vneuron.cheese.order.service.services.testcomponets;

import com.vneuron.cheese.order.service.config.JmsConfig;
import com.vneuron.creamery.model.events.ValidateOrderRequest;
import com.vneuron.creamery.model.events.ValidateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class CheeseOrderValidationListener {
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void list(Message msg){
        boolean isValid = true;
        boolean sendResponse = true;

        ValidateOrderRequest request = (ValidateOrderRequest) msg.getPayload();

        //condition to fail validation
        if (request.getCheeseOrder().getCustomerRef() != null) {
            if (request.getCheeseOrder().getCustomerRef().equals("fail-validation")){
                isValid = false;
            } else if (request.getCheeseOrder().getCustomerRef().equals("dont-validate")){
                sendResponse = false;
            }
        }

        if (sendResponse) {
            jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE,
                    ValidateOrderResult.builder()
                            .isValid(isValid)
                            .orderId(request.getCheeseOrder().getId())
                            .build());
        }
    }
}
