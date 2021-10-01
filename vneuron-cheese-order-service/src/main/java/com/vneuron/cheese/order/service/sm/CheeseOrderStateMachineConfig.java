package com.vneuron.cheese.order.service.sm;

import com.vneuron.cheese.order.service.domain.CheeseOrderEventEnum;
import com.vneuron.cheese.order.service.domain.CheeseOrderStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;


@RequiredArgsConstructor
@Configuration
@EnableStateMachineFactory
public class CheeseOrderStateMachineConfig extends StateMachineConfigurerAdapter<CheeseOrderStatusEnum, CheeseOrderEventEnum> {

    private final Action<CheeseOrderStatusEnum, CheeseOrderEventEnum>  validateOrderAction;
    private final Action<CheeseOrderStatusEnum, CheeseOrderEventEnum>  allocateOrderAction;
    private final Action<CheeseOrderStatusEnum, CheeseOrderEventEnum>  validationFailureAction;
    private final Action<CheeseOrderStatusEnum, CheeseOrderEventEnum>  allocationFailureAction;
    private final Action<CheeseOrderStatusEnum, CheeseOrderEventEnum>  deallocateOrderAction;

    @Override
    public void configure(StateMachineStateConfigurer<CheeseOrderStatusEnum, CheeseOrderEventEnum> states) throws Exception {
        states.withStates()
                .initial(CheeseOrderStatusEnum.NEW)
                .states(EnumSet.allOf(CheeseOrderStatusEnum.class))
                .end(CheeseOrderStatusEnum.PICKED_UP)
                .end(CheeseOrderStatusEnum.DELIVERED)
                .end(CheeseOrderStatusEnum.CANCELLED)
                .end(CheeseOrderStatusEnum.DELIVERY_EXCEPTION)
                .end(CheeseOrderStatusEnum.VALIDATION_EXCEPTION)
                .end(CheeseOrderStatusEnum.ALLOCATION_EXCEPTION);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<CheeseOrderStatusEnum, CheeseOrderEventEnum> transitions) throws Exception {
        transitions.withExternal()
                .source(CheeseOrderStatusEnum.NEW).target(CheeseOrderStatusEnum.VALIDATION_PENDING)
                .event(CheeseOrderEventEnum.VALIDATE_ORDER)
                .action(validateOrderAction)
           .and().withExternal()
                .source(CheeseOrderStatusEnum.VALIDATION_PENDING).target(CheeseOrderStatusEnum.VALIDATED)
                .event(CheeseOrderEventEnum.VALIDATION_PASSED)
           .and().withExternal()
                .source(CheeseOrderStatusEnum.VALIDATION_PENDING).target(CheeseOrderStatusEnum.CANCELLED)
                .event(CheeseOrderEventEnum.CANCEL_ORDER)
           .and().withExternal()
                .source(CheeseOrderStatusEnum.VALIDATION_PENDING).target(CheeseOrderStatusEnum.VALIDATION_EXCEPTION)
                .event(CheeseOrderEventEnum.VALIDATION_FAILED)
                .action(validationFailureAction)
            .and().withExternal()
                .source(CheeseOrderStatusEnum.VALIDATED).target(CheeseOrderStatusEnum.ALLOCATION_PENDING)
                .event(CheeseOrderEventEnum.ALLOCATE_ORDER)
                .action(allocateOrderAction)
            .and().withExternal()
                .source(CheeseOrderStatusEnum.VALIDATED).target(CheeseOrderStatusEnum.CANCELLED)
                .event(CheeseOrderEventEnum.CANCEL_ORDER)
            .and().withExternal()
                .source(CheeseOrderStatusEnum.ALLOCATION_PENDING).target(CheeseOrderStatusEnum.ALLOCATED)
                .event(CheeseOrderEventEnum.ALLOCATION_SUCCESS)
            .and().withExternal()
                .source(CheeseOrderStatusEnum.ALLOCATION_PENDING).target(CheeseOrderStatusEnum.ALLOCATION_EXCEPTION)
                .event(CheeseOrderEventEnum.ALLOCATION_FAILED)
                .action(allocationFailureAction)
            .and().withExternal()
                .source(CheeseOrderStatusEnum.ALLOCATION_PENDING).target(CheeseOrderStatusEnum.CANCELLED)
                .event(CheeseOrderEventEnum.CANCEL_ORDER)
                .and().withExternal()
                .source(CheeseOrderStatusEnum.ALLOCATION_PENDING).target(CheeseOrderStatusEnum.PENDING_INVENTORY)
                .event(CheeseOrderEventEnum.ALLOCATION_NO_INVENTORY)
           .and().withExternal()
                .source(CheeseOrderStatusEnum.ALLOCATED).target(CheeseOrderStatusEnum.PICKED_UP)
                .event(CheeseOrderEventEnum.CHEESEORDER_PICKED_UP)
           .and().withExternal()
                .source(CheeseOrderStatusEnum.ALLOCATED).target(CheeseOrderStatusEnum.CANCELLED)
                .event(CheeseOrderEventEnum.CANCEL_ORDER)
                .action(deallocateOrderAction);
    }
}
