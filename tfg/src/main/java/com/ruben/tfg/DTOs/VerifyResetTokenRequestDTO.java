package com.ruben.tfg.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyResetTokenRequestDTO {
    @NotBlank
    private String token;
}