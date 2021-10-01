package com.vneuron.vneuroncheeseservice.web.mappers;

import com.vneuron.creamery.model.CheeseDto;
import com.vneuron.vneuroncheeseservice.domain.Cheese;
import com.vneuron.vneuroncheeseservice.services.inventory.CheeseInventoryService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class CheeseMapperDecorator implements CheeseMapper {
    private CheeseInventoryService cheeseInventoryService;
    private CheeseMapper mapper;

    @Autowired
    public void setCheeseInventoryService(CheeseInventoryService cheeseInventoryService) {
        this.cheeseInventoryService = cheeseInventoryService;
    }

    @Autowired
    public void setMapper(CheeseMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public CheeseDto cheeseToCheeseDto(Cheese cheese) {
       return mapper.cheeseToCheeseDto(cheese);
    }

    @Override
    public CheeseDto cheeseToCheeseDtoWithInventory(Cheese cheese) {
        CheeseDto dto = mapper.cheeseToCheeseDto(cheese);
        dto.setQuantityOnHand(cheeseInventoryService.getOnhandInventory(cheese.getId()));
        return dto;
    }

    @Override
    public Cheese cheeseDtoToCheese(CheeseDto cheeseDto) {
        return mapper.cheeseDtoToCheese(cheeseDto);
    }
}
