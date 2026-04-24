package com.arremateai.propertycatalog.service;

import com.arremateai.propertycatalog.domain.ImagemImovel;
import com.arremateai.propertycatalog.domain.Imovel;
import com.arremateai.propertycatalog.dto.ImagemRequest;
import com.arremateai.propertycatalog.dto.ImagemResponse;
import com.arremateai.propertycatalog.repository.ImagemImovelRepository;
import com.arremateai.propertycatalog.repository.ImovelRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImagemServiceTest {

    @Mock
    private ImagemImovelRepository imagemRepo;

    @Mock
    private ImovelRepository imovelRepo;

    @InjectMocks
    private ImagemService imagemService;

    private static final UUID ID_IMOVEL = UUID.randomUUID();
    private static final UUID ID_IMAGEM = UUID.randomUUID();
    private static final String URL_PADRAO = "http://img.com/foto.jpg";

    private Imovel criarImovelPadrao() {
        Imovel imovel = new Imovel();
        imovel.setId(ID_IMOVEL);
        imovel.setAtivo(true);
        return imovel;
    }

    private ImagemImovel criarImagemPadrao() {
        ImagemImovel img = new ImagemImovel();
        img.setId(ID_IMAGEM);
        img.setUrl(URL_PADRAO);
        img.setLegenda("Fachada");
        img.setPrincipal(false);
        img.setOrdem(0);
        img.setImovel(criarImovelPadrao());
        return img;
    }

    private ImagemRequest criarRequestPadrao() {
        return new ImagemRequest(URL_PADRAO, "Fachada", false, 0);
    }

    // ===== registrarImagem =====

    @Test
    @DisplayName("Deve registrar imagem com sucesso")
    void deveRegistrarImagemComSucesso() {
        ImagemRequest req = criarRequestPadrao();
        when(imovelRepo.findByIdAndAtivoTrue(ID_IMOVEL)).thenReturn(Optional.of(criarImovelPadrao()));
        when(imagemRepo.save(any(ImagemImovel.class))).thenAnswer(inv -> {
            ImagemImovel img = inv.getArgument(0);
            img.setId(ID_IMAGEM);
            return img;
        });

        ImagemResponse resultado = imagemService.registrarImagem(ID_IMOVEL, req);

        assertThat(resultado).isNotNull();
        assertThat(resultado.url()).isEqualTo(URL_PADRAO);
        verify(imagemRepo).save(any(ImagemImovel.class));
    }

    @Test
    @DisplayName("Deve limpar imagem principal anterior ao registrar nova principal")
    void deveLimparImagemPrincipalAnteriorAoRegistrarNovaPrincipal() {
        ImagemRequest req = new ImagemRequest(URL_PADRAO, "Fachada", true, 0);
        when(imovelRepo.findByIdAndAtivoTrue(ID_IMOVEL)).thenReturn(Optional.of(criarImovelPadrao()));
        when(imagemRepo.save(any(ImagemImovel.class))).thenAnswer(inv -> {
            ImagemImovel img = inv.getArgument(0);
            img.setId(ID_IMAGEM);
            return img;
        });

        imagemService.registrarImagem(ID_IMOVEL, req);

        verify(imagemRepo).clearPrincipalByImovelId(ID_IMOVEL);
    }

    @Test
    @DisplayName("Não deve limpar principal quando imagem não é principal")
    void naoDeveLimparPrincipalQuandoImagemNaoEhPrincipal() {
        ImagemRequest req = new ImagemRequest(URL_PADRAO, "Sala", false, 1);
        when(imovelRepo.findByIdAndAtivoTrue(ID_IMOVEL)).thenReturn(Optional.of(criarImovelPadrao()));
        when(imagemRepo.save(any(ImagemImovel.class))).thenReturn(criarImagemPadrao());

        imagemService.registrarImagem(ID_IMOVEL, req);

        verify(imagemRepo, never()).clearPrincipalByImovelId(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar imagem para imóvel inexistente")
    void deveLancarExcecaoAoRegistrarImagemParaImovelInexistente() {
        when(imovelRepo.findByIdAndAtivoTrue(ID_IMOVEL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imagemService.registrarImagem(ID_IMOVEL, criarRequestPadrao()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Imóvel não encontrado");
    }

    @Test
    @DisplayName("Deve usar ordem 0 quando ordem for null no request")
    void deveUsarOrdemZeroQuandoOrdemForNull() {
        ImagemRequest req = new ImagemRequest(URL_PADRAO, "Fachada", null, null);
        when(imovelRepo.findByIdAndAtivoTrue(ID_IMOVEL)).thenReturn(Optional.of(criarImovelPadrao()));
        when(imagemRepo.save(any(ImagemImovel.class))).thenAnswer(inv -> {
            ImagemImovel img = inv.getArgument(0);
            img.setId(ID_IMAGEM);
            assertThat(img.getOrdem()).isZero();
            return img;
        });

        imagemService.registrarImagem(ID_IMOVEL, req);

        verify(imagemRepo).save(any(ImagemImovel.class));
    }

    // ===== listarImagens =====

    @Test
    @DisplayName("Deve listar imagens do imóvel ordenadas por ordem")
    void deveListarImagensDoImovelOrdenadasPorOrdem() {
        ImagemImovel img = criarImagemPadrao();
        when(imagemRepo.findByImovelIdOrderByOrdemAsc(ID_IMOVEL)).thenReturn(List.of(img));

        List<ImagemResponse> resultado = imagemService.listarImagens(ID_IMOVEL);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).url()).isEqualTo(URL_PADRAO);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando imóvel não tem imagens")
    void deveRetornarListaVaziaQuandoImovelNaoTemImagens() {
        when(imagemRepo.findByImovelIdOrderByOrdemAsc(ID_IMOVEL)).thenReturn(List.of());

        List<ImagemResponse> resultado = imagemService.listarImagens(ID_IMOVEL);

        assertThat(resultado).isEmpty();
    }

    // ===== atualizarImagem =====

    @Test
    @DisplayName("Deve atualizar apenas campos não nulos da imagem")
    void deveAtualizarApenasCamposNaoNulosDaImagem() {
        ImagemImovel img = criarImagemPadrao();
        ImagemRequest req = new ImagemRequest(null, "Nova legenda", null, 5);
        when(imagemRepo.findById(ID_IMAGEM)).thenReturn(Optional.of(img));
        when(imagemRepo.save(any(ImagemImovel.class))).thenReturn(img);

        ImagemResponse resultado = imagemService.atualizarImagem(ID_IMAGEM, req);

        assertThat(resultado).isNotNull();
        assertThat(img.getLegenda()).isEqualTo("Nova legenda");
        assertThat(img.getOrdem()).isEqualTo(5);
        assertThat(img.getUrl()).isEqualTo(URL_PADRAO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar imagem inexistente")
    void deveLancarExcecaoAoAtualizarImagemInexistente() {
        when(imagemRepo.findById(ID_IMAGEM)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imagemService.atualizarImagem(ID_IMAGEM, criarRequestPadrao()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Imagem não encontrada");
    }

    // ===== definirImagemPrincipal =====

    @Test
    @DisplayName("Deve definir imagem como principal limpando anterior")
    void deveDefinirImagemComoPrincipalLimpandoAnterior() {
        ImagemImovel img = criarImagemPadrao();
        when(imagemRepo.findById(ID_IMAGEM)).thenReturn(Optional.of(img));
        when(imagemRepo.save(any(ImagemImovel.class))).thenReturn(img);

        imagemService.definirImagemPrincipal(ID_IMAGEM);

        verify(imagemRepo).clearPrincipalByImovelId(ID_IMOVEL);
        assertThat(img.getPrincipal()).isTrue();
        verify(imagemRepo).save(img);
    }

    @Test
    @DisplayName("Deve lançar exceção ao definir principal para imagem inexistente")
    void deveLancarExcecaoAoDefinirPrincipalParaImagemInexistente() {
        when(imagemRepo.findById(ID_IMAGEM)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imagemService.definirImagemPrincipal(ID_IMAGEM))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ===== removerImagem =====

    @Test
    @DisplayName("Deve remover imagem com sucesso")
    void deveRemoverImagemComSucesso() {
        when(imagemRepo.existsById(ID_IMAGEM)).thenReturn(true);

        imagemService.removerImagem(ID_IMAGEM);

        verify(imagemRepo).deleteById(ID_IMAGEM);
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover imagem inexistente")
    void deveLancarExcecaoAoRemoverImagemInexistente() {
        when(imagemRepo.existsById(ID_IMAGEM)).thenReturn(false);

        assertThatThrownBy(() -> imagemService.removerImagem(ID_IMAGEM))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Imagem não encontrada");
    }
}
