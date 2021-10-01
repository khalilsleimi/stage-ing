package com.vneuron.cheese.order.service.sm.actions;

import com.vneuron.cheese.order.service.config.JmsConfig;
import com.vneuron.cheese.order.service.domain.CheeseOrder;
import com.vneuron.cheese.order.service.domain.CheeseOrderEventEnum;
import com.vneuron.cheese.order.service.domain.CheeseOrderStatusEnum;
import com.vneuron.cheese.order.service.repositories.CheeseOrderRepository;
import com.vneuron.cheese.order.service.services.CheeseOrderManagerImpl;
import com.vneuron.cheese.order.service.web.mappers.CheeseOrderMapper;
import com.vneuron.creamery.model.events.ValidateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateOrderAction implements Action<CheeseOrderStatusEnum, CheeseOrderEventEnum> {

    private final CheeseOrderRepository cheeseOrderRepository;
    private final CheeseOrderMapper cheeseOrderMapper;
    private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<CheeseOrderStatusEnum, CheeseOrderEventEnum> context) {
        String cheeseOrderId = (String) context.getMessage().getHeaders().get(CheeseOrderManagerImpl.ORDER_ID_HEADER);
        Optional<CheeseOrder> cheeseOrderOptional = cheeseOrderRepository.findById(UUID.fromString(cheeseOrderId));

        cheeseOrderOptional.ifPresentOrElse(cheeseOrder -> {
            jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_QUEUE, ValidateOrderRequest.builder()
                    .cheeseOrder(cheeseOrderMapper.cheeseOrderToDto(cheeseOrder))
                    .build());
        }, () -> log.error("Order Not Found. Id: " + cheeseOrderId));

        log.debug("Sent Validation request to queue for order id " + cheeseOrderId);
    }
}
