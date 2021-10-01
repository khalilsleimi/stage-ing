package com.vneuron.cheese.order.service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.vneuron.creamery.model.CheeseDto;
import com.vneuron.creamery.model.events.AllocationFailureEvent;
import com.vneuron.creamery.model.events.DeallocateOrderRequest;
import com.vneuron.cheese.order.service.config.JmsConfig;
import com.vneuron.cheese.order.service.domain.CheeseOrder;
import com.vneuron.cheese.order.service.domain.CheeseOrderLine;
import com.vneuron.cheese.order.service.domain.CheeseOrderStatusEnum;
import com.vneuron.cheese.order.service.domain.Customer;
import com.vneuron.cheese.order.service.repositories.CheeseOrderRepository;
import com.vneuron.cheese.order.service.repositories.CustomerRepository;
import com.vneuron.cheese.order.service.services.cheese.CheeseServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.jgroups.util.Util.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(WireMockExtension.class)
@SpringBootTest
public class CheeseOrderManagerImplIT {

    @Autowired
    CheeseOrderManager cheeseOrderManager;

    @Autowired
    CheeseOrderRepository cheeseOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WireMockServer wireMockServer;

    @Autowired
    JmsTemplate jmsTemplate;

    Customer testCustomer;

    UUID cheeseId = UUID.randomUUID();

