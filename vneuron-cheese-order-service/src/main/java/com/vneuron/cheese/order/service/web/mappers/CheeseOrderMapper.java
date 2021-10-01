/*
 *  Copyright 2019 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.vneuron.cheese.order.service.web.mappers;

import com.vneuron.cheese.order.service.domain.CheeseOrder;
import com.vneuron.creamery.model.CheeseOrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DateMapper.class, CheeseOrderLineMapper.class})
public interface CheeseOrderMapper {

    @Mapping(target = "customerId", source = "customer.id")
    CheeseOrderDto cheeseOrderToDto(CheeseOrder cheeseOrder);

    CheeseOrder dtoToCheeseOrder(CheeseOrderDto dto);
}
