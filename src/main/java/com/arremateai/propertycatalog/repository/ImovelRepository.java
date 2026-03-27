package com.arremateai.propertycatalog.repository;

import com.arremateai.propertycatalog.domain.Imovel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ImovelRepository extends JpaRepository<Imovel, UUID>, JpaSpecificationExecutor<Imovel> {

    Optional<Imovel> findByNumeroLeilaoAndAtivoTrue(String numeroLeilao);

    boolean existsByNumeroLeilao(String numeroLeilao);

    Optional<Imovel> findByIdAndAtivoTrue(UUID id);

    @Query("SELECT i FROM Imovel i WHERE i.vendedorId = :vendedorId AND i.ativo = true ORDER BY i.createdAt DESC")
    Page<Imovel> findByVendedorId(@Param("vendedorId") UUID vendedorId, Pageable pageable);

    @Query("SELECT i FROM Imovel i WHERE i.vendedorId = :vendedorId AND i.status = :status AND i.ativo = true ORDER BY i.createdAt DESC")
    Page<Imovel> findByVendedorIdAndStatus(@Param("vendedorId") UUID vendedorId, @Param("status") String status, Pageable pageable);
}
