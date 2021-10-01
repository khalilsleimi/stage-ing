package com.vneuron.vneuroncheeseservice.repositories;

import com.vneuron.creamery.model.CheeseStyleEnum;
import com.vneuron.vneuroncheeseservice.domain.Cheese;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface CheeseRepository extends JpaRepository<Cheese, UUID> {
    Page<Cheese> findAllByCheeseName(String cheeseName, Pageable pageable);

    Page<Cheese> findAllByCheeseStyle(CheeseStyleEnum cheeseStyle, Pageable pageable);

    Page<Cheese> findAllByCheeseNameAndCheeseStyle(String cheeseName, CheeseStyleEnum cheeseStyle, Pageable pageable);

    Cheese findByUpc(String upc);
}
