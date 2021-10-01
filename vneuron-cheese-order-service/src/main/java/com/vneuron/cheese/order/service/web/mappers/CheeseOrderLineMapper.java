package com.vneuron.cheese.order.service.web.mappers;

import com.vneuron.cheese.order.service.domain.CheeseOrderLine;
import com.vneuron.creamery.model.CheeseOrderLineDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(CheeseOrderLineMapperDecorator.class)
public interface CheeseOrderLineMapper {
    CheeseOrderLineDto cheeseOrderLineToDto(CheeseOrderLine line);

    CheeseOrderLine dtoToCheeseOrderLine(CheeseOrderLineDto dto);
}
