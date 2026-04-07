package com.arremateai.propertycatalog.service;

import com.arremateai.propertycatalog.domain.ImagemImovel;
import com.arremateai.propertycatalog.domain.Imovel;
import com.arremateai.propertycatalog.domain.VideoImovel;
import com.arremateai.propertycatalog.dto.ImagemResponse;
import com.arremateai.propertycatalog.dto.ImovelRequest;
import com.arremateai.propertycatalog.dto.ImovelResponse;
import com.arremateai.propertycatalog.dto.VideoResponse;
import com.arremateai.propertycatalog.exception.BusinessException;
import com.arremateai.propertycatalog.repository.ImagemImovelRepository;
import com.arremateai.propertycatalog.repository.ImovelRepository;
import com.arremateai.propertycatalog.repository.VideoImovelRepository;
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
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImovelServiceTest {

    @Mock
    private ImovelRepository imovelRepo;

    @Mock
    private ImagemImovelRepository imagemRepo;

    @Mock
    private VideoImovelRepository videoRepo;

    @InjectMocks
    private ImovelService imovelService;

    private static final UUID ID_PADRAO = UUID.randomUUID();
    private static final UUID VENDEDOR_ID_PADRAO = UUID.randomUUID();
    private static final String NUMERO_LEILAO_PADRAO = "LEI-001";
    private static final String DESCRICAO_PADRAO = "Apartamento 3 quartos";
    private static final BigDecimal VALOR_PADRAO = new BigDecimal("500000.00");
    private static final String DATA_LEILAO_PADRAO = "2026-06-15";
    private static final String UF_PADRAO = "SP";
    private static final String INSTITUICAO_PADRAO = "Banco do Brasil";

    private Imovel criarImovelPadrao() {
        Imovel imovel = new Imovel();
        imovel.setId(ID_PADRAO);
        imovel.setNumeroLeilao(NUMERO_LEILAO_PADRAO);
        imovel.setDescricao(DESCRICAO_PADRAO);
        imovel.setValorAvaliacao(VALOR_PADRAO);
        imovel.setDataLeilao(LocalDate.parse(DATA_LEILAO_PADRAO));
        imovel.setUf(UF_PADRAO);
        imovel.setInstituicao(INSTITUICAO_PADRAO);
        imovel.setStatus("DISPONIVEL");
        imovel.setAtivo(true);
        imovel.setVendedorId(VENDEDOR_ID_PADRAO);
        imovel.setCidade("São Paulo");
        imovel.setCreatedAt(LocalDateTime.now());
        return imovel;
    }

    private ImovelRequest criarRequestPadrao() {
        return new ImovelRequest(
                NUMERO_LEILAO_PADRAO, DESCRICAO_PADRAO, VALOR_PADRAO, DATA_LEILAO_PADRAO,
                UF_PADRAO, INSTITUICAO_PADRAO, "http://edital.com", "http://leilao.com",
                "JUDICIAL", "São Paulo", "Centro", new BigDecimal("120.00"),
                "Apartamento", 3, 2, 1, "Rua X", "01001-000",
                new BigDecimal("-23.5505"), new BigDecimal("-46.6333"),
                "USADO", true, "Obs teste", "DISPONIVEL"
        );
    }

    private ImagemImovel criarImagemPadrao(UUID imovelId, boolean principal) {
        ImagemImovel img = new ImagemImovel();
        img.setId(UUID.randomUUID());
        img.setUrl("http://img.com/1.jpg");
        img.setLegenda("Fachada");
        img.setPrincipal(principal);
        img.setOrdem(0);
        Imovel imovel = new Imovel();
        imovel.setId(imovelId);
        img.setImovel(imovel);
        return img;
    }

    private VideoImovel criarVideoPadrao(UUID imovelId) {
        VideoImovel video = new VideoImovel();
        video.setId(UUID.randomUUID());
        video.setUrl("http://video.com/1.mp4");
        video.setNomeOriginal("tour.mp4");
        video.setTamanho(10000L);
        video.setTipo("mp4");
        video.setOrdem(0);
        Imovel imovel = new Imovel();
        imovel.setId(imovelId);
        video.setImovel(imovel);
        return video;
    }

    // ===== buscarPorId =====

    @Test
    @DisplayName("Deve retornar imóvel quando buscar por ID existente")
    void deveRetornarImovelQuandoBuscarPorIdExistente() {
        Imovel imovel = criarImovelPadrao();
        when(imovelRepo.findByIdAndAtivoTrue(ID_PADRAO)).thenReturn(Optional.of(imovel));
        when(imagemRepo.findByImovelIdOrderByOrdemAsc(ID_PADRAO)).thenReturn(List.of());
        when(videoRepo.findByImovelIdOrderByOrdemAsc(ID_PADRAO)).thenReturn(List.of());

        ImovelResponse resultado = imovelService.buscarPorId(ID_PADRAO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(ID_PADRAO);
        assertThat(resultado.descricao()).isEqualTo(DESCRICAO_PADRAO);
        verify(imovelRepo).findByIdAndAtivoTrue(ID_PADRAO);
    }

    @Test
    @DisplayName("Deve lançar exceção quando imóvel não for encontrado por ID")
    void deveLancarExcecaoQuandoImovelNaoForEncontrado() {
        when(imovelRepo.findByIdAndAtivoTrue(ID_PADRAO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imovelService.buscarPorId(ID_PADRAO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Imóvel não encontrado");
    }

    @Test
    @DisplayName("Deve retornar imóvel com imagem principal e vídeos")
    void deveRetornarImovelComImagemPrincipalEVideos() {
        Imovel imovel = criarImovelPadrao();
        ImagemImovel img = criarImagemPadrao(ID_PADRAO, true);
        VideoImovel video = criarVideoPadrao(ID_PADRAO);
        when(imovelRepo.findByIdAndAtivoTrue(ID_PADRAO)).thenReturn(Optional.of(imovel));
        when(imagemRepo.findByImovelIdOrderByOrdemAsc(ID_PADRAO)).thenReturn(List.of(img));
        when(videoRepo.findByImovelIdOrderByOrdemAsc(ID_PADRAO)).thenReturn(List.of(video));

        ImovelResponse resultado = imovelService.buscarPorId(ID_PADRAO);

        assertThat(resultado.imagens()).hasSize(1);
        assertThat(resultado.imagemPrincipal()).isEqualTo(img.getUrl());
        assertThat(resultado.videos()).hasSize(1);
    }

    @Test
    @DisplayName("Deve usar primeira imagem quando não houver principal definida")
    void deveUsarPrimeiraImagemQuandoNaoHouverPrincipal() {
        Imovel imovel = criarImovelPadrao();
        ImagemImovel img = criarImagemPadrao(ID_PADRAO, false);
        when(imovelRepo.findByIdAndAtivoTrue(ID_PADRAO)).thenReturn(Optional.of(imovel));
        when(imagemRepo.findByImovelIdOrderByOrdemAsc(ID_PADRAO)).thenReturn(List.of(img));
        when(videoRepo.findByImovelIdOrderByOrdemAsc(ID_PADRAO)).thenReturn(List.of());

        ImovelResponse resultado = imovelService.buscarPorId(ID_PADRAO);

        assertThat(resultado.imagemPrincipal()).isEqualTo(img.getUrl());
    }

    // ===== buscarComFiltros =====

    @Test
    @DisplayName("Deve retornar página de imóveis com filtros aplicados")
    @SuppressWarnings("unchecked")
    void deveRetornarPaginaDeImoveisComFiltros() {
        Imovel imovel = criarImovelPadrao();
        Page<Imovel> pagina = new PageImpl<>(List.of(imovel));
        Pageable pageable = PageRequest.of(0, 10);
        when(imovelRepo.findAll(any(Specification.class), eq(pageable))).thenReturn(pagina);
        when(imagemRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());
        when(videoRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());

        Page<ImovelResponse> resultado = imovelService.buscarComFiltros(
                "SP", "São Paulo", null, null, null, null, null,
                null, null, null, null, null, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).uf()).isEqualTo(UF_PADRAO);
    }

    // ===== buscarImoveisPorUsuario =====

    @Test
    @DisplayName("Deve buscar imóveis do vendedor sem filtro de status")
    void deveBuscarImoveisDoVendedorSemFiltroDeStatus() {
        Imovel imovel = criarImovelPadrao();
        Page<Imovel> pagina = new PageImpl<>(List.of(imovel));
        Pageable pageable = PageRequest.of(0, 10);
        when(imovelRepo.findByVendedorId(VENDEDOR_ID_PADRAO, pageable)).thenReturn(pagina);
        when(imagemRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());
        when(videoRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());

        Page<ImovelResponse> resultado = imovelService.buscarImoveisPorUsuario(VENDEDOR_ID_PADRAO, null, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        verify(imovelRepo).findByVendedorId(VENDEDOR_ID_PADRAO, pageable);
    }

    @Test
    @DisplayName("Deve buscar imóveis do vendedor com filtro de status")
    void deveBuscarImoveisDoVendedorComFiltroDeStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        when(imovelRepo.findByVendedorIdAndStatus(VENDEDOR_ID_PADRAO, "DISPONIVEL", pageable))
                .thenReturn(new PageImpl<>(List.of()));

        Page<ImovelResponse> resultado = imovelService.buscarImoveisPorUsuario(
                VENDEDOR_ID_PADRAO, "DISPONIVEL", pageable);

        assertThat(resultado.getContent()).isEmpty();
        verify(imovelRepo).findByVendedorIdAndStatus(VENDEDOR_ID_PADRAO, "DISPONIVEL", pageable);
    }

    @Test
    @DisplayName("Deve buscar imóveis do vendedor com status vazio como se fosse null")
    void deveBuscarImoveisDoVendedorComStatusVazio() {
        Pageable pageable = PageRequest.of(0, 10);
        when(imovelRepo.findByVendedorId(VENDEDOR_ID_PADRAO, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        imovelService.buscarImoveisPorUsuario(VENDEDOR_ID_PADRAO, "  ", pageable);

        verify(imovelRepo).findByVendedorId(VENDEDOR_ID_PADRAO, pageable);
    }

    // ===== cadastrarImovel =====

    @Test
    @DisplayName("Deve cadastrar imóvel com sucesso")
    void deveCadastrarImovelComSucesso() {
        ImovelRequest req = criarRequestPadrao();
        when(imovelRepo.existsByNumeroLeilao(req.numeroLeilao())).thenReturn(false);
        when(imovelRepo.save(any(Imovel.class))).thenAnswer(inv -> {
            Imovel i = inv.getArgument(0);
            i.setId(ID_PADRAO);
            return i;
        });
        when(imagemRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());
        when(videoRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());

        ImovelResponse resultado = imovelService.cadastrarImovel(req, VENDEDOR_ID_PADRAO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.vendedorId()).isEqualTo(VENDEDOR_ID_PADRAO);
        verify(imovelRepo).save(any(Imovel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando número do leilão já existir")
    void deveLancarExcecaoQuandoNumeroLeilaoJaExistir() {
        ImovelRequest req = criarRequestPadrao();
        when(imovelRepo.existsByNumeroLeilao(req.numeroLeilao())).thenReturn(true);

        assertThatThrownBy(() -> imovelService.cadastrarImovel(req, VENDEDOR_ID_PADRAO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("número de leilão");
    }

    // ===== atualizarImovel =====

    @Test
    @DisplayName("Deve atualizar imóvel com sucesso")
    void deveAtualizarImovelComSucesso() {
        Imovel imovel = criarImovelPadrao();
        ImovelRequest req = criarRequestPadrao();
        when(imovelRepo.findById(ID_PADRAO)).thenReturn(Optional.of(imovel));
        when(imovelRepo.save(any(Imovel.class))).thenReturn(imovel);
        when(imagemRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());
        when(videoRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());

        ImovelResponse resultado = imovelService.atualizarImovel(ID_PADRAO, req);

        assertThat(resultado).isNotNull();
        verify(imovelRepo).save(any(Imovel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar imóvel inexistente")
    void deveLancarExcecaoAoAtualizarImovelInexistente() {
        when(imovelRepo.findById(ID_PADRAO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imovelService.atualizarImovel(ID_PADRAO, criarRequestPadrao()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ===== atualizarParcial =====

    @Test
    @DisplayName("Deve atualizar parcialmente apenas campos não nulos")
    void deveAtualizarParcialmenteApenasCamposNaoNulos() {
        Imovel imovel = criarImovelPadrao();
        ImovelRequest req = new ImovelRequest(
                null, "Nova descrição", null, null, null, null,
                null, null, null, null, null, null, null,
                null, null, null, null, null, null, null,
                null, null, null, null
        );
        when(imovelRepo.findById(ID_PADRAO)).thenReturn(Optional.of(imovel));
        when(imovelRepo.save(any(Imovel.class))).thenReturn(imovel);
        when(imagemRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());
        when(videoRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());

        ImovelResponse resultado = imovelService.atualizarParcial(ID_PADRAO, req);

        assertThat(resultado).isNotNull();
        assertThat(imovel.getDescricao()).isEqualTo("Nova descrição");
        assertThat(imovel.getUf()).isEqualTo(UF_PADRAO);
    }

    // ===== removerImovel =====

    @Test
    @DisplayName("Deve desativar imóvel ao remover")
    void deveDesativarImovelAoRemover() {
        Imovel imovel = criarImovelPadrao();
        when(imovelRepo.findById(ID_PADRAO)).thenReturn(Optional.of(imovel));
        when(imovelRepo.save(any(Imovel.class))).thenReturn(imovel);

        imovelService.removerImovel(ID_PADRAO);

        assertThat(imovel.getAtivo()).isFalse();
        verify(imovelRepo).save(imovel);
    }

    // ===== atualizarStatus =====

    @Test
    @DisplayName("Deve atualizar status quando usuário é admin")
    void deveAtualizarStatusQuandoUsuarioEhAdmin() {
        Imovel imovel = criarImovelPadrao();
        UUID adminId = UUID.randomUUID();
        when(imovelRepo.findById(ID_PADRAO)).thenReturn(Optional.of(imovel));
        when(imovelRepo.save(any(Imovel.class))).thenReturn(imovel);
        when(imagemRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());
        when(videoRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());

        ImovelResponse resultado = imovelService.atualizarStatus(ID_PADRAO, "VENDIDO", adminId, "ROLE_ADMIN");

        assertThat(resultado).isNotNull();
        assertThat(imovel.getStatus()).isEqualTo("VENDIDO");
    }

    @Test
    @DisplayName("Deve atualizar status quando usuário é dono do imóvel")
    void deveAtualizarStatusQuandoUsuarioEhDono() {
        Imovel imovel = criarImovelPadrao();
        when(imovelRepo.findById(ID_PADRAO)).thenReturn(Optional.of(imovel));
        when(imovelRepo.save(any(Imovel.class))).thenReturn(imovel);
        when(imagemRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());
        when(videoRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());

        ImovelResponse resultado = imovelService.atualizarStatus(
                ID_PADRAO, "SUSPENSO", VENDEDOR_ID_PADRAO, "ROLE_VENDEDOR");

        assertThat(resultado).isNotNull();
        assertThat(imovel.getStatus()).isEqualTo("SUSPENSO");
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não é admin nem dono")
    void deveLancarExcecaoQuandoUsuarioNaoEhAdminNemDono() {
        Imovel imovel = criarImovelPadrao();
        UUID outroUsuario = UUID.randomUUID();
        when(imovelRepo.findById(ID_PADRAO)).thenReturn(Optional.of(imovel));

        assertThatThrownBy(() -> imovelService.atualizarStatus(
                ID_PADRAO, "VENDIDO", outroUsuario, "ROLE_COMPRADOR"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Acesso negado");
    }

    // ===== validarImovel =====

    @Test
    @DisplayName("Deve retornar imóvel válido quando tem imagens e está ativo")
    void deveRetornarImovelValidoQuandoTemImagensEEstaAtivo() {
        Imovel imovel = criarImovelPadrao();
        when(imovelRepo.findById(ID_PADRAO)).thenReturn(Optional.of(imovel));
        when(imagemRepo.countByImovelId(ID_PADRAO)).thenReturn(3L);

        Map<String, Object> resultado = imovelService.validarImovel(ID_PADRAO);

        assertThat(resultado.get("isValido")).isEqualTo(true);
        assertThat(resultado.get("temImagens")).isEqualTo(true);
        assertThat(resultado.get("quantidadeImagens")).isEqualTo(3L);
    }

    @Test
    @DisplayName("Deve retornar imóvel inválido quando não tem imagens")
    void deveRetornarImovelInvalidoQuandoNaoTemImagens() {
        Imovel imovel = criarImovelPadrao();
        when(imovelRepo.findById(ID_PADRAO)).thenReturn(Optional.of(imovel));
        when(imagemRepo.countByImovelId(ID_PADRAO)).thenReturn(0L);

        Map<String, Object> resultado = imovelService.validarImovel(ID_PADRAO);

        assertThat(resultado.get("isValido")).isEqualTo(false);
        assertThat(resultado.get("temImagens")).isEqualTo(false);
    }

    // ===== buscarDestaques / buscarRecentes / buscarMaisProcurados =====

    @Test
    @DisplayName("Deve retornar imóveis em destaque filtrando apenas ativos")
    void deveRetornarImoveisEmDestaqueFiltrandoApenasAtivos() {
        Imovel ativo = criarImovelPadrao();
        Imovel inativo = criarImovelPadrao();
        inativo.setAtivo(false);
        when(imovelRepo.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(ativo, inativo)));
        when(imagemRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());
        when(videoRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());

        List<ImovelResponse> resultado = imovelService.buscarDestaques(5);

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Deve retornar imóveis recentes filtrando apenas ativos")
    void deveRetornarImoveisRecentesFiltrandoApenasAtivos() {
        Imovel ativo = criarImovelPadrao();
        when(imovelRepo.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(ativo)));
        when(imagemRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());
        when(videoRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());

        List<ImovelResponse> resultado = imovelService.buscarRecentes(5);

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Deve retornar mesmos resultados de buscarRecentes em buscarMaisProcurados")
    void deveRetornarMesmosResultadosDeBuscarRecentesEmBuscarMaisProcurados() {
        Imovel ativo = criarImovelPadrao();
        when(imovelRepo.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(ativo)));
        when(imagemRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());
        when(videoRepo.findByImovelIdOrderByOrdemAsc(any())).thenReturn(List.of());

        List<ImovelResponse> resultado = imovelService.buscarMaisProcurados(5);

        assertThat(resultado).hasSize(1);
    }

    // ===== buscarEstatisticas =====

    @Test
    @DisplayName("Deve retornar estatísticas com totais por UF e cidade")
    void deveRetornarEstatisticasComTotaisPorUfECidade() {
        Imovel imovel1 = criarImovelPadrao();
        Imovel imovel2 = criarImovelPadrao();
        imovel2.setUf("RJ");
        imovel2.setCidade("Rio de Janeiro");
        Imovel inativo = criarImovelPadrao();
        inativo.setAtivo(false);
        when(imovelRepo.count()).thenReturn(3L);
        when(imovelRepo.findAll()).thenReturn(List.of(imovel1, imovel2, inativo));

        @SuppressWarnings("unchecked")
        Map<String, Object> resultado = imovelService.buscarEstatisticas();

        assertThat(resultado.get("totalImoveis")).isEqualTo(3L);
        assertThat(resultado.get("totalAtivos")).isEqualTo(2L);
        assertThat(resultado.get("totalInativos")).isEqualTo(1L);
    }

    // ===== converterParaResponse (sem imagens) =====

    @Test
    @DisplayName("Deve retornar imagem principal null quando não houver imagens")
    void deveRetornarImagemPrincipalNullQuandoNaoHouverImagens() {
        Imovel imovel = criarImovelPadrao();
        imovel.setDataLeilao(null);
        when(imovelRepo.findByIdAndAtivoTrue(ID_PADRAO)).thenReturn(Optional.of(imovel));
        when(imagemRepo.findByImovelIdOrderByOrdemAsc(ID_PADRAO)).thenReturn(List.of());
        when(videoRepo.findByImovelIdOrderByOrdemAsc(ID_PADRAO)).thenReturn(List.of());

        ImovelResponse resultado = imovelService.buscarPorId(ID_PADRAO);

        assertThat(resultado.imagemPrincipal()).isNull();
        assertThat(resultado.dataLeilao()).isNull();
    }
}
