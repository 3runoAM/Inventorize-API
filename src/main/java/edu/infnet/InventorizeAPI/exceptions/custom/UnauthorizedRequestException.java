package edu.infnet.InventorizeAPI.exceptions.custom;

public class UnauthorizedRequestException extends RuntimeException {
    public UnauthorizedRequestException(String message) {
        super(message);
    }
}
