package com.vneuron.cheese.order.service.services;

import com.vneuron.cheese.order.service.domain.CheeseOrder;
import com.vneuron.creamery.model.CheeseOrderDto;

import java.util.UUID;


public interface CheeseOrderManager {

    CheeseOrder newCheeseOrder(CheeseOrder cheeseOrder);

    void processValidationResult(UUID cheeseOrderId, Boolean isValid);

    void cheeseOrderAllocationPassed(CheeseOrderDto cheeseOrder);

    void cheeseOrderAllocationPendingInventory(CheeseOrderDto cheeseOrder);

    void cheeseOrderAllocationFailed(CheeseOrderDto cheeseOrder);

    void cheeseOrderPickedUp(UUID id);

    void cancelOrder(UUID id);
}
