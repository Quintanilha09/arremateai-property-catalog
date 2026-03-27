package com.arremateai.propertycatalog.repository;

import com.arremateai.propertycatalog.domain.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query("""
            SELECT p FROM Produto p
            WHERE p.status = 'DISPONIVEL'
              AND (:categoria IS NULL OR LOWER(p.categoria) = LOWER(:categoria))
              AND (:valorMin IS NULL OR p.lanceMinimo >= :valorMin)
              AND (:valorMax IS NULL OR p.lanceMinimo <= :valorMax)
            """)
    Page<Produto> buscarComFiltros(
            @Param("categoria") String categoria,
            @Param("valorMin") BigDecimal valorMin,
            @Param("valorMax") BigDecimal valorMax,
            Pageable pageable);
}
