package com.arremateai.propertycatalog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "leiloeira")
@Getter @Setter @NoArgsConstructor
public class Leiloeira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "tipo_integracao", nullable = false, length = 20)
    private String tipoIntegracao;

    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    @Column(name = "configuracao_json", columnDefinition = "TEXT")
    private String configuracaoJson;

    @Column(length = 20)
    private String status = "ATIVA";

    @Column(name = "ultima_sincronizacao")
    private LocalDateTime ultimaSincronizacao;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
