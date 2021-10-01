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
package com.vneuron.cheese.order.service.repositories;


import com.vneuron.cheese.order.service.domain.CheeseOrder;
import com.vneuron.cheese.order.service.domain.CheeseOrderStatusEnum;
import com.vneuron.cheese.order.service.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;



public interface CheeseOrderRepository  extends JpaRepository<CheeseOrder, UUID> {

    Page<CheeseOrder> findAllByCustomer(Customer customer, Pageable pageable);

    List<CheeseOrder> findAllByOrderStatus(CheeseOrderStatusEnum orderStatusEnum);

   // @Lock(LockModeType.PESSIMISTIC_WRITE)
   // CheeseOrder findOneById(UUID id);
}
