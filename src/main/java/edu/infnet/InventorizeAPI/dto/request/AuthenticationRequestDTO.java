package edu.infnet.InventorizeAPI.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationRequestDTO(
        @Email @NotBlank String email,
        @NotBlank @Size(min=8, max=16) String password
) {}