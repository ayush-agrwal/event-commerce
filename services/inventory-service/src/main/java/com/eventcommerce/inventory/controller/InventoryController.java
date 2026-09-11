package com.eventcommerce.inventory.controller;

import com.eventcommerce.inventory.model.InventoryItem;
import com.eventcommerce.inventory.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<InventoryItem> addInventory(@RequestBody InventoryItem item) {
        return ResponseEntity.ok(inventoryService.addInventory(item));
    }

    @GetMapping
    public ResponseEntity<List<InventoryItem>> getInventory() {
        return ResponseEntity.ok(inventoryService.getInventory());
    }
}