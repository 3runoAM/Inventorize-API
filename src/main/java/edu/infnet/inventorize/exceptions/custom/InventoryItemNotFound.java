package edu.infnet.inventorize.exceptions.custom;

public class InventoryItemNotFound extends RuntimeException {
    public InventoryItemNotFound(String message) {
        super(message);
    }
}
