package com.vneuron.cheese.order.service.sm.actions;

import com.vneuron.cheese.order.service.domain.CheeseOrderEventEnum;
import com.vneuron.cheese.order.service.domain.CheeseOrderStatusEnum;
import com.vneuron.cheese.order.service.services.CheeseOrderManagerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class ValidationFailureAction implements Action<CheeseOrderStatusEnum, CheeseOrderEventEnum> {

    @Override
    public void execute(StateContext<CheeseOrderStatusEnum, CheeseOrderEventEnum> context) {
        String cheeseOrderId = (String) context.getMessage().getHeaders().get(CheeseOrderManagerImpl.ORDER_ID_HEADER);
        log.error("Compensating Transaction.... Validation Failed: " + cheeseOrderId);
    }
}
