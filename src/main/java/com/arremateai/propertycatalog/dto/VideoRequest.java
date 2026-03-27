package com.arremateai.propertycatalog.dto;

import jakarta.validation.constraints.NotBlank;

public record VideoRequest(
        @NotBlank String url,
        String nomeOriginal,
        Long tamanho,
        String tipo,
        Integer ordem
) {}
