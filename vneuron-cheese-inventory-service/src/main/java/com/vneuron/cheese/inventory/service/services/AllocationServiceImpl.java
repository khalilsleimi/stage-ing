package com.vneuron.cheese.inventory.service.services;

import com.vneuron.cheese.inventory.service.repositories.CheeseInventoryRepository;
import com.vneuron.cheese.inventory.service.domain.CheeseInventory;
import com.vneuron.creamery.model.CheeseOrderDto;
import com.vneuron.creamery.model.CheeseOrderLineDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class AllocationServiceImpl implements AllocationService {

    private final CheeseInventoryRepository cheeseInventoryRepository;

    @Override
    public Boolean allocateOrder(CheeseOrderDto cheeseOrderDto) {
        log.debug("Allocating OrderId: " + cheeseOrderDto.getId());

        AtomicInteger totalOrdered = new AtomicInteger();
        AtomicInteger totalAllocated = new AtomicInteger();

        cheeseOrderDto.getCheeseOrderLines().forEach(cheeseOrderLine -> {
            if ((((cheeseOrderLine.getOrderQuantity() != null ? cheeseOrderLine.getOrderQuantity() : 0)
                    - (cheeseOrderLine.getQuantityAllocated() != null ? cheeseOrderLine.getQuantityAllocated() : 0)) > 0)) {
                allocateCheeseOrderLine(cheeseOrderLine);
            }
            totalOrdered.set(totalOrdered.get() + cheeseOrderLine.getOrderQuantity());
            totalAllocated.set(totalAllocated.get() + (cheeseOrderLine.getQuantityAllocated() != null ? cheeseOrderLine.getQuantityAllocated() : 0));
        });

        log.debug("Total Ordered: " + totalOrdered.get() + " Total Allocated: " + totalAllocated.get());

        return totalOrdered.get() == totalAllocated.get();
    }

    private void allocateCheeseOrderLine(CheeseOrderLineDto cheeseOrderLine) {
        List<CheeseInventory> cheeseInventoryList = cheeseInventoryRepository.findAllByUpc(cheeseOrderLine.getUpc());

        cheeseInventoryList.forEach(cheeseInventory -> {
            int inventory = (cheeseInventory.getQuantityOnHand() == null) ? 0 : cheeseInventory.getQuantityOnHand();
            int orderQty = (cheeseOrderLine.getOrderQuantity() == null) ? 0 : cheeseOrderLine.getOrderQuantity();
            int allocatedQty = (cheeseOrderLine.getQuantityAllocated() == null) ? 0 : cheeseOrderLine.getQuantityAllocated();
            int qtyToAllocate = orderQty - allocatedQty;

            if (inventory >= qtyToAllocate) { // full allocation
                inventory = inventory - qtyToAllocate;
                cheeseOrderLine.setQuantityAllocated(orderQty);
                cheeseInventory.setQuantityOnHand(inventory);

                cheeseInventoryRepository.save(cheeseInventory);
            } else if (inventory > 0) { //partial allocation
                cheeseOrderLine.setQuantityAllocated(allocatedQty + inventory);
                cheeseInventory.setQuantityOnHand(0);

            }

            if (cheeseInventory.getQuantityOnHand() == 0) {
                cheeseInventoryRepository.delete(cheeseInventory);
            }
        });

    }

    @Override
    public void deallocateOrder(CheeseOrderDto cheeseOrderDto) {
        cheeseOrderDto.getCheeseOrderLines().forEach(cheeseOrderLineDto -> {
            CheeseInventory cheeseInventory = CheeseInventory.builder()
                    .cheeseId(cheeseOrderLineDto.getCheeseId())
                    .upc(cheeseOrderLineDto.getUpc())
                    .quantityOnHand(cheeseOrderLineDto.getQuantityAllocated())
                    .build();

            CheeseInventory savedInventory = cheeseInventoryRepository.save(cheeseInventory);

            log.debug("Saved Inventory for cheese upc: " + savedInventory.getUpc() + " inventory id: " + savedInventory.getId());
        });
    }
}
