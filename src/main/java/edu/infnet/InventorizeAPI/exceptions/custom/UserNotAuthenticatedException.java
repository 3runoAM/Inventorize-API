package edu.infnet.InventorizeAPI.exceptions.custom;

public class UserNotAuthenticatedException extends RuntimeException {
    public UserNotAuthenticatedException(String message) {
        super(message);
    }
}