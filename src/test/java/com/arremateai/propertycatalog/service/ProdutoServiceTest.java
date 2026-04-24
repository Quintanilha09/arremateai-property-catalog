package com.arremateai.propertycatalog.service;

import com.arremateai.propertycatalog.domain.Leiloeira;
import com.arremateai.propertycatalog.domain.Produto;
import com.arremateai.propertycatalog.domain.Leilao;
import com.arremateai.propertycatalog.dto.ProdutoResponse;
import com.arremateai.propertycatalog.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepo;

    @InjectMocks
    private ProdutoService produtoService;

    private static final Long ID_PADRAO = 1L;

    private Produto criarProdutoPadrao() {
        Produto produto = new Produto();
        produto.setId(ID_PADRAO);
        produto.setTitulo("Veículo Fiat Uno");
        produto.setDescricao("Veículo em bom estado");
        produto.setCategoria("Veículos");
        produto.setValorAvaliacao(new BigDecimal("25000.00"));
        produto.setLanceMinimo(new BigDecimal("15000.00"));
        produto.setStatus("DISPONIVEL");

        Leiloeira leiloeira = new Leiloeira();
        leiloeira.setId(1L);
        leiloeira.setNome("Leiloeira X");
        leiloeira.setLogoUrl("http://logo.com/x.png");
        produto.setLeiloeira(leiloeira);

        Leilao leilao = new Leilao();
        leilao.setId(1L);
        produto.setLeilao(leilao);

        return produto;
    }

    // ===== buscarProdutos =====

    @Test
    @DisplayName("Deve retornar página de produtos com filtros")
    void deveRetornarPaginaDeProdutosComFiltros() {
        Produto produto = criarProdutoPadrao();
        Page<Produto> pagina = new PageImpl<>(List.of(produto));
        Pageable pageable = PageRequest.of(0, 10);
        when(produtoRepo.buscarComFiltros("Veículos", null, null, pageable)).thenReturn(pagina);

        Page<ProdutoResponse> resultado = produtoService.buscarProdutos("Veículos", null, null, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).titulo()).isEqualTo("Veículo Fiat Uno");
        verify(produtoRepo).buscarComFiltros("Veículos", null, null, pageable);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver produtos")
    void deveRetornarPaginaVaziaQuandoNaoHouverProdutos() {
        Pageable pageable = PageRequest.of(0, 10);
        when(produtoRepo.buscarComFiltros(null, null, null, pageable)).thenReturn(Page.empty());

        Page<ProdutoResponse> resultado = produtoService.buscarProdutos(null, null, null, pageable);

        assertThat(resultado.getContent()).isEmpty();
    }

    // ===== buscarPorId =====

    @Test
    @DisplayName("Deve retornar produto quando buscar por ID existente")
    void deveRetornarProdutoQuandoBuscarPorIdExistente() {
        Produto produto = criarProdutoPadrao();
        when(produtoRepo.findById(ID_PADRAO)).thenReturn(Optional.of(produto));

        ProdutoResponse resultado = produtoService.buscarPorId(ID_PADRAO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(ID_PADRAO);
        assertThat(resultado.categoria()).isEqualTo("Veículos");
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não for encontrado")
    void deveLancarExcecaoQuandoProdutoNaoForEncontrado() {
        when(produtoRepo.findById(ID_PADRAO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> produtoService.buscarPorId(ID_PADRAO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Produto não encontrado");
    }

    @Test
    @DisplayName("Deve retornar produto com leiloeira null quando não houver leiloeira")
    void deveRetornarProdutoComLeiloeiraNullQuandoNaoHouverLeiloeira() {
        Produto produto = criarProdutoPadrao();
        produto.setLeiloeira(null);
        when(produtoRepo.findById(ID_PADRAO)).thenReturn(Optional.of(produto));

        ProdutoResponse resultado = produtoService.buscarPorId(ID_PADRAO);

        assertThat(resultado.leiloeira()).isNull();
    }
}
