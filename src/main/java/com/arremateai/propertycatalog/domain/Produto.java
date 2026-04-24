package com.arremateai.propertycatalog.domain;

import com.arremateai.propertycatalog.converter.StringArrayConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "produto")
@Getter @Setter @NoArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leilao_id", nullable = false)
    private Leilao leilao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leiloeira_id", nullable = false)
    private Leiloeira leiloeira;

    @Column(nullable = false, length = 500)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(length = 100)
    private String categoria;

    @Column(length = 100)
    private String subcategoria;

    @Column(length = 20)
    private String condicao;

    @Column(name = "valor_avaliacao", precision = 15, scale = 2)
    private BigDecimal valorAvaliacao;

    @Column(name = "lance_minimo", precision = 15, scale = 2)
    private BigDecimal lanceMinimo;

    @Column(name = "lance_atual", precision = 15, scale = 2)
    private BigDecimal lanceAtual;

    @Column(name = "fotos_urls", columnDefinition = "TEXT")
    @Convert(converter = StringArrayConverter.class)
    private String[] fotosUrls;

    @Column(columnDefinition = "TEXT")
    private String especificacoes;

    @Column(length = 200)
    private String localizacao;

    @Column(name = "data_limite")
    private LocalDateTime dataLimite;

    @Column(length = 20)
    private String status = "DISPONIVEL";

    @Column(name = "payload_original", columnDefinition = "TEXT")
    private String payloadOriginal;

    @Column(name = "url_original", length = 1000)
    private String urlOriginal;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
