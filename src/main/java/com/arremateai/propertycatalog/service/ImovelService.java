package com.arremateai.propertycatalog.service;

import com.arremateai.propertycatalog.domain.Imovel;
import com.arremateai.propertycatalog.dto.ImagemResponse;
import com.arremateai.propertycatalog.dto.ImovelRequest;
import com.arremateai.propertycatalog.dto.ImovelResponse;
import com.arremateai.propertycatalog.dto.VideoResponse;
import com.arremateai.propertycatalog.exception.BusinessException;
import com.arremateai.propertycatalog.repository.ImagemImovelRepository;
import com.arremateai.propertycatalog.repository.ImovelRepository;
import com.arremateai.propertycatalog.repository.VideoImovelRepository;
import com.arremateai.propertycatalog.specification.ImovelSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImovelService {

    private final ImovelRepository imovelRepo;
    private final ImagemImovelRepository imagemRepo;
    private final VideoImovelRepository videoRepo;

    @Transactional(readOnly = true)
    public Page<ImovelResponse> buscarComFiltros(String uf, String cidade, String tipoImovel,
                                                  String instituicao, BigDecimal valorMin, BigDecimal valorMax,
                                                  String busca, Integer quartosMin, Integer banheirosMin,
                                                  Integer vagasMin, BigDecimal areaMin, BigDecimal areaMax,
                                                  Pageable pageable) {
        Specification<Imovel> spec = new ImovelSpecifications.Builder()
                .comUf(uf)
                .comCidade(cidade)
                .comTipoImovel(tipoImovel)
                .comInstituicao(instituicao)
                .comValorMinimo(valorMin)
                .comValorMaximo(valorMax)
                .comBuscaTexto(busca)
                .comQuartosMinimo(quartosMin)
                .comBanheirosMinimo(banheirosMin)
                .comVagasMinimo(vagasMin)
                .comAreaMinima(areaMin)
                .comAreaMaxima(areaMax)
                .construir();
        return imovelRepo.findAll(spec, pageable).map(this::converterParaResponse);
    }

    @Transactional(readOnly = true)
    public ImovelResponse buscarPorId(UUID id) {
        Imovel imovel = imovelRepo.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Imóvel não encontrado: " + id));
        return converterParaResponse(imovel);
    }

    @Transactional(readOnly = true)
    public Page<ImovelResponse> buscarImoveisPorUsuario(UUID vendedorId, String status, Pageable pageable) {
        Page<Imovel> page = (status != null && !status.isBlank())
                ? imovelRepo.findByVendedorIdAndStatus(vendedorId, status, pageable)
                : imovelRepo.findByVendedorId(vendedorId, pageable);
        return page.map(this::converterParaResponse);
    }

    @Transactional
    public ImovelResponse cadastrarImovel(ImovelRequest req, UUID vendedorId) {
        if (imovelRepo.existsByNumeroLeilao(req.numeroLeilao())) {
            throw new BusinessException("Já existe um imóvel com o número de leilão: " + req.numeroLeilao());
        }
        Imovel imovel = new Imovel();
        preencherImovel(imovel, req);
        imovel.setVendedorId(vendedorId);
        imovelRepo.save(imovel);
        return converterParaResponse(imovel);
    }

    @Transactional
    public ImovelResponse atualizarImovel(UUID id, ImovelRequest req) {
        Imovel imovel = buscarEntidade(id);
        preencherImovel(imovel, req);
        imovelRepo.save(imovel);
        return converterParaResponse(imovel);
    }

    @Transactional
    public ImovelResponse atualizarParcial(UUID id, ImovelRequest req) {
        Imovel imovel = buscarEntidade(id);
        if (req.descricao() != null) imovel.setDescricao(req.descricao());
        if (req.valorAvaliacao() != null) imovel.setValorAvaliacao(req.valorAvaliacao());
        if (req.dataLeilao() != null) imovel.setDataLeilao(LocalDate.parse(req.dataLeilao()));
        if (req.uf() != null) imovel.setUf(req.uf());
        if (req.instituicao() != null) imovel.setInstituicao(req.instituicao());
        if (req.linkEdital() != null) imovel.setLinkEdital(req.linkEdital());
        if (req.linkLeilao() != null) imovel.setLinkLeilao(req.linkLeilao());
        if (req.tipoLeilao() != null) imovel.setTipoLeilao(req.tipoLeilao());
        if (req.cidade() != null) imovel.setCidade(req.cidade());
        if (req.bairro() != null) imovel.setBairro(req.bairro());
        if (req.areaTotal() != null) imovel.setAreaTotal(req.areaTotal());
        if (req.tipoImovel() != null) imovel.setTipoImovel(req.tipoImovel());
        if (req.quartos() != null) imovel.setQuartos(req.quartos());
        if (req.banheiros() != null) imovel.setBanheiros(req.banheiros());
        if (req.vagas() != null) imovel.setVagas(req.vagas());
        if (req.endereco() != null) imovel.setEndereco(req.endereco());
        if (req.cep() != null) imovel.setCep(req.cep());
        if (req.latitude() != null) imovel.setLatitude(req.latitude());
        if (req.longitude() != null) imovel.setLongitude(req.longitude());
        if (req.condicao() != null) imovel.setCondicao(req.condicao());
        if (req.aceitaFinanciamento() != null) imovel.setAceitaFinanciamento(req.aceitaFinanciamento());
        if (req.observacoes() != null) imovel.setObservacoes(req.observacoes());
        if (req.status() != null) imovel.setStatus(req.status());
        imovelRepo.save(imovel);
        return converterParaResponse(imovel);
    }

    @Transactional(readOnly = true)
    public List<ImovelResponse> buscarDestaques(int limite) {
        Pageable pageable = PageRequest.of(0, limite, Sort.by(Sort.Direction.DESC, "valorAvaliacao"));
        return imovelRepo.findAll(pageable).stream()
                .filter(i -> Boolean.TRUE.equals(i.getAtivo()))
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ImovelResponse> buscarRecentes(int limite) {
        Pageable pageable = PageRequest.of(0, limite, Sort.by(Sort.Direction.DESC, "createdAt"));
        return imovelRepo.findAll(pageable).stream()
                .filter(i -> Boolean.TRUE.equals(i.getAtivo()))
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ImovelResponse> buscarMaisProcurados(int limite, VisualizacaoService visualizacaoService) {
        List<UUID> maisVisualizadosIds = visualizacaoService.buscarMaisVisualizados(limite);
        if (maisVisualizadosIds.isEmpty()) {
            // Fallback: se não há visualizações, retorna os mais recentes
            return buscarRecentes(limite);
        }
        List<ImovelResponse> result = maisVisualizadosIds.stream()
                .map(id -> imovelRepo.findByIdAndAtivoTrue(id).orElse(null))
                .filter(java.util.Objects::nonNull)
                .map(this::converterParaResponse)
                .toList();
        if (result.isEmpty()) {
            return buscarRecentes(limite);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> buscarEstatisticas() {
        long totalImoveis = imovelRepo.count();
        long totalAtivos = imovelRepo.findAll().stream().filter(i -> Boolean.TRUE.equals(i.getAtivo())).count();
        long totalInativos = totalImoveis - totalAtivos;

        Map<String, Long> totalPorUf = new java.util.LinkedHashMap<>();
        Map<String, Long> totalPorCidade = new java.util.LinkedHashMap<>();
        imovelRepo.findAll().stream()
                .filter(i -> Boolean.TRUE.equals(i.getAtivo()))
                .forEach(i -> {
                    if (i.getUf() != null) totalPorUf.merge(i.getUf(), 1L, Long::sum);
                    if (i.getCidade() != null) totalPorCidade.merge(i.getCidade(), 1L, Long::sum);
                });

        Map<String, Object> result = new HashMap<>();
        result.put("totalImoveis", totalImoveis);
        result.put("totalAtivos", totalAtivos);
        result.put("totalInativos", totalInativos);
        result.put("totalPorUf", totalPorUf);
        result.put("totalPorCidade", totalPorCidade);
        return result;
    }

    @Transactional
    public void removerImovel(UUID id) {
        Imovel imovel = buscarEntidade(id);
        imovel.setAtivo(false);
        imovelRepo.save(imovel);
    }

    @Transactional
    public ImovelResponse atualizarStatus(UUID id, String novoStatus, UUID userId, String role) {
        Imovel imovel = buscarEntidade(id);
        boolean isAdmin = "ROLE_ADMIN".equals(role);
        boolean isOwner = imovel.getVendedorId() != null && imovel.getVendedorId().equals(userId);
        if (!isAdmin && !isOwner) {
            throw new BusinessException("Acesso negado: você não tem permissão para alterar este imóvel");
        }
        imovel.setStatus(novoStatus);
        imovelRepo.save(imovel);
        return converterParaResponse(imovel);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> validarImovel(UUID id) {
        Imovel imovel = buscarEntidade(id);
        long qtdImagens = imagemRepo.countByImovelId(id);
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("id", imovel.getId());
        resultado.put("status", imovel.getStatus());
        resultado.put("temImagens", qtdImagens > 0);
        resultado.put("quantidadeImagens", qtdImagens);
        resultado.put("isValido", qtdImagens > 0 && imovel.getAtivo());
        return resultado;
    }

    // ---- helpers ----

    private Imovel buscarEntidade(UUID id) {
        return imovelRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Imóvel não encontrado: " + id));
    }

    private void preencherImovel(Imovel imovel, ImovelRequest req) {
        imovel.setNumeroLeilao(req.numeroLeilao());
        imovel.setDescricao(req.descricao());
        imovel.setValorAvaliacao(req.valorAvaliacao());
        imovel.setDataLeilao(LocalDate.parse(req.dataLeilao()));
        imovel.setUf(req.uf());
        imovel.setInstituicao(req.instituicao());
        imovel.setLinkEdital(req.linkEdital());
        imovel.setLinkLeilao(req.linkLeilao());
        imovel.setTipoLeilao(req.tipoLeilao());
        imovel.setCidade(req.cidade());
        imovel.setBairro(req.bairro());
        imovel.setAreaTotal(req.areaTotal());
        imovel.setTipoImovel(req.tipoImovel());
        imovel.setQuartos(req.quartos());
        imovel.setBanheiros(req.banheiros());
        imovel.setVagas(req.vagas());
        imovel.setEndereco(req.endereco());
        imovel.setCep(req.cep());
        imovel.setLatitude(req.latitude());
        imovel.setLongitude(req.longitude());
        imovel.setCondicao(req.condicao());
        imovel.setAceitaFinanciamento(req.aceitaFinanciamento());
        imovel.setObservacoes(req.observacoes());
        if (req.status() != null) imovel.setStatus(req.status());
    }

    ImovelResponse converterParaResponse(Imovel imovel) {
        List<ImagemResponse> imagens = imagemRepo.findByImovelIdOrderByOrdemAsc(imovel.getId())
                .stream()
                .map(i -> new ImagemResponse(i.getId(), i.getUrl(), i.getLegenda(), i.getPrincipal(), i.getOrdem()))
                .toList();

        List<VideoResponse> videos = videoRepo.findByImovelIdOrderByOrdemAsc(imovel.getId())
                .stream()
                .map(v -> new VideoResponse(v.getId(), v.getUrl(), v.getNomeOriginal(), v.getTamanho(), v.getTipo(), v.getOrdem()))
                .toList();

        String imagemPrincipal = imagens.stream()
                .filter(img -> Boolean.TRUE.equals(img.principal()))
                .map(ImagemResponse::url)
                .findFirst()
                .orElse(imagens.isEmpty() ? null : imagens.get(0).url());

        return new ImovelResponse(
                imovel.getId(),
                imovel.getNumeroLeilao(),
                imovel.getDescricao(),
                imovel.getValorAvaliacao(),
                imovel.getDataLeilao() != null ? imovel.getDataLeilao().toString() : null,
                imovel.getUf(),
                imovel.getInstituicao(),
                imovel.getLinkEdital(),
                imovel.getLinkLeilao(),
                imovel.getTipoLeilao(),
                imovel.getCidade(),
                imovel.getBairro(),
                imovel.getAreaTotal(),
                imovel.getTipoImovel(),
                imovel.getQuartos(),
                imovel.getBanheiros(),
                imovel.getVagas(),
                imovel.getEndereco(),
                imovel.getCep(),
                imovel.getLatitude(),
                imovel.getLongitude(),
                imovel.getCondicao(),
                imovel.getAceitaFinanciamento(),
                imovel.getObservacoes(),
                imovel.getStatus(),
                imovel.getVendedorId(),
                imagens,
                imagemPrincipal,
                videos
        );
    }
}
