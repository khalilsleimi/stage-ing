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

package com.vneuron.cheese.order.service.web.controllers;

import com.vneuron.creamery.model.CheeseOrderDto;
import com.vneuron.creamery.model.CheeseOrderPagedList;
import com.vneuron.cheese.order.service.services.CheeseOrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/customers/{customerId}/")
@RestController
public class CheeseOrderController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final CheeseOrderService cheeseOrderService;

    public CheeseOrderController(CheeseOrderService cheeseOrderService) {
        this.cheeseOrderService = cheeseOrderService;
    }

    @GetMapping("orders")
    public CheeseOrderPagedList listOrders(@PathVariable("customerId") UUID customerId,
                                           @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                           @RequestParam(value = "pageSize", required = false) Integer pageSize){

        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return cheeseOrderService.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
    }

    @PostMapping("orders")
    @ResponseStatus(HttpStatus.CREATED)
    public CheeseOrderDto placeOrder(@PathVariable("customerId") UUID customerId, @RequestBody CheeseOrderDto cheeseOrderDto){
        return cheeseOrderService.placeOrder(customerId, cheeseOrderDto);
    }

    @GetMapping("orders/{orderId}")
    public CheeseOrderDto getOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId){
        return cheeseOrderService.getOrderById(customerId, orderId);
    }

    @PutMapping("/orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId){
        cheeseOrderService.pickupOrder(customerId, orderId);
    }
}
