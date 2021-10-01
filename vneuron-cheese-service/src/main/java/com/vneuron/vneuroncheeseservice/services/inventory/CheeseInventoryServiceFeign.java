package com.vneuron.vneuroncheeseservice.services.inventory;

import com.vneuron.vneuroncheeseservice.services.inventory.model.CheeseInventoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Profile("local-discovery")
@Service
public class CheeseInventoryServiceFeign implements CheeseInventoryService {

    private final InventoryServiceFeignClient inventoryServiceFeignClient;

    @Override
    public Integer getOnhandInventory(UUID cheeseId) {
        log.debug("Calling Inventory Service - CheeseId: " + cheeseId);

        ResponseEntity<List<CheeseInventoryDto>> responseEntity = inventoryServiceFeignClient.getOnhandInventory(cheeseId);

        Integer onHand = Objects.requireNonNull(responseEntity.getBody())
                .stream()
                .mapToInt(CheeseInventoryDto::getQuantityOnHand)
                .sum();

        log.debug("CheeseId: " + cheeseId + " On hand is: " + onHand);

        return onHand;
    }
}
