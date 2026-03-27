package com.arremateai.propertycatalog.service;

import com.arremateai.propertycatalog.dto.CidadeResponse;
import com.arremateai.propertycatalog.dto.EstadoResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocalizacaoService {

    private static final String IBGE_API_BASE = "https://servicodados.ibge.gov.br/api/v1/localidades";

    private final RestClient restClient;

    public LocalizacaoService() {
        this.restClient = RestClient.create();
    }

    public List<EstadoResponse> listarEstados() {
        EstadoIBGE[] estados = restClient.get()
                .uri(IBGE_API_BASE + "/estados?orderBy=nome")
                .retrieve()
                .body(EstadoIBGE[].class);

        if (estados == null) return List.of();

        return List.of(estados).stream()
                .map(e -> new EstadoResponse(e.sigla(), e.nome()))
                .collect(Collectors.toList());
    }

    public List<CidadeResponse> listarCidadesPorEstado(String estadoSigla) {
        MunicipioIBGE[] municipios = restClient.get()
                .uri(IBGE_API_BASE + "/estados/" + estadoSigla + "/municipios?orderBy=nome")
                .retrieve()
                .body(MunicipioIBGE[].class);

        if (municipios == null) return List.of();

        return List.of(municipios).stream()
                .map(m -> new CidadeResponse(m.nome()))
                .collect(Collectors.toList());
    }

    private record EstadoIBGE(Integer id, String sigla, String nome, RegiaoIBGE regiao) {}
    private record RegiaoIBGE(Integer id, String sigla, String nome) {}
    private record MunicipioIBGE(Integer id, String nome) {}
}
