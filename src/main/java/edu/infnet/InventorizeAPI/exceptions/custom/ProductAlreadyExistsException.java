package edu.infnet.InventorizeAPI.exceptions.custom;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String message) {
        super(message);
    }
}
