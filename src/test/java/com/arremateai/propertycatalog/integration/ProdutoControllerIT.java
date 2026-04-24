package com.arremateai.propertycatalog.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class ProdutoControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/produtos/{id} inexistente retorna 404")
    void deveRetornar404ProdutoInexistente() throws Exception {
        mockMvc.perform(get("/api/produtos/999999"))
                .andExpect(status().isNotFound());
    }
}
