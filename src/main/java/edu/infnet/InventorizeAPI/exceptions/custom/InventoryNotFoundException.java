package edu.infnet.InventorizeAPI.exceptions.custom;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(String message) {
        super(message);
    }
}
