package com.arremateai.propertycatalog.service;

import com.arremateai.propertycatalog.domain.VisualizacaoImovel;
import com.arremateai.propertycatalog.dto.EstatisticasImovelResponse;
import com.arremateai.propertycatalog.repository.ImovelRepository;
import com.arremateai.propertycatalog.repository.VisualizacaoImovelRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisualizacaoService {

    private final VisualizacaoImovelRepository visualizacaoRepo;
    private final ImovelRepository imovelRepo;
    private final RestTemplate restTemplate;

    @Value("${services.userprofile.url:http://localhost:8085}")
    private String userprofileUrl;

    @Transactional
    public void registrarVisualizacao(UUID imovelId, String ipAddress, UUID userId) {
        imovelRepo.findByIdAndAtivoTrue(imovelId)
                .orElseThrow(() -> new EntityNotFoundException("Imóvel não encontrado: " + imovelId));

        // Rate limit: 1 visualização por IP por imóvel a cada 30 minutos
        LocalDateTime limite = LocalDateTime.now().minusMinutes(30);
        if (visualizacaoRepo.existsByImovelIdAndIpAddressAndCreatedAtAfter(imovelId, ipAddress, limite)) {
            return;
        }

        VisualizacaoImovel visualizacao = new VisualizacaoImovel();
        visualizacao.setImovelId(imovelId);
        visualizacao.setIpAddress(ipAddress);
        visualizacao.setUserId(userId);
        visualizacaoRepo.save(visualizacao);
    }

    @Transactional(readOnly = true)
    public EstatisticasImovelResponse buscarEstatisticasImovel(UUID imovelId) {
        imovelRepo.findByIdAndAtivoTrue(imovelId)
                .orElseThrow(() -> new EntityNotFoundException("Imóvel não encontrado: " + imovelId));

        long totalVisualizacoes = visualizacaoRepo.countByImovelId(imovelId);
        long visitantesUnicos = visualizacaoRepo.countDistinctIpByImovelId(imovelId);

        // Buscar última visualização
        LocalDateTime trintaDiasAtras = LocalDateTime.now().minusDays(30);
        List<VisualizacaoImovel> recentes = visualizacaoRepo
                .findByImovelIdAndCreatedAtAfterOrderByCreatedAtDesc(imovelId, trintaDiasAtras);

        String ultimaVisualizacao = recentes.isEmpty() ? null :
                recentes.get(0).getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // Visualizações por dia (últimos 30 dias)
        Map<String, Long> visualizacoesPorDia = recentes.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getCreatedAt().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                        Collectors.counting()
                ));

        // Preencher dias sem visualizações com 0
        LocalDate hoje = LocalDate.now();
        for (int i = 0; i < 30; i++) {
            String dia = hoje.minusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE);
            visualizacoesPorDia.putIfAbsent(dia, 0L);
        }
        visualizacoesPorDia = new TreeMap<>(visualizacoesPorDia);

        // Buscar total de favoritos via userprofile
        long totalFavoritos = buscarTotalFavoritos(imovelId);

        return new EstatisticasImovelResponse(
                totalVisualizacoes,
                visitantesUnicos,
                totalFavoritos,
                ultimaVisualizacao,
                visualizacoesPorDia
        );
    }

    @Transactional(readOnly = true)
    public List<UUID> buscarMaisVisualizados(int limite) {
        return visualizacaoRepo.findMaisVisualizados(limite).stream()
                .map(row -> (UUID) row[0])
                .toList();
    }

    private long buscarTotalFavoritos(UUID imovelId) {
        try {
            String url = userprofileUrl + "/api/favoritos/imovel/" + imovelId + "/count";
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("total")) {
                return ((Number) response.get("total")).longValue();
            }
        } catch (Exception e) {
            log.warn("Erro ao buscar favoritos do imóvel {} no userprofile: {}", imovelId, e.getMessage());
        }
        return 0;
    }
}
