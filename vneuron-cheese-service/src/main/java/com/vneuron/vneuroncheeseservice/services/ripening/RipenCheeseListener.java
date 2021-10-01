package com.vneuron.vneuroncheeseservice.services.ripening;

import com.vneuron.creamery.model.CheeseDto;
import com.vneuron.creamery.model.events.NewInventoryEvent;
import com.vneuron.creamery.model.events.RipenCheeseEvent;
import com.vneuron.vneuroncheeseservice.config.JmsConfig;
import com.vneuron.vneuroncheeseservice.domain.Cheese;
import com.vneuron.vneuroncheeseservice.repositories.CheeseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class RipenCheeseListener {

    private final CheeseRepository cheeseRepository;
    private final JmsTemplate jmsTemplate;

    @Transactional
    @JmsListener(destination = JmsConfig.RIPENING_REQUEST_QUEUE)
    public void listen(RipenCheeseEvent event){
        CheeseDto cheeseDto = event.getCheeseDto();

        Cheese cheese = cheeseRepository.getOne(cheeseDto.getId());

        cheeseDto.setQuantityOnHand(cheese.getQuantityToRipen());

        NewInventoryEvent newInventoryEvent = new NewInventoryEvent(cheeseDto);

        log.debug("Ripened cheese " + cheese.getMinOnHand() + " : QOH: " + cheeseDto.getQuantityOnHand());

        jmsTemplate.convertAndSend(JmsConfig.NEW_INVENTORY_QUEUE, newInventoryEvent);
    }
}
