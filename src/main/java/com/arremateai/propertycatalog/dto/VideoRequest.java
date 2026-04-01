package com.arremateai.propertycatalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record VideoRequest(
        @NotBlank @URL @Size(max = 2000) String url,
        String nomeOriginal,
        Long tamanho,
        String tipo,
        Integer ordem
) {}
