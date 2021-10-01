/*
 *  Copyright 2019 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.vneuron.cheese.order.service.services;

import com.vneuron.cheese.order.service.domain.CheeseOrder;
import com.vneuron.cheese.order.service.domain.CheeseOrderStatusEnum;
import com.vneuron.cheese.order.service.domain.Customer;
import com.vneuron.cheese.order.service.repositories.CheeseOrderRepository;
import com.vneuron.cheese.order.service.repositories.CustomerRepository;
import com.vneuron.cheese.order.service.web.mappers.CheeseOrderMapper;
import com.vneuron.creamery.model.CheeseOrderDto;
import com.vneuron.creamery.model.CheeseOrderPagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheeseOrderServiceImpl implements CheeseOrderService {

    private final CheeseOrderRepository cheeseOrderRepository;
    private final CustomerRepository customerRepository;
    private final CheeseOrderMapper cheeseOrderMapper;
    private final CheeseOrderManager cheeseOrderManager;

    @Override
    public CheeseOrderPagedList listOrders(UUID customerId, Pageable pageable) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Page<CheeseOrder> cheeseOrderPage =
                    cheeseOrderRepository.findAllByCustomer(customerOptional.get(), pageable);

            return new CheeseOrderPagedList(cheeseOrderPage
                    .stream()
                    .map(cheeseOrderMapper::cheeseOrderToDto)
                    .collect(Collectors.toList()), PageRequest.of(
                    cheeseOrderPage.getPageable().getPageNumber(),
                    cheeseOrderPage.getPageable().getPageSize()),
                    cheeseOrderPage.getTotalElements());
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public CheeseOrderDto placeOrder(UUID customerId, CheeseOrderDto cheeseOrderDto) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            CheeseOrder cheeseOrder = cheeseOrderMapper.dtoToCheeseOrder(cheeseOrderDto);
            cheeseOrder.setId(null); //should not be set by outside client
            cheeseOrder.setCustomer(customerOptional.get());
            cheeseOrder.setOrderStatus(CheeseOrderStatusEnum.NEW);

            cheeseOrder.getCheeseOrderLines().forEach(line -> line.setCheeseOrder(cheeseOrder));

            CheeseOrder savedCheeseOrder = cheeseOrderManager.newCheeseOrder(cheeseOrder);

            log.debug("Saved Cheese Order: " + cheeseOrder.getId());

            return cheeseOrderMapper.cheeseOrderToDto(savedCheeseOrder);
        }
        //todo add exception type
        throw new RuntimeException("Customer Not Found");
    }

    @Override
    public CheeseOrderDto getOrderById(UUID customerId, UUID orderId) {
        return cheeseOrderMapper.cheeseOrderToDto(getOrder(customerId, orderId));
    }

    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {
        cheeseOrderManager.cheeseOrderPickedUp(orderId);
    }

    private CheeseOrder getOrder(UUID customerId, UUID orderId){
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if(customerOptional.isPresent()){
            Optional<CheeseOrder> cheeseOrderOptional = cheeseOrderRepository.findById(orderId);

            if(cheeseOrderOptional.isPresent()){
                CheeseOrder cheeseOrder = cheeseOrderOptional.get();

                // fall to exception if customer id's do not match - order not for customer
                if(cheeseOrder.getCustomer().getId().equals(customerId)){
                    return cheeseOrder;
                }
            }
            throw new RuntimeException("Cheese Order Not Found");
        }
        throw new RuntimeException("Customer Not Found");
    }
}
