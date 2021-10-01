package com.vneuron.vneuroncheeseservice.services.inventory;

import com.vneuron.vneuroncheeseservice.services.inventory.model.CheeseInventoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


@FeignClient(name = "inventory-failover")
public interface InventoryFailoverFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/inventory-failover")
    ResponseEntity<List<CheeseInventoryDto>> getOnhandInventory();
}
