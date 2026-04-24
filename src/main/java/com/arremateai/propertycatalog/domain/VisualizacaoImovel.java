package com.arremateai.propertycatalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "visualizacao_imovel", indexes = {
        @Index(name = "idx_visualizacao_imovel_id", columnList = "imovel_id"),
        @Index(name = "idx_visualizacao_ip_imovel", columnList = "imovel_id, ip_address"),
        @Index(name = "idx_visualizacao_created_at", columnList = "created_at")
})
@Getter @Setter @NoArgsConstructor
public class VisualizacaoImovel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "imovel_id", nullable = false)
    private UUID imovelId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_id")
    private UUID userId;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
