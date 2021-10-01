package com.vneuron.vneuroncheeseservice.services.inventory;

import com.vneuron.vneuroncheeseservice.config.FeignClientConfig;
import com.vneuron.vneuroncheeseservice.services.inventory.model.CheeseInventoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;


@FeignClient(name = "inventory-service", fallback = InventoryServiceFeignClientFailover.class, configuration = FeignClientConfig.class)
public interface InventoryServiceFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = CheeseInventoryServiceRestTemplateImpl.INVENTORY_PATH)
    ResponseEntity<List<CheeseInventoryDto>> getOnhandInventory(@PathVariable UUID cheeseId);
}
