package com.vneuron.creamery.inventoryfailover.web;

import com.vneuron.creamery.inventoryfailover.model.CheeseInventoryDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class InventoryHandler {
    public Mono<ServerResponse> listInventory(ServerRequest request){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(Mono.just(Arrays.asList(
                        CheeseInventoryDto.builder()
                            .id(UUID.randomUUID())
                            .cheeseId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                            .quantityOnHand(999)
                            .createdDate(OffsetDateTime.now())
                            .lastModifiedDate(OffsetDateTime.now())
                                .build())), List.class);
    }
}
