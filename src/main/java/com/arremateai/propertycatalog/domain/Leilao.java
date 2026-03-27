package com.arremateai.propertycatalog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "leilao")
@Getter @Setter @NoArgsConstructor
public class Leilao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leiloeira_id", nullable = false)
    private Leiloeira leiloeira;

    @Column(nullable = false, length = 500)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Column(name = "data_encerramento")
    private LocalDateTime dataEncerramento;

    @Column(length = 200)
    private String localizacao;

    @Column(length = 20)
    private String status = "AGENDADO";

    @Column(name = "url_edital", length = 1000)
    private String urlEdital;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
