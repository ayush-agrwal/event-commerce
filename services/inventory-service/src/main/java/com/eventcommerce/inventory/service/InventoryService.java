package com.eventcommerce.inventory.service;

import com.eventcommerce.inventory.model.InventoryItem;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InventoryService {
    private final List<InventoryItem> inventory = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public InventoryItem addInventory(InventoryItem item) {
        item.setId(idGenerator.getAndIncrement());
        inventory.add(item);
        return item;
    }

    public List<InventoryItem> getInventory() {
        return inventory;
    }
}