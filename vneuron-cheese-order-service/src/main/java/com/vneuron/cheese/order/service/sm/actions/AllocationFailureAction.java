package com.vneuron.cheese.order.service.sm.actions;

import com.vneuron.cheese.order.service.config.JmsConfig;
import com.vneuron.cheese.order.service.domain.CheeseOrderEventEnum;
import com.vneuron.cheese.order.service.domain.CheeseOrderStatusEnum;
import com.vneuron.cheese.order.service.services.CheeseOrderManagerImpl;
import com.vneuron.creamery.model.events.AllocationFailureEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationFailureAction implements Action<CheeseOrderStatusEnum, CheeseOrderEventEnum> {

    private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<CheeseOrderStatusEnum, CheeseOrderEventEnum> context) {
        String cheeseOrderId = (String) context.getMessage().getHeaders().get(CheeseOrderManagerImpl.ORDER_ID_HEADER);

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_FAILURE_QUEUE, AllocationFailureEvent.builder()
            .orderId(UUID.fromString(cheeseOrderId))
                    .build());

        log.debug("Sent Allocation Failure Message to queue for order id " + cheeseOrderId);
    }
}