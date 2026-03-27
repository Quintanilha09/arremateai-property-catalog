package com.arremateai.propertycatalog.repository;

import com.arremateai.propertycatalog.domain.Leilao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeilaoRepository extends JpaRepository<Leilao, Long> {
    List<Leilao> findByStatus(String status);
}
