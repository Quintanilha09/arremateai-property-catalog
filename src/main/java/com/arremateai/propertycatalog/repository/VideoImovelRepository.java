package com.arremateai.propertycatalog.repository;

import com.arremateai.propertycatalog.domain.VideoImovel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VideoImovelRepository extends JpaRepository<VideoImovel, UUID> {

    List<VideoImovel> findByImovelIdOrderByOrdemAsc(UUID imovelId);

    long countByImovelId(UUID imovelId);
}
