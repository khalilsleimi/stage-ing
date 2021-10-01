package com.vneuron.cheese.order.service.web.mappers;

import com.vneuron.cheese.order.service.domain.Customer;
import com.vneuron.creamery.model.CustomerDto;
import org.mapstruct.Mapper;


@Mapper(uses = {DateMapper.class})
public interface CustomerMapper {
    CustomerDto customerToDto(Customer customer);

    Customer dtoToCustomer(Customer dto);
}
