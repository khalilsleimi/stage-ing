package com.vneuron.vneuroncheeseservice.services.inventory;

import com.vneuron.vneuroncheeseservice.services.inventory.model.CheeseInventoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@Component
public class InventoryServiceFeignClientFailover implements InventoryServiceFeignClient {

    private final InventoryFailoverFeignClient failoverFeignClient;

    @Override
    public ResponseEntity<List<CheeseInventoryDto>> getOnhandInventory(UUID cheeseId) {
        return failoverFeignClient.getOnhandInventory();
    }
}
