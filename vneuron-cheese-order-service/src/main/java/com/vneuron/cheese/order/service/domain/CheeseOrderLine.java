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
package com.vneuron.cheese.order.service.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class CheeseOrderLine extends BaseEntity {

    @Builder
    public CheeseOrderLine(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate,
                           CheeseOrder cheeseOrder, UUID cheeseId, String upc, Integer orderQuantity,
                           Integer quantityAllocated) {
        super(id, version, createdDate, lastModifiedDate);
        this.cheeseOrder = cheeseOrder;
        this.cheeseId = cheeseId;
        this.upc = upc;
        this.orderQuantity = orderQuantity;
        this.quantityAllocated = quantityAllocated;
    }

    @ManyToOne
    private CheeseOrder cheeseOrder;

    private UUID cheeseId;
    private String upc;
    private Integer orderQuantity = 0;
    private Integer quantityAllocated = 0;
}
