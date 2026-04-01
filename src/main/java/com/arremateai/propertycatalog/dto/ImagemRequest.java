package com.arremateai.propertycatalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record ImagemRequest(
        @NotBlank @URL @Size(max = 2000) String url,
        String legenda,
        Boolean principal,
        Integer ordem
) {}
