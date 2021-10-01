package com.vneuron.creamery.model.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CheeseEvent implements Serializable {

    static final long serialVersionUID = -5781515597148163111L;

    private CheeseDto cheeseDto;
}
