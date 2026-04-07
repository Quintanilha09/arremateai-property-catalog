package com.arremateai.propertycatalog.service;

import com.arremateai.propertycatalog.dto.CidadeResponse;
import com.arremateai.propertycatalog.dto.EstadoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class LocalizacaoServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private LocalizacaoService localizacaoService;

    @BeforeEach
    void setUp() {
        localizacaoService = new LocalizacaoService();
        ReflectionTestUtils.setField(localizacaoService, "restClient", restClient);

        lenient().when(restClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    // ===== listarEstados =====

    @Test
    @DisplayName("Deve retornar lista vazia quando API retornar null para estados")
    void deveRetornarListaVaziaQuandoApiRetornarNullParaEstados() {
        when(responseSpec.body(any(Class.class))).thenReturn(null);

        List<EstadoResponse> resultado = localizacaoService.listarEstados();

        assertThat(resultado).isEmpty();
        verify(restClient).get();
    }

    // ===== listarCidadesPorEstado =====

    @Test
    @DisplayName("Deve retornar lista vazia quando API retornar null para cidades")
    void deveRetornarListaVaziaQuandoApiRetornarNullParaCidades() {
        when(responseSpec.body(any(Class.class))).thenReturn(null);

        List<CidadeResponse> resultado = localizacaoService.listarCidadesPorEstado("SP");

        assertThat(resultado).isEmpty();
        verify(restClient).get();
    }
}
