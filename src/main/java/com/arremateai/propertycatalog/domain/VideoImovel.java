package com.arremateai.propertycatalog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "video_imovel")
@Getter @Setter @NoArgsConstructor
public class VideoImovel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imovel_id", nullable = false)
    private Imovel imovel;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(name = "nome_original", length = 500)
    private String nomeOriginal;

    private Long tamanho;

    @Column(length = 50)
    private String tipo;

    private Integer ordem = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
