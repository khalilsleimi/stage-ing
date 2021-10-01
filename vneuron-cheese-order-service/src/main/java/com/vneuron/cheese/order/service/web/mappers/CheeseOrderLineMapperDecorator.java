package com.vneuron.cheese.order.service.web.mappers;

import com.vneuron.cheese.order.service.domain.CheeseOrderLine;
import com.vneuron.creamery.model.CheeseDto;
import com.vneuron.creamery.model.CheeseOrderLineDto;
import com.vneuron.cheese.order.service.services.cheese.CheeseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Optional;


public abstract class CheeseOrderLineMapperDecorator implements CheeseOrderLineMapper {

    private CheeseService cheeseService;
    private CheeseOrderLineMapper cheeseOrderLineMapper;

    @Autowired
    public void setCheeseService(CheeseService cheeseService) {
        this.cheeseService = cheeseService;
    }

    @Autowired
    @Qualifier("delegate")
    public void setCheeseOrderLineMapper(CheeseOrderLineMapper cheeseOrderLineMapper) {
        this.cheeseOrderLineMapper = cheeseOrderLineMapper;
    }

    @Override
    public CheeseOrderLineDto cheeseOrderLineToDto(CheeseOrderLine line) {
        CheeseOrderLineDto orderLineDto = cheeseOrderLineMapper.cheeseOrderLineToDto(line);
        Optional<CheeseDto> cheeseDtoOptional = cheeseService.getCheeseByUpc(line.getUpc());

        cheeseDtoOptional.ifPresent(cheeseDto -> {
            orderLineDto.setCheeseName(cheeseDto.getCheeseName());
            orderLineDto.setCheeseStyle(cheeseDto.getCheeseStyle());
            orderLineDto.setPrice(cheeseDto.getPrice());
            orderLineDto.setCheeseId(cheeseDto.getId());
        });

        return orderLineDto;
    }
}
