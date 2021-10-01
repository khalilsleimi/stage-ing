package com.vneuron.vneuroncheeseservice.services.ripening;

import com.vneuron.creamery.model.events.RipenCheeseEvent;
import com.vneuron.vneuroncheeseservice.config.JmsConfig;
import com.vneuron.vneuroncheeseservice.domain.Cheese;
import com.vneuron.vneuroncheeseservice.repositories.CheeseRepository;
import com.vneuron.vneuroncheeseservice.services.inventory.CheeseInventoryService;
import com.vneuron.vneuroncheeseservice.web.mappers.CheeseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class RipeningService {
    private final CheeseRepository cheeseRepository;
    private final CheeseInventoryService cheeseInventoryService;
    private final JmsTemplate jmsTemplate;
    private final CheeseMapper cheeseMapper;

    @Scheduled(fixedRate = 5000) //every 5 seconds
    public void checkForLowInventory(){
        List<Cheese> cheeses = cheeseRepository.findAll();

        cheeses.forEach(cheese -> {
            Integer invQOH = cheeseInventoryService.getOnhandInventory(cheese.getId());
            log.debug("Checking Inventory for: " + cheese.getCheeseName() + " / " + cheese.getId());
            log.debug("Min Onhand is: " + cheese.getMinOnHand());
            log.debug("Inventory is: "  + invQOH);

            if(cheese.getMinOnHand() >= invQOH){
                jmsTemplate.convertAndSend(JmsConfig.RIPENING_REQUEST_QUEUE, new RipenCheeseEvent(cheeseMapper.cheeseToCheeseDto(cheese)));
            }
        });

    }
}
