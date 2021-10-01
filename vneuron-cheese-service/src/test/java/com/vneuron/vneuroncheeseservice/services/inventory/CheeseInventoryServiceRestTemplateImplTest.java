package com.vneuron.vneuroncheeseservice.services.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled // utility for manual testing
@SpringBootTest
class CheeseInventoryServiceRestTemplateImplTest {

    @Autowired
    CheeseInventoryService cheeseInventoryService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void getOnhandInventory() {

        //todo evolve to use UPC
      //  Integer qoh = cheeseInventoryService.getOnhandInventory(CheeseLoader.CHEESE_1_UUID);

        //System.out.println(qoh);

    }
}