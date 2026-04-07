package com.arremateai.propertycatalog.controller;

import com.arremateai.propertycatalog.dto.ProdutoResponse;
import com.arremateai.propertycatalog.service.ProdutoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoControllerTest {

    @Mock
    private ProdutoService produtoService;

    @InjectMocks
    private ProdutoController produtoController;

    private ProdutoResponse criarProdutoResponse() {
        return new ProdutoResponse(1L, "Produto", "Desc", "Eletrônicos", null, "NOVO",
                BigDecimal.TEN, BigDecimal.ONE, null, null, "SP", null, "ATIVO", null, null, null);
    }

    // ===== listar =====

    @Test
    @DisplayName("Deve retornar página de produtos")
    void deveRetornarPaginaDeProdutos() {
        Page<ProdutoResponse> pagina = new PageImpl<>(List.of(criarProdutoResponse()));
        when(produtoService.buscarProdutos(any(), any(), any(), any(Pageable.class))).thenReturn(pagina);

        var resultado = produtoController.listar(null, null, null, 0, 12);

        assertThat(resultado.getStatusCode().value()).isEqualTo(200);
        assertThat(resultado.getBody().getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve filtrar produtos por categoria e valor")
    void deveFiltrarProdutosPorCategoriaEValor() {
        Page<ProdutoResponse> pagina = new PageImpl<>(List.of());
        when(produtoService.buscarProdutos(eq("Eletrônicos"), eq(BigDecimal.ONE), eq(BigDecimal.TEN), any(Pageable.class)))
                .thenReturn(pagina);

        var resultado = produtoController.listar("Eletrônicos", BigDecimal.ONE, BigDecimal.TEN, 0, 12);

        assertThat(resultado.getStatusCode().value()).isEqualTo(200);
        verify(produtoService).buscarProdutos(eq("Eletrônicos"), eq(BigDecimal.ONE), eq(BigDecimal.TEN), any());
    }

    // ===== buscarPorId =====

    @Test
    @DisplayName("Deve retornar produto por ID")
    void deveRetornarProdutoPorId() {
        when(produtoService.buscarPorId(1L)).thenReturn(criarProdutoResponse());

        var resultado = produtoController.buscarPorId(1L);

        assertThat(resultado.getStatusCode().value()).isEqualTo(200);
        assertThat(resultado.getBody().titulo()).isEqualTo("Produto");
        verify(produtoService).buscarPorId(1L);
    }
}
