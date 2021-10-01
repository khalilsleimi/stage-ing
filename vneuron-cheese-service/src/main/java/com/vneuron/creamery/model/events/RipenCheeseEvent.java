package com.vneuron.creamery.model.events;

import com.vneuron.creamery.model.CheeseDto;
import lombok.NoArgsConstructor;


@NoArgsConstructor
public class RipenCheeseEvent extends CheeseEvent {

    public RipenCheeseEvent(CheeseDto cheeseDto) {
        super(cheeseDto);
    }
}
