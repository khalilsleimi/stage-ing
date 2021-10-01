package com.vneuron.cheese.inventory.service.web.controllers;

import com.vneuron.cheese.inventory.service.repositories.CheeseInventoryRepository;
import com.vneuron.cheese.inventory.service.web.mappers.CheeseInventoryMapper;
import com.vneuron.creamery.model.CheeseInventoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CheeseInventoryController {

    private final CheeseInventoryRepository cheeseInventoryRepository;
    private final CheeseInventoryMapper cheeseInventoryMapper;

    @GetMapping("api/v1/cheese/{cheeseId}/inventory")
    List<CheeseInventoryDto> listCheesesById(@PathVariable UUID cheeseId){
        log.debug("Finding Inventory for cheeseId:" + cheeseId);

        return cheeseInventoryRepository.findAllByCheeseId(cheeseId)
                .stream()
                .map(cheeseInventoryMapper::cheeseInventoryToCheeseInventoryDto)
                .collect(Collectors.toList());
    }
}
