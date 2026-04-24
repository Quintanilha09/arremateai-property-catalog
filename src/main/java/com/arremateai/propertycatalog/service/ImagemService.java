package com.arremateai.propertycatalog.service;

import com.arremateai.propertycatalog.domain.ImagemImovel;
import com.arremateai.propertycatalog.domain.Imovel;
import com.arremateai.propertycatalog.dto.ImagemRequest;
import com.arremateai.propertycatalog.dto.ImagemResponse;
import com.arremateai.propertycatalog.repository.ImagemImovelRepository;
import com.arremateai.propertycatalog.repository.ImovelRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImagemService {

    private final ImagemImovelRepository imagemRepo;
    private final ImovelRepository imovelRepo;

    @Transactional
    public ImagemResponse registrarImagem(UUID imovelId, ImagemRequest req) {
        Imovel imovel = imovelRepo.findByIdAndAtivoTrue(imovelId)
                .orElseThrow(() -> new EntityNotFoundException("Imóvel não encontrado: " + imovelId));

        ImagemImovel imagem = new ImagemImovel();
        imagem.setImovel(imovel);
        imagem.setUrl(req.url());
        imagem.setLegenda(req.legenda());
        imagem.setPrincipal(Boolean.TRUE.equals(req.principal()));
        imagem.setOrdem(req.ordem() != null ? req.ordem() : 0);

        if (Boolean.TRUE.equals(req.principal())) {
            imagemRepo.clearPrincipalByImovelId(imovelId);
        }

        imagemRepo.save(imagem);
        return toResponse(imagem);
    }

    @Transactional(readOnly = true)
    public List<ImagemResponse> listarImagens(UUID imovelId) {
        return imagemRepo.findByImovelIdOrderByOrdemAsc(imovelId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ImagemResponse atualizarImagem(UUID imagemId, ImagemRequest req) {
        ImagemImovel imagem = imagemRepo.findById(imagemId)
                .orElseThrow(() -> new EntityNotFoundException("Imagem não encontrada: " + imagemId));
        if (req.url() != null) imagem.setUrl(req.url());
        if (req.legenda() != null) imagem.setLegenda(req.legenda());
        if (req.ordem() != null) imagem.setOrdem(req.ordem());
        imagemRepo.save(imagem);
        return toResponse(imagem);
    }

    @Transactional
    public void definirImagemPrincipal(UUID imagemId) {
        ImagemImovel imagem = imagemRepo.findById(imagemId)
                .orElseThrow(() -> new EntityNotFoundException("Imagem não encontrada: " + imagemId));
        imagemRepo.clearPrincipalByImovelId(imagem.getImovel().getId());
        imagem.setPrincipal(true);
        imagemRepo.save(imagem);
    }

    @Transactional
    public void removerImagem(UUID imagemId) {
        if (!imagemRepo.existsById(imagemId)) {
            throw new EntityNotFoundException("Imagem não encontrada: " + imagemId);
        }
        imagemRepo.deleteById(imagemId);
    }

    private ImagemResponse toResponse(ImagemImovel i) {
        return new ImagemResponse(i.getId(), i.getUrl(), i.getLegenda(), i.getPrincipal(), i.getOrdem());
    }
}
