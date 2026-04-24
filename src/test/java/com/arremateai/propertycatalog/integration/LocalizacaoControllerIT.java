package com.arremateai.propertycatalog.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class LocalizacaoControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/localizacao/estados retorna 200")
    void deveListarEstados() throws Exception {
        mockMvc.perform(get("/api/localizacao/estados"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/localizacao/estados/{sigla}/cidades retorna 200")
    void deveListarCidadesPorEstado() throws Exception {
        mockMvc.perform(get("/api/localizacao/estados/SP/cidades"))
                .andExpect(status().isOk());
    }
}
