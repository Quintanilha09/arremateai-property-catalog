package com.arremateai.propertycatalog.dto;

import jakarta.validation.constraints.NotBlank;

public record ImagemRequest(
        @NotBlank String url,
        String legenda,
        Boolean principal,
        Integer ordem
) {}
