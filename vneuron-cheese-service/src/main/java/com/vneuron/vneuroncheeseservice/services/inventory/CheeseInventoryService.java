package com.vneuron.vneuroncheeseservice.services.inventory;

import java.util.UUID;


public interface CheeseInventoryService {

    Integer getOnhandInventory(UUID cheeseId);
}
