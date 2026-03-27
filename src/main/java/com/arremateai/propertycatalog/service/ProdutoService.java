package com.arremateai.propertycatalog.service;

import com.arremateai.propertycatalog.dto.ProdutoResponse;
import com.arremateai.propertycatalog.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepo;

    @Transactional(readOnly = true)
    public Page<ProdutoResponse> buscarProdutos(String categoria, BigDecimal valorMin, BigDecimal valorMax,
                                                 Pageable pageable) {
        return produtoRepo.buscarComFiltros(categoria, valorMin, valorMax, pageable)
                .map(ProdutoResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        return produtoRepo.findById(id)
                .map(ProdutoResponse::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));
    }
}
