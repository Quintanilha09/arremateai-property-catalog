package com.arremateai.propertycatalog.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ImovelResponse(
        UUID id,
        String numeroLeilao,
        String descricao,
        BigDecimal valorAvaliacao,
        String dataLeilao,
        String uf,
        String instituicao,
        String linkEdital,
        String linkLeilao,
        String tipoLeilao,
        String cidade,
        String bairro,
        BigDecimal areaTotal,
        String tipoImovel,
        Integer quartos,
        Integer banheiros,
        Integer vagas,
        String endereco,
        String cep,
        BigDecimal latitude,
        BigDecimal longitude,
        String condicao,
        Boolean aceitaFinanciamento,
        String observacoes,
        String status,
        UUID vendedorId,
        List<ImagemResponse> imagens,
        String imagemPrincipal,
        List<VideoResponse> videos
) {}
