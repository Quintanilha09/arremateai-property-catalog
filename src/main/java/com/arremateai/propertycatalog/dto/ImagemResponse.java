package com.arremateai.propertycatalog.dto;

import java.util.UUID;

public record ImagemResponse(
        UUID id,
        String url,
        String legenda,
        Boolean principal,
        Integer ordem
) {}
