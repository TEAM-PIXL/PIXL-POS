package com.example.pixlpos.constructs;

import java.util.HashMap;
import java.util.Map;

public class Order {
    private Map<String, Integer> items;
    private double total;

    public Order() {
        items = new HashMap<>();
        total = 0.0;
    }

    public void addItem(String itemName, double price) {
        items.put(itemName, items.getOrDefault(itemName, 0) + 1);
        total += price;
        if (total < 0) {
            total = 0;
        }
    }

    public void removeItem(String itemName, double price) {
        if (items.containsKey(itemName)) {
            int quantity = items.get(itemName);
            if (quantity > 1) {
                items.put(itemName, quantity - 1);
            } else {
                items.remove(itemName);
            }
            total -= price;
            if (total < 0) {
                total = 0;
            }
        }
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }

    public void setItemQuantity(String itemName, int quantity, double price) {
        if (quantity < 1) {
            quantity = 1;
        }
        int currentQuantity = items.getOrDefault(itemName, 0);
        total -= currentQuantity * price;
        items.put(itemName, quantity);
        total += quantity * price;
        if (total < 0) {
            total = 0;
        }
    }

    public String getItemWithQuantity(String itemName) {
        int quantity = items.getOrDefault(itemName, 0);
        return itemName + " x" + quantity;
    }
}
