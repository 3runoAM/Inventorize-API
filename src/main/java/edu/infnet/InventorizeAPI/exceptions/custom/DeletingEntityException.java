package edu.infnet.InventorizeAPI.exceptions.custom;

public class DeletingEntityException extends RuntimeException {
    public DeletingEntityException(String message) {
        super(message);
    }
}
