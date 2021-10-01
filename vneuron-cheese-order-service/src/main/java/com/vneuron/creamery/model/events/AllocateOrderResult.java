package com.vneuron.creamery.model.events;

import com.vneuron.creamery.model.CheeseOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocateOrderResult {
    private CheeseOrderDto cheeseOrderDto;
    private Boolean allocationError = false;
    private Boolean pendingInventory = false;
}
