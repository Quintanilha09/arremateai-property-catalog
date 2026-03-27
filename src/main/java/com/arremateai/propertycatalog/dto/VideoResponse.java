package com.arremateai.propertycatalog.dto;

import java.util.UUID;

public record VideoResponse(
        UUID id,
        String url,
        String nomeOriginal,
        Long tamanho,
        String tipo,
        Integer ordem
) {}
