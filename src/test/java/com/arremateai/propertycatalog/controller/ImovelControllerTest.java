package com.arremateai.propertycatalog.controller;

import com.arremateai.propertycatalog.dto.*;
import com.arremateai.propertycatalog.service.ImagemService;
import com.arremateai.propertycatalog.service.ImovelService;
import com.arremateai.propertycatalog.service.VideoService;
import com.arremateai.propertycatalog.service.VisualizacaoService;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImovelControllerTest {

    @Mock
    private ImovelService imovelService;

    @Mock
    private ImagemService imagemService;

    @Mock
    private VideoService videoService;

    @Mock
    private VisualizacaoService visualizacaoService;

    @InjectMocks
    private ImovelController imovelController;

    private static final String USER_ID_PADRAO = "123e4567-e89b-12d3-a456-426614174000";
    private static final UUID USER_UUID = UUID.fromString(USER_ID_PADRAO);
    private static final UUID IMOVEL_ID = UUID.randomUUID();
    private static final UUID IMAGEM_ID = UUID.randomUUID();
    private static final UUID VIDEO_ID = UUID.randomUUID();

    private ImovelResponse criarImovelResponse() {
        return new ImovelResponse(IMOVEL_ID, "LEI-001", "Descrição", BigDecimal.TEN,
                "2026-05-01", "SP", "Banco X", null, null, "JUDICIAL", "São Paulo",
                "Centro", BigDecimal.valueOf(100), "APARTAMENTO", 3, 2, 1,
                "Rua X, 100", "01000-000", null, null, "USADO", true, null,
                "DISPONIVEL", USER_UUID, List.of(), null, List.of());
    }

    private ImovelRequest criarImovelRequest() {
        return new ImovelRequest("LEI-001", "Descrição", BigDecimal.TEN, "2026-05-01",
                "SP", "Banco X", null, null, "JUDICIAL", "São Paulo", "Centro",
                BigDecimal.valueOf(100), "APARTAMENTO", 3, 2, 1, "Rua X, 100",
                "01000-000", null, null, "USADO", true, null, "DISPONIVEL");
    }

    // ===== listar (público) =====

    @Test
    @DisplayName("Deve retornar página de imóveis com filtros")
    void deveRetornarPaginaDeImoveisComFiltros() {
        Page<ImovelResponse> pagina = new PageImpl<>(List.of(criarImovelResponse()));
        when(imovelService.buscarComFiltros(any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(pagina);

        var resultado = imovelController.listar("SP", "São Paulo", null, null,
                null, null, null, null, null, null, null, null, 0, 12, null, "desc");

        assertThat(resultado.getStatusCode().value()).isEqualTo(200);
        assertThat(resultado.getBody().getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve usar sortBy padrão quando não informado")
    void deveUsarSortByPadraoQuandoNaoInformado() {
        Page<ImovelResponse> pagina = new PageImpl<>(List.of());
        when(imovelService.buscarComFiltros(any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(pagina);

        imovelController.listar(null, null, null, null, null, null, null,
                null, null, null, null, null, 0, 12, null, "desc");

        verify(imovelService).buscarComFiltros(any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(Pageable.class));
    }

    // ===== buscarPorId =====

    @Test
    @DisplayName("Deve retornar imóvel por ID")
    void deveRetornarImovelPorId() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(imovelService.buscarPorId(IMOVEL_ID)).thenReturn(criarImovelResponse());

        var resultado = imovelController.buscarPorId(IMOVEL_ID, request, null);

        assertThat(resultado.getStatusCode().value()).isEqualTo(200);
        assertThat(resultado.getBody().id()).isEqualTo(IMOVEL_ID);
    }

    // ===== destaques =====

    @Test
    @DisplayName("Deve retornar imóveis em destaque")
    void deveRetornarImoveisEmDestaque() {
        when(imovelService.buscarDestaques(6)).thenReturn(List.of(criarImovelResponse()));

        var resultado = imovelController.destaques(6);

        assertThat(resultado.getStatusCode().value()).isEqualTo(200);
        assertThat(resultado.getBody()).hasSize(1);
    }

    // ===== recentes =====

    @Test
    @DisplayName("Deve retornar imóveis recentes")
    void deveRetornarImoveisRecentes() {
        when(imovelService.buscarRecentes(6)).thenReturn(List.of(criarImovelResponse()));

        var resultado = imovelController.recentes(6);

        assertThat(resultado.getStatusCode().value()).isEqualTo(200);
        assertThat(resultado.getBody()).hasSize(1);
    }

    // ===== maisProcurados =====

    @Test
    @DisplayName("Deve retornar imóveis mais procurados")
    void deveRetornarImoveisMaisProcurados() {
        when(imovelService.buscarMaisProcurados(6, visualizacaoService)).thenReturn(List.of(criarImovelResponse()));

        var resultado = imovelController.maisProcurados(6);

        assertThat(resultado.getStatusCode().value()).isEqualTo(200);
    }

    // ===== estatisticas =====

    @Test
    @DisplayName("Deve retornar estatísticas de imóveis")
    void deveRetornarEstatisticasDeImoveis() {
        when(imovelService.buscarEstatisticas()).thenReturn(Map.of("total", 10));

        var resultado = imovelController.estatisticas();

        assertThat(resultado.getStatusCode().value()).isEqualTo(200);
        assertThat(resultado.getBody()).containsEntry("total", 10);
    }

    // ===== validar =====

    @Test
    @DisplayName("Deve retornar validação de imóvel")
    void deveRetornarValidacaoDeImovel() {
        when(imovelService.validarImovel(IMOVEL_ID)).thenReturn(Map.of("valido", true));

        var resultado = imovelController.validar(IMOVEL_ID);

        assertThat(resultado.getStatusCode().value()).isEqualTo(200);
    }

    // ===== listarImagens =====

    @Test
    @DisplayName("Deve retornar lista de imagens do imóvel")
    void deveRetornarListaDeImagensDoImovel() {
        var imagem = new ImagemResponse(IMAGEM_ID, "http://img.jpg", "Sala", true, 1);
        when(imagemService.listarImagens(IMOVEL_ID)).thenReturn(List.of(imagem));

        var resultado = imovelController.listarImagens(IMOVEL_ID);

        assertThat(resultado.getStatusCode().value()).isEqualTo(200);
        assertThat(resultado.getBody()).hasSize(1);
    }

    // ===== listarVideos =====

    @Test
    @DisplayName("Deve retornar lista de vídeos do imóvel")
    void deveRetornarListaDeVideosDoImovel() {
        var video = new VideoResponse(VIDEO_ID, "http://video.mp4", "tour.mp4", 1000L, "video/mp4", 1);
        when(videoService.listarVideos(IMOVEL_ID)).thenReturn(List.of(video));

        var resultado = imovelController.listarVideos(IMOVEL_ID);

        assertThat(resultado.getStatusCode().value()).isEqualTo(200);
        assertThat(resultado.getBody()).hasSize(1);
    }

    // ===== criar =====

    @Test
    @DisplayName("Deve criar imóvel quando role é ROLE_VENDEDOR")
    void deveCriarImovelQuandoRoleEVendedor() {
        when(imovelService.cadastrarImovel(any(), eq(USER_UUID))).thenReturn(criarImovelResponse());

        var resultado = imovelController.criar(criarImovelRequest(), USER_ID_PADRAO, "ROLE_VENDEDOR");

        assertThat(resultado.getStatusCode().value()).isEqualTo(201);
        verify(imovelService).cadastrarImovel(any(), eq(USER_UUID));
    }

    @Test
    @DisplayName("Deve criar imóvel quando role é ROLE_ADMIN")
    void deveCriarImovelQuandoRoleEAdmin() {
        when(imovelService.cadastrarImovel(any(), eq(USER_UUID))).thenReturn(criarImovelResponse());

        var resultado = imovelController.criar(criarImovelRequest(), USER_ID_PADRAO, "ROLE_ADMIN");

        assertThat(resultado.getStatusCode().value()).isEqualTo(201);
    }

    @Test
    @DisplayName("Deve retornar 403 quando role não é vendedor nem admin ao criar")
    void deveRetornar403QuandoRoleNaoPermitidaAoCriar() {
        var resultado = imovelController.criar(criarImovelRequest(), USER_ID_PADRAO, "ROLE_COMPRADOR");

        assertThat(resultado.getStatusCode().value()).isEqualTo(403);
        verify(imovelService, never()).cadastrarImovel(any(), any());
    }

    // ===== atualizar =====

    @Test
    @DisplayName("Deve atualizar imóvel quando role é ROLE_ADMIN")
    void deveAtualizarImovelQuandoRoleEAdmin() {
        when(imovelService.atualizarImovel(eq(IMOVEL_ID), any())).thenReturn(criarImovelResponse());

        var resultado = imovelController.atualizar(IMOVEL_ID, criarImovelRequest(), USER_ID_PADRAO, "ROLE_ADMIN");

        assertThat(resultado.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("Deve retornar 403 quando role não é admin ao atualizar")
    void deveRetornar403QuandoRoleNaoEAdminAoAtualizar() {
        var resultado = imovelController.atualizar(IMOVEL_ID, criarImovelRequest(), USER_ID_PADRAO, "ROLE_VENDEDOR");

        assertThat(resultado.getStatusCode().value()).isEqualTo(403);
    }

    // ===== atualizarParcial =====

    @Test
    @DisplayName("Deve atualizar parcialmente quando role é ROLE_ADMIN")
    void deveAtualizarParcialmenteQuandoRoleEAdmin() {
        when(imovelService.atualizarParcial(eq(IMOVEL_ID), any())).thenReturn(criarImovelResponse());

        var resultado = imovelController.atualizarParcial(IMOVEL_ID, criarImovelRequest(), USER_ID_PADRAO, "ROLE_ADMIN");

        assertThat(resultado.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("Deve retornar 403 quando role não é admin ao atualizar parcial")
    void deveRetornar403QuandoRoleNaoEAdminAoAtualizarParcial() {
        var resultado = imovelController.atualizarParcial(IMOVEL_ID, criarImovelRequest(), USER_ID_PADRAO, "ROLE_COMPRADOR");

        assertThat(resultado.getStatusCode().value()).isEqualTo(403);
    }

    // ===== remover =====

    @Test
    @DisplayName("Deve remover imóvel e retornar 204 quando role é ROLE_ADMIN")
    void deveRemoverImovelERetornar204QuandoAdmin() {
        doNothing().when(imovelService).removerImovel(IMOVEL_ID);

        var resultado = imovelController.remover(IMOVEL_ID, USER_ID_PADRAO, "ROLE_ADMIN");

        assertThat(resultado.getStatusCode().value()).isEqualTo(204);
        verify(imovelService).removerImovel(IMOVEL_ID);
    }

    @Test
    @DisplayName("Deve retornar 403 quando role não é admin ao remover")
    void deveRetornar403QuandoRoleNaoEAdminAoRemover() {
        var resultado = imovelController.remover(IMOVEL_ID, USER_ID_PADRAO, "ROLE_COMPRADOR");

        assertThat(resultado.getStatusCode().value()).isEqualTo(403);
    }

    // ===== atualizarStatus =====

    @Test
    @DisplayName("Deve atualizar status do imóvel")
    void deveAtualizarStatusDoImovel() {
        when(imovelService.atualizarStatus(eq(IMOVEL_ID), eq("VENDIDO"), eq(USER_UUID), eq("ROLE_ADMIN")))
                .thenReturn(criarImovelResponse());

        var resultado = imovelController.atualizarStatus(IMOVEL_ID,
                new AtualizarStatusRequest("VENDIDO"), USER_ID_PADRAO, "ROLE_ADMIN");

        assertThat(resultado.getStatusCode().value()).isEqualTo(200);
    }

    // ===== meus =====

    @Test
    @DisplayName("Deve retornar imóveis do usuário autenticado")
    void deveRetornarImoveisDoUsuarioAutenticado() {
        Page<ImovelResponse> pagina = new PageImpl<>(List.of(criarImovelResponse()));
        when(imovelService.buscarImoveisPorUsuario(eq(USER_UUID), isNull(), any(Pageable.class)))
                .thenReturn(pagina);

        var resultado = imovelController.meus(USER_ID_PADRAO, null, 0, 10);

        assertThat(resultado.getStatusCode().value()).isEqualTo(200);
        assertThat(resultado.getBody().getContent()).hasSize(1);
    }

    // ===== registrarImagem =====

    @Test
    @DisplayName("Deve registrar imagem e retornar 201")
    void deveRegistrarImagemERetornar201() {
        var request = new ImagemRequest("http://img.jpg", "Sala", true, 1);
        var resposta = new ImagemResponse(IMAGEM_ID, "http://img.jpg", "Sala", true, 1);
        when(imagemService.registrarImagem(IMOVEL_ID, request)).thenReturn(resposta);

        var resultado = imovelController.registrarImagem(IMOVEL_ID, request, USER_ID_PADRAO);

        assertThat(resultado.getStatusCode().value()).isEqualTo(201);
    }

    // ===== atualizarImagem =====

    @Test
    @DisplayName("Deve atualizar imagem existente")
    void deveAtualizarImagemExistente() {
        var request = new ImagemRequest("http://new.jpg", "Quarto", false, 2);
        var resposta = new ImagemResponse(IMAGEM_ID, "http://new.jpg", "Quarto", false, 2);
        when(imagemService.atualizarImagem(IMAGEM_ID, request)).thenReturn(resposta);

        var resultado = imovelController.atualizarImagem(IMAGEM_ID, request, USER_ID_PADRAO);

        assertThat(resultado.getStatusCode().value()).isEqualTo(200);
    }

    // ===== definirPrincipal =====

    @Test
    @DisplayName("Deve definir imagem como principal e retornar 204")
    void deveDefinirImagemComoPrincipalERetornar204() {
        doNothing().when(imagemService).definirImagemPrincipal(IMAGEM_ID);

        var resultado = imovelController.definirPrincipal(IMAGEM_ID, USER_ID_PADRAO);

        assertThat(resultado.getStatusCode().value()).isEqualTo(204);
    }

    // ===== removerImagem =====

    @Test
    @DisplayName("Deve remover imagem e retornar 204")
    void deveRemoverImagemERetornar204() {
        doNothing().when(imagemService).removerImagem(IMAGEM_ID);

        var resultado = imovelController.removerImagem(IMAGEM_ID, USER_ID_PADRAO);

        assertThat(resultado.getStatusCode().value()).isEqualTo(204);
    }

    // ===== registrarVideo =====

    @Test
    @DisplayName("Deve registrar vídeo e retornar 201")
    void deveRegistrarVideoERetornar201() {
        var request = new VideoRequest("http://video.mp4", "tour.mp4", 1000L, "video/mp4", 1);
        var resposta = new VideoResponse(VIDEO_ID, "http://video.mp4", "tour.mp4", 1000L, "video/mp4", 1);
        when(videoService.registrarVideo(IMOVEL_ID, request)).thenReturn(resposta);

        var resultado = imovelController.registrarVideo(IMOVEL_ID, request, USER_ID_PADRAO);

        assertThat(resultado.getStatusCode().value()).isEqualTo(201);
    }

    // ===== removerVideo =====

    @Test
    @DisplayName("Deve remover vídeo e retornar 204")
    void deveRemoverVideoERetornar204() {
        doNothing().when(videoService).removerVideo(VIDEO_ID);

        var resultado = imovelController.removerVideo(VIDEO_ID, USER_ID_PADRAO);

        assertThat(resultado.getStatusCode().value()).isEqualTo(204);
    }
}
