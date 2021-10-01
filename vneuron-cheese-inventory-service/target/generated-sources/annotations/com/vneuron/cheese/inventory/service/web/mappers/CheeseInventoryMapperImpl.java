package com.vneuron.cheese.inventory.service.web.mappers;

import com.vneuron.cheese.inventory.service.domain.CheeseInventory;
import com.vneuron.cheese.inventory.service.domain.CheeseInventory.CheeseInventoryBuilder;
import com.vneuron.creamery.model.CheeseInventoryDto;
import com.vneuron.creamery.model.CheeseInventoryDto.CheeseInventoryDtoBuilder;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-09-30T21:45:40+0100",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.12 (Amazon.com Inc.)"
)
@Component
public class CheeseInventoryMapperImpl implements CheeseInventoryMapper {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public CheeseInventory cheeseInventoryDtoToCheeseInventory(CheeseInventoryDto cheeseInventoryDTO) {
        if ( cheeseInventoryDTO == null ) {
            return null;
        }

        CheeseInventoryBuilder cheeseInventory = CheeseInventory.builder();

        cheeseInventory.id( cheeseInventoryDTO.getId() );
        cheeseInventory.createdDate( dateMapper.asTimestamp( cheeseInventoryDTO.getCreatedDate() ) );
        cheeseInventory.lastModifiedDate( dateMapper.asTimestamp( cheeseInventoryDTO.getLastModifiedDate() ) );
        cheeseInventory.cheeseId( cheeseInventoryDTO.getCheeseId() );
        cheeseInventory.upc( cheeseInventoryDTO.getUpc() );
        cheeseInventory.quantityOnHand( cheeseInventoryDTO.getQuantityOnHand() );

        return cheeseInventory.build();
    }

    @Override
    public CheeseInventoryDto cheeseInventoryToCheeseInventoryDto(CheeseInventory cheeseInventory) {
        if ( cheeseInventory == null ) {
            return null;
        }

        CheeseInventoryDtoBuilder cheeseInventoryDto = CheeseInventoryDto.builder();

        cheeseInventoryDto.id( cheeseInventory.getId() );
        cheeseInventoryDto.createdDate( dateMapper.asOffsetDateTime( cheeseInventory.getCreatedDate() ) );
        cheeseInventoryDto.lastModifiedDate( dateMapper.asOffsetDateTime( cheeseInventory.getLastModifiedDate() ) );
        cheeseInventoryDto.cheeseId( cheeseInventory.getCheeseId() );
        cheeseInventoryDto.upc( cheeseInventory.getUpc() );
        cheeseInventoryDto.quantityOnHand( cheeseInventory.getQuantityOnHand() );

        return cheeseInventoryDto.build();
    }
}
