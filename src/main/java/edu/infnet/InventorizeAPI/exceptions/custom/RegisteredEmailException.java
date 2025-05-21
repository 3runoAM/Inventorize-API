package edu.infnet.InventorizeAPI.exceptions.custom;

public class RegisteredEmailException extends RuntimeException {
    public RegisteredEmailException(String message) {
        super(message);
    }
}
