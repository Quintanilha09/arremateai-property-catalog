package com.arremateai.propertycatalog.controller;

import com.arremateai.propertycatalog.dto.CidadeResponse;
import com.arremateai.propertycatalog.dto.EstadoResponse;
import com.arremateai.propertycatalog.service.LocalizacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalizacaoControllerTest {

    @Mock
    private LocalizacaoService localizacaoService;

    @InjectMocks
    private LocalizacaoController localizacaoController;

    // ===== listarEstados =====

    @Test
    @DisplayName("Deve retornar lista de estados")
    void deveRetornarListaDeEstados() {
        var estados = List.of(new EstadoResponse("SP", "São Paulo"), new EstadoResponse("RJ", "Rio de Janeiro"));
        when(localizacaoService.listarEstados()).thenReturn(estados);

        var resultado = localizacaoController.listarEstados();

        assertThat(resultado.getStatusCode().value()).isEqualTo(200);
        assertThat(resultado.getBody()).hasSize(2);
        verify(localizacaoService).listarEstados();
    }

    // ===== listarCidades =====

    @Test
    @DisplayName("Deve retornar lista de cidades por estado")
    void deveRetornarListaDeCidadesPorEstado() {
        var cidades = List.of(new CidadeResponse("São Paulo"), new CidadeResponse("Campinas"));
        when(localizacaoService.listarCidadesPorEstado("SP")).thenReturn(cidades);

        var resultado = localizacaoController.listarCidades("SP");

        assertThat(resultado.getStatusCode().value()).isEqualTo(200);
        assertThat(resultado.getBody()).hasSize(2);
        verify(localizacaoService).listarCidadesPorEstado("SP");
    }

    @Test
    @DisplayName("Deve converter sigla para maiúscula antes de buscar cidades")
    void deveConverterSiglaParaMaiusculaAntesDeButscarCidades() {
        when(localizacaoService.listarCidadesPorEstado("SP")).thenReturn(List.of());

        localizacaoController.listarCidades("sp");

        verify(localizacaoService).listarCidadesPorEstado("SP");
    }
}
