package com.vneuron.cheese.order.service.services;

import com.vneuron.cheese.order.service.bootstrap.CheeseOrderBootStrap;
import com.vneuron.cheese.order.service.domain.Customer;
import com.vneuron.cheese.order.service.repositories.CheeseOrderRepository;
import com.vneuron.cheese.order.service.repositories.CustomerRepository;
import com.vneuron.creamery.model.CheeseOrderDto;
import com.vneuron.creamery.model.CheeseOrderLineDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class TastingRoomService {

    private final CustomerRepository customerRepository;
    private final CheeseOrderService cheeseOrderService;
    private final CheeseOrderRepository cheeseOrderRepository;
    private final List<String> cheeseUpcs = new ArrayList<>(3);

    public TastingRoomService(CustomerRepository customerRepository, CheeseOrderService cheeseOrderService,
                              CheeseOrderRepository cheeseOrderRepository) {
        this.customerRepository = customerRepository;
        this.cheeseOrderService = cheeseOrderService;
        this.cheeseOrderRepository = cheeseOrderRepository;

        cheeseUpcs.add(CheeseOrderBootStrap.CHEESE_1_UPC);
        cheeseUpcs.add(CheeseOrderBootStrap.CHEESE_2_UPC);
        cheeseUpcs.add(CheeseOrderBootStrap.CHEESE_3_UPC);
    }

    @Transactional
    @Scheduled(fixedRate = 2000) //run every 2 seconds
    public void placeTastingRoomOrder(){

        List<Customer> customerList = customerRepository.findAllByCustomerNameLike(CheeseOrderBootStrap.TASTING_ROOM);

        if (customerList.size() == 1){ //should be just one
            doPlaceOrder(customerList.get(0));
        } else {
            log.error("Too many or too few tasting room customers found");

            customerList.forEach(customer -> log.debug(customer.toString()));
        }
    }

    private void doPlaceOrder(Customer customer) {
        String cheeseToOrder = getRandomCheeseUpc();

        CheeseOrderLineDto cheeseOrderLine = CheeseOrderLineDto.builder()
                .upc(cheeseToOrder)
                .orderQuantity(new Random().nextInt(6)) //todo externalize value to property
                .build();

        List<CheeseOrderLineDto> cheeseOrderLineSet = new ArrayList<>();
        cheeseOrderLineSet.add(cheeseOrderLine);

        CheeseOrderDto cheeseOrder = CheeseOrderDto.builder()
                .customerId(customer.getId())
                .customerRef(UUID.randomUUID().toString())
                .cheeseOrderLines(cheeseOrderLineSet)
                .build();

        CheeseOrderDto savedOrder = cheeseOrderService.placeOrder(customer.getId(), cheeseOrder);

    }

    private String getRandomCheeseUpc() {
        return cheeseUpcs.get(new Random().nextInt(cheeseUpcs.size() -0));
    }
}
