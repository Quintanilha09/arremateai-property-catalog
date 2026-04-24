package com.arremateai.propertycatalog.controller;

import com.arremateai.propertycatalog.dto.AtualizarStatusRequest;
import com.arremateai.propertycatalog.dto.EstatisticasImovelResponse;
import com.arremateai.propertycatalog.dto.ImagemRequest;
import com.arremateai.propertycatalog.dto.ImagemResponse;
import com.arremateai.propertycatalog.dto.ImovelRequest;
import com.arremateai.propertycatalog.dto.ImovelResponse;
import com.arremateai.propertycatalog.dto.VideoRequest;
import com.arremateai.propertycatalog.dto.VideoResponse;
import com.arremateai.propertycatalog.service.ImagemService;
import com.arremateai.propertycatalog.service.ImovelService;
import com.arremateai.propertycatalog.service.VideoService;
import com.arremateai.propertycatalog.service.VisualizacaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/imoveis")
@RequiredArgsConstructor
public class ImovelController {

    private final ImovelService imovelService;
    private final ImagemService imagemService;
    private final VideoService videoService;
    private final VisualizacaoService visualizacaoService;

    // ---- public endpoints ----

    @GetMapping
    public ResponseEntity<Page<ImovelResponse>> listar(
            @RequestParam(required = false) String uf,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String tipoImovel,
            @RequestParam(required = false) String instituicao,
            @RequestParam(required = false) BigDecimal valorMin,
            @RequestParam(required = false) BigDecimal valorMax,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) Integer quartosMin,
            @RequestParam(required = false) Integer banheirosMin,
            @RequestParam(required = false) Integer vagasMin,
            @RequestParam(required = false) BigDecimal areaMin,
            @RequestParam(required = false) BigDecimal areaMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction dir = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sort = sortBy != null ? sortBy : "createdAt";
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sort));

        return ResponseEntity.ok(imovelService.buscarComFiltros(uf, cidade, tipoImovel, instituicao,
                valorMin, valorMax, busca, quartosMin, banheirosMin, vagasMin, areaMin, areaMax, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImovelResponse> buscarPorId(
            @PathVariable UUID id,
            HttpServletRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        // Registrar visualização ao acessar detalhes do imóvel
        String ip = getClientIp(request);
        UUID userUuid = userId != null ? UUID.fromString(userId) : null;
        visualizacaoService.registrarVisualizacao(id, ip, userUuid);
        return ResponseEntity.ok(imovelService.buscarPorId(id));
    }

    @GetMapping("/destaques")
    public ResponseEntity<List<ImovelResponse>> destaques(
            @RequestParam(defaultValue = "6") int limite) {
        return ResponseEntity.ok(imovelService.buscarDestaques(limite));
    }

    @GetMapping("/recentes")
    public ResponseEntity<List<ImovelResponse>> recentes(
            @RequestParam(defaultValue = "6") int limite) {
        return ResponseEntity.ok(imovelService.buscarRecentes(limite));
    }

    @GetMapping("/mais-procurados")
    public ResponseEntity<List<ImovelResponse>> maisProcurados(
            @RequestParam(defaultValue = "6") int limite) {
        return ResponseEntity.ok(imovelService.buscarMaisProcurados(limite, visualizacaoService));
    }

    @GetMapping("/{id}/estatisticas")
    public ResponseEntity<EstatisticasImovelResponse> estatisticasImovel(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        return ResponseEntity.ok(visualizacaoService.buscarEstatisticasImovel(id));
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> estatisticas() {
        return ResponseEntity.ok(imovelService.buscarEstatisticas());
    }

    @GetMapping("/{id}/validar")
    public ResponseEntity<Map<String, Object>> validar(@PathVariable UUID id) {
        return ResponseEntity.ok(imovelService.validarImovel(id));
    }

    @GetMapping("/{id}/imagens")
    public ResponseEntity<List<ImagemResponse>> listarImagens(@PathVariable UUID id) {
        return ResponseEntity.ok(imagemService.listarImagens(id));
    }

    @GetMapping("/{id}/videos")
    public ResponseEntity<List<VideoResponse>> listarVideos(@PathVariable UUID id) {
        return ResponseEntity.ok(videoService.listarVideos(id));
    }

    // ---- authenticated endpoints ----

    @PostMapping
    public ResponseEntity<ImovelResponse> criar(
            @Valid @RequestBody ImovelRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {

        if (!"ROLE_VENDEDOR".equals(role) && !"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        ImovelResponse response = imovelService.cadastrarImovel(request, UUID.fromString(userId));
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ImovelResponse> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ImovelRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {

        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(imovelService.atualizarImovel(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ImovelResponse> atualizarParcial(
            @PathVariable UUID id,
            @RequestBody ImovelRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {

        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(imovelService.atualizarParcial(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {

        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        imovelService.removerImovel(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ImovelResponse> atualizarStatus(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarStatusRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {

        return ResponseEntity.ok(
                imovelService.atualizarStatus(id, request.status(), UUID.fromString(userId), role));
    }

    @GetMapping("/meus")
    public ResponseEntity<Page<ImovelResponse>> meus(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(imovelService.buscarImoveisPorUsuario(UUID.fromString(userId), status, pageable));
    }

    // ---- image management ----

    @PostMapping("/{id}/imagens")
    public ResponseEntity<ImagemResponse> registrarImagem(
            @PathVariable UUID id,
            @Valid @RequestBody ImagemRequest request,
            @RequestHeader("X-User-Id") String userId) {

        return ResponseEntity.status(201).body(imagemService.registrarImagem(id, request));
    }

    @PutMapping("/imagens/{imagemId}")
    public ResponseEntity<ImagemResponse> atualizarImagem(
            @PathVariable UUID imagemId,
            @RequestBody ImagemRequest request,
            @RequestHeader("X-User-Id") String userId) {

        return ResponseEntity.ok(imagemService.atualizarImagem(imagemId, request));
    }

    @PatchMapping("/imagens/{imagemId}/principal")
    public ResponseEntity<Void> definirPrincipal(
            @PathVariable UUID imagemId,
            @RequestHeader("X-User-Id") String userId) {

        imagemService.definirImagemPrincipal(imagemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/imagens/{imagemId}")
    public ResponseEntity<Void> removerImagem(
            @PathVariable UUID imagemId,
            @RequestHeader("X-User-Id") String userId) {

        imagemService.removerImagem(imagemId);
        return ResponseEntity.noContent().build();
    }

    // ---- video management ----

    @PostMapping("/{id}/videos")
    public ResponseEntity<VideoResponse> registrarVideo(
            @PathVariable UUID id,
            @Valid @RequestBody VideoRequest request,
            @RequestHeader("X-User-Id") String userId) {

        return ResponseEntity.status(201).body(videoService.registrarVideo(id, request));
    }

    @DeleteMapping("/videos/{videoId}")
    public ResponseEntity<Void> removerVideo(
            @PathVariable UUID videoId,
            @RequestHeader("X-User-Id") String userId) {

        videoService.removerVideo(videoId);
        return ResponseEntity.noContent().build();
    }

    // ---- helpers ----

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
