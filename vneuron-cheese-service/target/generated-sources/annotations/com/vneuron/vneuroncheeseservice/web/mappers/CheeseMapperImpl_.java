package com.vneuron.vneuroncheeseservice.web.mappers;

import com.vneuron.creamery.model.CheeseDto;
import com.vneuron.creamery.model.CheeseDto.CheeseDtoBuilder;
import com.vneuron.creamery.model.CheeseStyleEnum;
import com.vneuron.vneuroncheeseservice.domain.Cheese;
import com.vneuron.vneuroncheeseservice.domain.Cheese.CheeseBuilder;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-09-30T21:32:04+0100",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.12 (Amazon.com Inc.)"
)
@Component
@Qualifier("delegate")
public class CheeseMapperImpl_ implements CheeseMapper {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public CheeseDto cheeseToCheeseDto(Cheese cheese) {
        if ( cheese == null ) {
            return null;
        }

        CheeseDtoBuilder cheeseDto = CheeseDto.builder();

        cheeseDto.id( cheese.getId() );
        if ( cheese.getVersion() != null ) {
            cheeseDto.version( cheese.getVersion().intValue() );
        }
        cheeseDto.createdDate( dateMapper.asOffsetDateTime( cheese.getCreatedDate() ) );
        cheeseDto.lastModifiedDate( dateMapper.asOffsetDateTime( cheese.getLastModifiedDate() ) );
        cheeseDto.cheeseName( cheese.getCheeseName() );
        if ( cheese.getCheeseStyle() != null ) {
            cheeseDto.cheeseStyle( Enum.valueOf( CheeseStyleEnum.class, cheese.getCheeseStyle() ) );
        }
        cheeseDto.upc( cheese.getUpc() );
        cheeseDto.price( cheese.getPrice() );

        return cheeseDto.build();
    }

    @Override
    public CheeseDto cheeseToCheeseDtoWithInventory(Cheese cheese) {
        if ( cheese == null ) {
            return null;
        }

        CheeseDtoBuilder cheeseDto = CheeseDto.builder();

        cheeseDto.id( cheese.getId() );
        if ( cheese.getVersion() != null ) {
            cheeseDto.version( cheese.getVersion().intValue() );
        }
        cheeseDto.createdDate( dateMapper.asOffsetDateTime( cheese.getCreatedDate() ) );
        cheeseDto.lastModifiedDate( dateMapper.asOffsetDateTime( cheese.getLastModifiedDate() ) );
        cheeseDto.cheeseName( cheese.getCheeseName() );
        if ( cheese.getCheeseStyle() != null ) {
            cheeseDto.cheeseStyle( Enum.valueOf( CheeseStyleEnum.class, cheese.getCheeseStyle() ) );
        }
        cheeseDto.upc( cheese.getUpc() );
        cheeseDto.price( cheese.getPrice() );

        return cheeseDto.build();
    }

    @Override
    public Cheese cheeseDtoToCheese(CheeseDto dto) {
        if ( dto == null ) {
            return null;
        }

        CheeseBuilder cheese = Cheese.builder();

        cheese.id( dto.getId() );
        if ( dto.getVersion() != null ) {
            cheese.version( dto.getVersion().longValue() );
        }
        cheese.createdDate( dateMapper.asTimestamp( dto.getCreatedDate() ) );
        cheese.lastModifiedDate( dateMapper.asTimestamp( dto.getLastModifiedDate() ) );
        cheese.cheeseName( dto.getCheeseName() );
        if ( dto.getCheeseStyle() != null ) {
            cheese.cheeseStyle( dto.getCheeseStyle().name() );
        }
        cheese.upc( dto.getUpc() );
        cheese.price( dto.getPrice() );

        return cheese.build();
    }
}
