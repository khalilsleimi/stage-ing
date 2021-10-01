package com.vneuron.cheese.order.service.services;

import com.vneuron.creamery.model.CustomerPagedList;
import org.springframework.data.domain.Pageable;


public interface CustomerService {

    CustomerPagedList listCustomers(Pageable pageable);

}
