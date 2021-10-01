package com.vneuron.cheese.order.service.services.cheese;

import com.vneuron.creamery.model.CheeseDto;

import java.util.Optional;
import java.util.UUID;

public interface CheeseService {

    Optional<CheeseDto> getCheeseById(UUID uuid);

    Optional<CheeseDto> getCheeseByUpc(String upc);
}
