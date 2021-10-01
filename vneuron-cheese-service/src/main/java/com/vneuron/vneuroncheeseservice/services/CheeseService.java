package com.vneuron.vneuroncheeseservice.services;

import com.vneuron.creamery.model.CheeseDto;
import com.vneuron.creamery.model.CheesePagedList;
import com.vneuron.creamery.model.CheeseStyleEnum;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;


public interface CheeseService {
    CheesePagedList listCheeses(String cheeseName, CheeseStyleEnum cheeseStyle, PageRequest pageRequest, Boolean showInventoryOnHand);

    CheeseDto getById(UUID cheeseId, Boolean showInventoryOnHand);

    CheeseDto saveNewCheese(CheeseDto cheeseDto);

    CheeseDto updateCheese(UUID cheeseId, CheeseDto cheeseDto);

    CheeseDto getByUpc(String upc);
}
