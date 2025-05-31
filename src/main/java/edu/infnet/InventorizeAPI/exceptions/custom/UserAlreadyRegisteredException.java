package edu.infnet.InventorizeAPI.exceptions.custom;

public class UserAlreadyRegisteredException extends RuntimeException {
    public UserAlreadyRegisteredException(String message) {
        super(message);
    }
}