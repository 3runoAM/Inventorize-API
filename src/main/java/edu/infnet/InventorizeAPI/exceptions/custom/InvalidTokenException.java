package edu.infnet.InventorizeAPI.exceptions.custom;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}