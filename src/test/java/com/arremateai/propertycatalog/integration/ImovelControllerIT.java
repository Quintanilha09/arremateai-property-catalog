package com.arremateai.propertycatalog.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class ImovelControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/imoveis retorna 200 com página")
    void deveListarImoveis() throws Exception {
        mockMvc.perform(get("/api/imoveis"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/imoveis/destaques retorna 200")
    void deveListarDestaques() throws Exception {
        mockMvc.perform(get("/api/imoveis/destaques"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/imoveis/recentes retorna 200")
    void deveListarRecentes() throws Exception {
        mockMvc.perform(get("/api/imoveis/recentes"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/imoveis/estatisticas retorna 200")
    void deveObterEstatisticasGerais() throws Exception {
        mockMvc.perform(get("/api/imoveis/estatisticas"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/imoveis/{id} inexistente retorna 404")
    void deveRetornar404ImovelInexistente() throws Exception {
        mockMvc.perform(get("/api/imoveis/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/imoveis/{id} sem headers retorna 401")
    void deveRejeitarDeleteSemHeaders() throws Exception {
        mockMvc.perform(delete("/api/imoveis/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/imoveis/{id} com ROLE_USER retorna 403")
    void deveRejeitarDeleteComRoleUser() throws Exception {
        mockMvc.perform(delete("/api/imoveis/" + UUID.randomUUID())
                        .header("X-User-Id", UUID.randomUUID().toString())
                        .header("X-User-Role", "ROLE_USER"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/imoveis/{id} com ROLE_VENDEDOR retorna 403 (só ADMIN)")
    void deveRejeitarDeleteSemAdmin() throws Exception {
        mockMvc.perform(delete("/api/imoveis/" + UUID.randomUUID())
                        .header("X-User-Id", UUID.randomUUID().toString())
                        .header("X-User-Role", "ROLE_VENDEDOR"))
                .andExpect(status().isForbidden());
    }
}
