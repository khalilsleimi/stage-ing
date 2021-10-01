package com.vneuron.cheese.inventory.service.services;

import com.vneuron.cheese.inventory.service.repositories.CheeseInventoryRepository;
import com.vneuron.cheese.inventory.service.config.JmsConfig;
import com.vneuron.cheese.inventory.service.domain.CheeseInventory;
import com.vneuron.creamery.model.events.NewInventoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NewInventoryListener {

    private final CheeseInventoryRepository cheeseInventoryRepository;

    @JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE)
    public void listen(NewInventoryEvent event){

        log.debug("Got Inventory: " + event.toString());

        cheeseInventoryRepository.save(CheeseInventory.builder()
                .cheeseId(event.getCheeseDto().getId())
                .upc(event.getCheeseDto().getUpc())
                .quantityOnHand(event.getCheeseDto().getQuantityOnHand())
                .build());
    }

}