    @TestConfiguration
    static class RestTemplateBuilderProvider {
        @Bean(destroyMethod = "stop")
        public WireMockServer wireMockServer(){
            WireMockServer server = with(wireMockConfig().port(8083));
            server.start();
            return server;
        }
    }

    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.save(Customer.builder()
                .customerName("Test Customer")
                .build());
    }

    @Test
    void testNewToAllocated() throws JsonProcessingException, InterruptedException {
        CheeseDto cheeseDto = CheeseDto.builder().id(cheeseId).upc("12345").build();

        wireMockServer.stubFor(get(CheeseServiceImpl.CHEESE_UPC_PATH_V1 + "12345")
        .willReturn(okJson(objectMapper.writeValueAsString(cheeseDto))));

        CheeseOrder cheeseOrder = createCheeseOrder();

        CheeseOrder savedCheeseOrder = cheeseOrderManager.newCheeseOrder(cheeseOrder);

        await().untilAsserted(() -> {
            CheeseOrder foundOrder = cheeseOrderRepository.findById(cheeseOrder.getId()).get();

            org.junit.jupiter.api.Assertions.assertEquals(CheeseOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
        });

        await().untilAsserted(() -> {
            CheeseOrder foundOrder = cheeseOrderRepository.findById(cheeseOrder.getId()).get();
            CheeseOrderLine line = foundOrder.getCheeseOrderLines().iterator().next();
            assertEquals(line.getOrderQuantity(), line.getQuantityAllocated());
        });

        CheeseOrder savedCheeseOrder2 = cheeseOrderRepository.findById(savedCheeseOrder.getId()).get();

        assertNotNull(savedCheeseOrder2);
        assertEquals(CheeseOrderStatusEnum.ALLOCATED, savedCheeseOrder2.getOrderStatus());
        savedCheeseOrder2.getCheeseOrderLines().forEach(line -> {
            assertEquals(line.getOrderQuantity(), line.getQuantityAllocated());
        });
    }

    @Test
    void testFailedValidation() throws JsonProcessingException {
        CheeseDto cheeseDto = CheeseDto.builder().id(cheeseId).upc("12345").build();

        wireMockServer.stubFor(get(CheeseServiceImpl.CHEESE_UPC_PATH_V1 + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(cheeseDto))));

        CheeseOrder cheeseOrder = createCheeseOrder();
        cheeseOrder.setCustomerRef("fail-validation");

        CheeseOrder savedCheeseOrder = cheeseOrderManager.newCheeseOrder(cheeseOrder);

        await().untilAsserted(() -> {
            CheeseOrder foundOrder = cheeseOrderRepository.findById(cheeseOrder.getId()).get();

            assertEquals(CheeseOrderStatusEnum.VALIDATION_EXCEPTION, foundOrder.getOrderStatus());
        });
    }

    @Test
    void testNewToPickedUp() throws JsonProcessingException {
        CheeseDto cheeseDto = CheeseDto.builder().id(cheeseId).upc("12345").build();

        wireMockServer.stubFor(get(CheeseServiceImpl.CHEESE_UPC_PATH_V1 + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(cheeseDto))));

        CheeseOrder cheeseOrder = createCheeseOrder();

        CheeseOrder savedCheeseOrder = cheeseOrderManager.newCheeseOrder(cheeseOrder);

        await().untilAsserted(() -> {
            CheeseOrder foundOrder = cheeseOrderRepository.findById(cheeseOrder.getId()).get();
            assertEquals(CheeseOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
        });

        cheeseOrderManager.cheeseOrderPickedUp(savedCheeseOrder.getId());

        await().untilAsserted(() -> {
            CheeseOrder foundOrder = cheeseOrderRepository.findById(cheeseOrder.getId()).get();
            assertEquals(CheeseOrderStatusEnum.PICKED_UP, foundOrder.getOrderStatus());
        });

        CheeseOrder pickedUpOrder = cheeseOrderRepository.findById(savedCheeseOrder.getId()).get();

        assertEquals(CheeseOrderStatusEnum.PICKED_UP, pickedUpOrder.getOrderStatus());
    }

    @Test
    void testAllocationFailure() throws JsonProcessingException {
        CheeseDto cheeseDto = CheeseDto.builder().id(cheeseId).upc("12345").build();

        wireMockServer.stubFor(get(CheeseServiceImpl.CHEESE_UPC_PATH_V1 + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(cheeseDto))));

        CheeseOrder cheeseOrder = createCheeseOrder();
        cheeseOrder.setCustomerRef("fail-allocation");

        CheeseOrder savedCheeseOrder = cheeseOrderManager.newCheeseOrder(cheeseOrder);

        await().untilAsserted(() -> {
            CheeseOrder foundOrder = cheeseOrderRepository.findById(cheeseOrder.getId()).get();
            assertEquals(CheeseOrderStatusEnum.ALLOCATION_EXCEPTION, foundOrder.getOrderStatus());
        });

        AllocationFailureEvent allocationFailureEvent = (AllocationFailureEvent) jmsTemplate.receiveAndConvert(JmsConfig.ALLOCATE_FAILURE_QUEUE);

        assertNotNull(allocationFailureEvent);
        assertThat(allocationFailureEvent.getOrderId()).isEqualTo(savedCheeseOrder.getId());
    }

    @Test
    void testPartialAllocation() throws JsonProcessingException {
        CheeseDto cheeseDto = CheeseDto.builder().id(cheeseId).upc("12345").build();

        wireMockServer.stubFor(get(CheeseServiceImpl.CHEESE_UPC_PATH_V1 + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(cheeseDto))));

        CheeseOrder cheeseOrder = createCheeseOrder();
        cheeseOrder.setCustomerRef("partial-allocation");

        CheeseOrder savedCheeseOrder = cheeseOrderManager.newCheeseOrder(cheeseOrder);

        await().untilAsserted(() -> {
            CheeseOrder foundOrder = cheeseOrderRepository.findById(cheeseOrder.getId()).get();
            assertEquals(CheeseOrderStatusEnum.PENDING_INVENTORY, foundOrder.getOrderStatus());
        });
    }

    @Test
    void testValidationPendingToCancel() throws JsonProcessingException {
        CheeseDto cheeseDto = CheeseDto.builder().id(cheeseId).upc("12345").build();

        wireMockServer.stubFor(get(CheeseServiceImpl.CHEESE_UPC_PATH_V1 + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(cheeseDto))));

        CheeseOrder cheeseOrder = createCheeseOrder();
        cheeseOrder.setCustomerRef("dont-validate");

        CheeseOrder savedCheeseOrder = cheeseOrderManager.newCheeseOrder(cheeseOrder);

        await().untilAsserted(() -> {
            CheeseOrder foundOrder = cheeseOrderRepository.findById(cheeseOrder.getId()).get();
            assertEquals(CheeseOrderStatusEnum.VALIDATION_PENDING, foundOrder.getOrderStatus());
        });

        cheeseOrderManager.cancelOrder(savedCheeseOrder.getId());

        await().untilAsserted(() -> {
            CheeseOrder foundOrder = cheeseOrderRepository.findById(cheeseOrder.getId()).get();
            assertEquals(CheeseOrderStatusEnum.CANCELLED, foundOrder.getOrderStatus());
        });
    }

    @Test
    void testAllocationPendingToCancel() throws JsonProcessingException {
        CheeseDto cheeseDto = CheeseDto.builder().id(cheeseId).upc("12345").build();

        wireMockServer.stubFor(get(CheeseServiceImpl.CHEESE_UPC_PATH_V1 + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(cheeseDto))));

        CheeseOrder cheeseOrder = createCheeseOrder();
        cheeseOrder.setCustomerRef("dont-allocate");

        CheeseOrder savedCheeseOrder = cheeseOrderManager.newCheeseOrder(cheeseOrder);

        await().untilAsserted(() -> {
            CheeseOrder foundOrder = cheeseOrderRepository.findById(cheeseOrder.getId()).get();
            assertEquals(CheeseOrderStatusEnum.ALLOCATION_PENDING, foundOrder.getOrderStatus());
        });

        cheeseOrderManager.cancelOrder(savedCheeseOrder.getId());

        await().untilAsserted(() -> {
            CheeseOrder foundOrder = cheeseOrderRepository.findById(cheeseOrder.getId()).get();
            assertEquals(CheeseOrderStatusEnum.CANCELLED, foundOrder.getOrderStatus());
        });
    }

    @Test
    void testAllocatedToCancel() throws JsonProcessingException {
        CheeseDto cheeseDto = CheeseDto.builder().id(cheeseId).upc("12345").build();

        wireMockServer.stubFor(get(CheeseServiceImpl.CHEESE_UPC_PATH_V1 + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(cheeseDto))));

        CheeseOrder cheeseOrder = createCheeseOrder();

        CheeseOrder savedCheeseOrder = cheeseOrderManager.newCheeseOrder(cheeseOrder);

        await().untilAsserted(() -> {
            CheeseOrder foundOrder = cheeseOrderRepository.findById(cheeseOrder.getId()).get();
            assertEquals(CheeseOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
        });

        cheeseOrderManager.cancelOrder(savedCheeseOrder.getId());

        await().untilAsserted(() -> {
            CheeseOrder foundOrder = cheeseOrderRepository.findById(cheeseOrder.getId()).get();
            assertEquals(CheeseOrderStatusEnum.CANCELLED, foundOrder.getOrderStatus());
        });

        DeallocateOrderRequest deallocateOrderRequest = (DeallocateOrderRequest) jmsTemplate.receiveAndConvert(JmsConfig.DEALLOCATE_ORDER_QUEUE);

        assertNotNull(deallocateOrderRequest);
        Assertions.assertThat(deallocateOrderRequest.getCheeseOrderDto().getId()).isEqualTo(savedCheeseOrder.getId());
    }

    public CheeseOrder createCheeseOrder(){
        CheeseOrder cheeseOrder = CheeseOrder.builder()
                .customer(testCustomer)
                .build();

        Set<CheeseOrderLine> lines = new HashSet<>();
        lines.add(CheeseOrderLine.builder()
                .cheeseId(cheeseId)
                .upc("12345")
                .orderQuantity(1)
                .cheeseOrder(cheeseOrder)
                .build());

        cheeseOrder.setCheeseOrderLines(lines);

        return cheeseOrder;
    }
}
