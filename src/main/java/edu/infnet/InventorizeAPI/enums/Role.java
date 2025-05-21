package edu.infnet.InventorizeAPI.enums;

import lombok.Getter;

/**
 * Enumeração que representa os diferentes papéis de usuário no sistema.
 * Cada papel possui uma descrição escrita em linguagem humana.
 */
@Getter
public enum Role {
    ROLE_ADMIN("ADMINISTRADOR"),
    ROLE_USER("USUÁRIO");

    private final String description;

    Role(String description) {
        this.description = description;
    }
}