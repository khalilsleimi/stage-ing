package com.vneuron.cheese.inventory.service.services;


import com.vneuron.creamery.model.CheeseOrderDto;

public interface AllocationService {

    Boolean allocateOrder(CheeseOrderDto cheeseOrderDto);

    void deallocateOrder(CheeseOrderDto cheeseOrderDto);
}
