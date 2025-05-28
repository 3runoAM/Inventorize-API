package edu.infnet.InventorizeAPI.exceptions.custom;

public class InventoryItemNotFound extends RuntimeException {
    public InventoryItemNotFound(String message) {
        super(message);
    }
}
