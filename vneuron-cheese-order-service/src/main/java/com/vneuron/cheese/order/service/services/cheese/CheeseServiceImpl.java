package com.vneuron.cheese.order.service.services.cheese;

import com.vneuron.creamery.model.CheeseDto;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;


@ConfigurationProperties(prefix = "sfg.brewery", ignoreUnknownFields = false)
@Service
public class CheeseServiceImpl implements CheeseService {
    public final static String CHEESE_PATH_V1 = "/api/v1/cheese/";
    public final static String CHEESE_UPC_PATH_V1 = "/api/v1/cheeseUpc/";
    private final RestTemplate restTemplate;

    private String cheeseServiceHost;

    public CheeseServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Optional<CheeseDto> getCheeseById(UUID uuid){
        return Optional.of(restTemplate.getForObject(cheeseServiceHost + CHEESE_PATH_V1 + uuid.toString(), CheeseDto.class));
    }

    @Override
    public Optional<CheeseDto> getCheeseByUpc(String upc) {
        return Optional.of(restTemplate.getForObject(cheeseServiceHost + CHEESE_UPC_PATH_V1 + upc, CheeseDto.class));
    }

    public void setCheeseServiceHost(String cheeseServiceHost) {
        this.cheeseServiceHost = cheeseServiceHost;
    }
}
