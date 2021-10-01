package com.vneuron.creamery.model.events;

import com.vneuron.creamery.model.CheeseDto;
import lombok.NoArgsConstructor;


@NoArgsConstructor
public class NewInventoryEvent extends CheeseEvent {
    public NewInventoryEvent(CheeseDto cheeseDto) {
        super(cheeseDto);
    }
}
