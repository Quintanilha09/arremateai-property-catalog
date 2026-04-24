package com.arremateai.propertycatalog.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "imovel", indexes = {
        @Index(name = "idx_imovel_uf", columnList = "uf"),
        @Index(name = "idx_imovel_data_leilao", columnList = "data_leilao"),
        @Index(name = "idx_imovel_valor", columnList = "valor_avaliacao"),
        @Index(name = "idx_imovel_status", columnList = "status"),
        @Index(name = "idx_imovel_vendedor", columnList = "vendedor_id")
})
@Getter @Setter @NoArgsConstructor
public class Imovel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "numero_leilao", nullable = false, unique = true, length = 100)
    private String numeroLeilao;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Column(name = "valor_avaliacao", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorAvaliacao;

    @Column(name = "data_leilao", nullable = false)
    private LocalDate dataLeilao;

    @Column(nullable = false, length = 2)
    private String uf;

    @Column(nullable = false, length = 300)
    private String instituicao;

    @Column(name = "link_edital", length = 1000)
    private String linkEdital;

    @Column(name = "link_leilao", length = 1000)
    private String linkLeilao;

    @Column(name = "tipo_leilao", length = 30)
    private String tipoLeilao;

    @Column(length = 100)
    private String cidade;

    @Column(length = 200)
    private String bairro;

    @Column(name = "area_total", precision = 10, scale = 2)
    private BigDecimal areaTotal;

    @Column(name = "tipo_imovel", length = 50)
    private String tipoImovel;

    private Integer quartos;
    private Integer banheiros;
    private Integer vagas;

    @Column(length = 500)
    private String endereco;

    @Column(length = 10)
    private String cep;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(length = 50)
    private String condicao;

    @Column(name = "aceita_financiamento")
    private Boolean aceitaFinanciamento = false;

    @Column(length = 2000)
    private String observacoes;

    @Column(length = 20)
    private String status = "DISPONIVEL";

    @Column(name = "vendedor_id")
    private UUID vendedorId;

    @OneToMany(mappedBy = "imovel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("ordem ASC")
    private List<ImagemImovel> imagens = new ArrayList<>();

    @OneToMany(mappedBy = "imovel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("ordem ASC")
    private List<VideoImovel> videos = new ArrayList<>();

    @Column(nullable = false)
    private Boolean ativo = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
