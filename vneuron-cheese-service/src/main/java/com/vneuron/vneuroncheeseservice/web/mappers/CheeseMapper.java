package com.vneuron.vneuroncheeseservice.web.mappers;

import com.vneuron.creamery.model.CheeseDto;
import com.vneuron.vneuroncheeseservice.domain.Cheese;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(CheeseMapperDecorator.class)
public interface CheeseMapper {

    CheeseDto cheeseToCheeseDto(Cheese cheese);

    CheeseDto cheeseToCheeseDtoWithInventory(Cheese cheese);

    Cheese cheeseDtoToCheese(CheeseDto dto);
}
