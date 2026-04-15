package com.arremateai.propertycatalog.dto;

import java.util.Map;

public record EstatisticasImovelResponse(
        long totalVisualizacoes,
        long visitantesUnicos,
        long totalFavoritos,
        String ultimaVisualizacao,
        Map<String, Long> visualizacoesPorDia
) {}
