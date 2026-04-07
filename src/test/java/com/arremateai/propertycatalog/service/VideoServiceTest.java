package com.arremateai.propertycatalog.service;

import com.arremateai.propertycatalog.domain.Imovel;
import com.arremateai.propertycatalog.domain.VideoImovel;
import com.arremateai.propertycatalog.dto.VideoRequest;
import com.arremateai.propertycatalog.dto.VideoResponse;
import com.arremateai.propertycatalog.repository.ImovelRepository;
import com.arremateai.propertycatalog.repository.VideoImovelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private VideoImovelRepository videoRepo;

    @Mock
    private ImovelRepository imovelRepo;

    @InjectMocks
    private VideoService videoService;

    private static final UUID ID_IMOVEL = UUID.randomUUID();
    private static final UUID ID_VIDEO = UUID.randomUUID();
    private static final String URL_PADRAO = "http://video.com/tour.mp4";

    private Imovel criarImovelPadrao() {
        Imovel imovel = new Imovel();
        imovel.setId(ID_IMOVEL);
        imovel.setAtivo(true);
        return imovel;
    }

    private VideoImovel criarVideoPadrao() {
        VideoImovel video = new VideoImovel();
        video.setId(ID_VIDEO);
        video.setUrl(URL_PADRAO);
        video.setNomeOriginal("tour.mp4");
        video.setTamanho(50000L);
        video.setTipo("mp4");
        video.setOrdem(0);
        video.setImovel(criarImovelPadrao());
        return video;
    }

    private VideoRequest criarRequestPadrao() {
        return new VideoRequest(URL_PADRAO, "tour.mp4", 50000L, "mp4", 0);
    }

    // ===== registrarVideo =====

    @Test
    @DisplayName("Deve registrar vídeo com sucesso")
    void deveRegistrarVideoComSucesso() {
        VideoRequest req = criarRequestPadrao();
        when(imovelRepo.findByIdAndAtivoTrue(ID_IMOVEL)).thenReturn(Optional.of(criarImovelPadrao()));
        when(videoRepo.save(any(VideoImovel.class))).thenAnswer(inv -> {
            VideoImovel v = inv.getArgument(0);
            v.setId(ID_VIDEO);
            return v;
        });

        VideoResponse resultado = videoService.registrarVideo(ID_IMOVEL, req);

        assertThat(resultado).isNotNull();
        assertThat(resultado.url()).isEqualTo(URL_PADRAO);
        assertThat(resultado.nomeOriginal()).isEqualTo("tour.mp4");
        verify(videoRepo).save(any(VideoImovel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar vídeo para imóvel inexistente")
    void deveLancarExcecaoAoRegistrarVideoParaImovelInexistente() {
        when(imovelRepo.findByIdAndAtivoTrue(ID_IMOVEL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> videoService.registrarVideo(ID_IMOVEL, criarRequestPadrao()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Imóvel não encontrado");
    }

    @Test
    @DisplayName("Deve usar ordem 0 quando ordem for null no request")
    void deveUsarOrdemZeroQuandoOrdemForNull() {
        VideoRequest req = new VideoRequest(URL_PADRAO, "tour.mp4", 50000L, "mp4", null);
        when(imovelRepo.findByIdAndAtivoTrue(ID_IMOVEL)).thenReturn(Optional.of(criarImovelPadrao()));
        when(videoRepo.save(any(VideoImovel.class))).thenAnswer(inv -> {
            VideoImovel v = inv.getArgument(0);
            v.setId(ID_VIDEO);
            assertThat(v.getOrdem()).isZero();
            return v;
        });

        videoService.registrarVideo(ID_IMOVEL, req);

        verify(videoRepo).save(any(VideoImovel.class));
    }

    // ===== listarVideos =====

    @Test
    @DisplayName("Deve listar vídeos do imóvel ordenados por ordem")
    void deveListarVideosDoImovelOrdenadosPorOrdem() {
        VideoImovel video = criarVideoPadrao();
        when(videoRepo.findByImovelIdOrderByOrdemAsc(ID_IMOVEL)).thenReturn(List.of(video));

        List<VideoResponse> resultado = videoService.listarVideos(ID_IMOVEL);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).url()).isEqualTo(URL_PADRAO);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando imóvel não tem vídeos")
    void deveRetornarListaVaziaQuandoImovelNaoTemVideos() {
        when(videoRepo.findByImovelIdOrderByOrdemAsc(ID_IMOVEL)).thenReturn(List.of());

        List<VideoResponse> resultado = videoService.listarVideos(ID_IMOVEL);

        assertThat(resultado).isEmpty();
    }

    // ===== removerVideo =====

    @Test
    @DisplayName("Deve remover vídeo com sucesso")
    void deveRemoverVideoComSucesso() {
        when(videoRepo.existsById(ID_VIDEO)).thenReturn(true);

        videoService.removerVideo(ID_VIDEO);

        verify(videoRepo).deleteById(ID_VIDEO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover vídeo inexistente")
    void deveLancarExcecaoAoRemoverVideoInexistente() {
        when(videoRepo.existsById(ID_VIDEO)).thenReturn(false);

        assertThatThrownBy(() -> videoService.removerVideo(ID_VIDEO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Vídeo não encontrado");
    }
}
