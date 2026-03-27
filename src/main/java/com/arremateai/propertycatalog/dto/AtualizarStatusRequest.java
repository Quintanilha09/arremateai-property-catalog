package com.arremateai.propertycatalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AtualizarStatusRequest(
        @NotBlank
        @Pattern(regexp = "DISPONIVEL|VENDIDO|SUSPENSO", message = "Status inválido")
        String status
) {}
