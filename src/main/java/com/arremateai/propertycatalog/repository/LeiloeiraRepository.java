package com.arremateai.propertycatalog.repository;

import com.arremateai.propertycatalog.domain.Leiloeira;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeiloeiraRepository extends JpaRepository<Leiloeira, Long> {
    List<Leiloeira> findByStatus(String status);
    Optional<Leiloeira> findByNome(String nome);
}
