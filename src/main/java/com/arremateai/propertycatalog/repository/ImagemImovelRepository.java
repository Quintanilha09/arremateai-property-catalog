package com.arremateai.propertycatalog.repository;

import com.arremateai.propertycatalog.domain.ImagemImovel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImagemImovelRepository extends JpaRepository<ImagemImovel, UUID> {

    List<ImagemImovel> findByImovelIdOrderByOrdemAsc(UUID imovelId);

    Optional<ImagemImovel> findByImovelIdAndPrincipalTrue(UUID imovelId);

    long countByImovelId(UUID imovelId);

    @Modifying
    @Query("UPDATE ImagemImovel i SET i.principal = false WHERE i.imovel.id = :imovelId")
    void clearPrincipalByImovelId(@Param("imovelId") UUID imovelId);
}
