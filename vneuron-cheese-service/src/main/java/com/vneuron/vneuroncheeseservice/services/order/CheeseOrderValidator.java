package com.vneuron.vneuroncheeseservice.services.order;

import com.vneuron.creamery.model.events.CheeseOrderDto;
import com.vneuron.vneuroncheeseservice.repositories.CheeseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Component
public class CheeseOrderValidator {

    private final CheeseRepository cheeseRepository;

    public Boolean validateOrder(CheeseOrderDto cheeseOrder){

        AtomicInteger cheesesNotFound = new AtomicInteger();

        cheeseOrder.getCheeseOrderLines().forEach(orderline -> {
            if(cheeseRepository.findByUpc(orderline.getUpc()) == null){
                cheesesNotFound.incrementAndGet();
            }
        });

        return cheesesNotFound.get() == 0;
    }

}
