package com.vneuron.vneuroncheeseservice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vneuron.creamery.model.CheeseDto;
import com.vneuron.creamery.model.CheeseStyleEnum;
import com.vneuron.vneuroncheeseservice.bootstrap.CheeseLoader;
import com.vneuron.vneuroncheeseservice.services.CheeseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CheeseController.class)
class CheeseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CheeseService cheeseService;

    @Test
    void getCheeseById() throws Exception {

        given(cheeseService.getById(any(), anyBoolean())).willReturn(getValidCheeseDto());

        mockMvc.perform(get("/api/v1/cheese/" + UUID.randomUUID().toString()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void saveNewCheese() throws Exception {

        CheeseDto cheeseDto = getValidCheeseDto();
        String cheeseDtoJson = objectMapper.writeValueAsString(cheeseDto);

        given(cheeseService.saveNewCheese(any())).willReturn(getValidCheeseDto());

        mockMvc.perform(post("/api/v1/cheese/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cheeseDtoJson))
                .andExpect(status().isCreated());
    }

    @Test
    void updateCheeseById() throws Exception {
        given(cheeseService.updateCheese(any(), any())).willReturn(getValidCheeseDto());

        CheeseDto cheeseDto = getValidCheeseDto();
        String cheeseDtoJson = objectMapper.writeValueAsString(cheeseDto);

        mockMvc.perform(put("/api/v1/cheese/" + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(cheeseDtoJson))
                .andExpect(status().isNoContent());
    }

    CheeseDto getValidCheeseDto(){
        return CheeseDto.builder()
                .cheeseName("My Cheese")
                .cheeseStyle(CheeseStyleEnum.ALE)
                .price(new BigDecimal("2.99"))
                .upc(CheeseLoader.CHEESE_1_UPC)
                .build();
    }
}