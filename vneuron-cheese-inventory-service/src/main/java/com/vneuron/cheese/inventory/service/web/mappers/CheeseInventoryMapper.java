package com.vneuron.cheese.inventory.service.web.mappers;

import com.vneuron.cheese.inventory.service.domain.CheeseInventory;
import com.vneuron.creamery.model.CheeseInventoryDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface CheeseInventoryMapper {

    CheeseInventory cheeseInventoryDtoToCheeseInventory(CheeseInventoryDto cheeseInventoryDTO);

    CheeseInventoryDto cheeseInventoryToCheeseInventoryDto(CheeseInventory cheeseInventory);
}
