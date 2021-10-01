package com.vneuron.vneuroncheeseservice.services.inventory;

import com.vneuron.vneuroncheeseservice.services.inventory.model.CheeseInventoryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Profile("!local-discovery")
@Slf4j
@ConfigurationProperties(prefix = "sfg.creamery", ignoreUnknownFields = true)
@Component
public class CheeseInventoryServiceRestTemplateImpl implements CheeseInventoryService {

    public static final String INVENTORY_PATH = "/api/v1/cheese/{cheeseId}/inventory";
    private final RestTemplate restTemplate;

    private String cheeseInventoryServiceHost;

    public void setCheeseInventoryServiceHost(String cheeseInventoryServiceHost) {
        this.cheeseInventoryServiceHost = cheeseInventoryServiceHost;
    }

    public CheeseInventoryServiceRestTemplateImpl(RestTemplateBuilder restTemplateBuilder,
                                                @Value("${sfg.creamery.inventory-user}") String inventoryUser,
                                                @Value("${sfg.creamery.inventory-password}")String inventoryPassword) {
        this.restTemplate = restTemplateBuilder
                .basicAuthentication(inventoryUser, inventoryPassword)
                .build();
    }

    @Override
    public Integer getOnhandInventory(UUID cheeseId) {

        log.debug("Calling Inventory Service");

        ResponseEntity<List<CheeseInventoryDto>> responseEntity = restTemplate
                .exchange(cheeseInventoryServiceHost + INVENTORY_PATH, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<CheeseInventoryDto>>(){}, (Object) cheeseId);

        //sum from inventory list
        Integer onHand = Objects.requireNonNull(responseEntity.getBody())
                .stream()
                .mapToInt(CheeseInventoryDto::getQuantityOnHand)
                .sum();

        return onHand;
    }
}
