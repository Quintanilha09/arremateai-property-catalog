package com.arremateai.propertycatalog.dto;

import com.arremateai.propertycatalog.domain.Produto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProdutoResponse(
        Long id,
        String titulo,
        String descricao,
        String categoria,
        String subcategoria,
        String condicao,
        BigDecimal valorAvaliacao,
        BigDecimal lanceMinimo,
        BigDecimal lanceAtual,
        String[] fotosUrls,
        String localizacao,
        LocalDateTime dataLimite,
        String status,
        String urlOriginal,
        LeiloeiraSimples leiloeira,
        Long leilaoId
) {
    public record LeiloeiraSimples(Long id, String nome, String logoUrl) {}

    public static ProdutoResponse fromEntity(Produto p) {
        LeiloeiraSimples leiloeiraSimples = p.getLeiloeira() != null
                ? new LeiloeiraSimples(p.getLeiloeira().getId(), p.getLeiloeira().getNome(), p.getLeiloeira().getLogoUrl())
                : null;
        return new ProdutoResponse(
                p.getId(),
                p.getTitulo(),
                p.getDescricao(),
                p.getCategoria(),
                p.getSubcategoria(),
                p.getCondicao(),
                p.getValorAvaliacao(),
                p.getLanceMinimo(),
                p.getLanceAtual(),
                p.getFotosUrls(),
                p.getLocalizacao(),
                p.getDataLimite(),
                p.getStatus(),
                p.getUrlOriginal(),
                leiloeiraSimples,
                p.getLeilao() != null ? p.getLeilao().getId() : null
        );
    }
}
