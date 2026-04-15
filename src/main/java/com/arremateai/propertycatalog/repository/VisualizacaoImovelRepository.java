package com.arremateai.propertycatalog.repository;

import com.arremateai.propertycatalog.domain.VisualizacaoImovel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface VisualizacaoImovelRepository extends JpaRepository<VisualizacaoImovel, UUID> {

    long countByImovelId(UUID imovelId);

    @Query("SELECT COUNT(DISTINCT v.ipAddress) FROM VisualizacaoImovel v WHERE v.imovelId = :imovelId")
    long countDistinctIpByImovelId(UUID imovelId);

    boolean existsByImovelIdAndIpAddressAndCreatedAtAfter(UUID imovelId, String ipAddress, LocalDateTime after);

    List<VisualizacaoImovel> findByImovelIdAndCreatedAtAfterOrderByCreatedAtDesc(UUID imovelId, LocalDateTime after);

    @Query("SELECT v.imovelId, COUNT(v) as total FROM VisualizacaoImovel v " +
           "GROUP BY v.imovelId ORDER BY total DESC LIMIT :limite")
    List<Object[]> findMaisVisualizados(int limite);
}
