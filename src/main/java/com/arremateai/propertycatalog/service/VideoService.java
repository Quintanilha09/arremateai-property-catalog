package com.arremateai.propertycatalog.service;

import com.arremateai.propertycatalog.domain.Imovel;
import com.arremateai.propertycatalog.domain.VideoImovel;
import com.arremateai.propertycatalog.dto.VideoRequest;
import com.arremateai.propertycatalog.dto.VideoResponse;
import com.arremateai.propertycatalog.repository.ImovelRepository;
import com.arremateai.propertycatalog.repository.VideoImovelRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoImovelRepository videoRepo;
    private final ImovelRepository imovelRepo;

    @Transactional
    public VideoResponse registrarVideo(UUID imovelId, VideoRequest req) {
        Imovel imovel = imovelRepo.findByIdAndAtivoTrue(imovelId)
                .orElseThrow(() -> new EntityNotFoundException("Imóvel não encontrado: " + imovelId));

        VideoImovel video = new VideoImovel();
        video.setImovel(imovel);
        video.setUrl(req.url());
        video.setNomeOriginal(req.nomeOriginal());
        video.setTamanho(req.tamanho());
        video.setTipo(req.tipo());
        video.setOrdem(req.ordem() != null ? req.ordem() : 0);

        videoRepo.save(video);
        return toResponse(video);
    }

    @Transactional(readOnly = true)
    public List<VideoResponse> listarVideos(UUID imovelId) {
        return videoRepo.findByImovelIdOrderByOrdemAsc(imovelId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void removerVideo(UUID videoId) {
        if (!videoRepo.existsById(videoId)) {
            throw new EntityNotFoundException("Vídeo não encontrado: " + videoId);
        }
        videoRepo.deleteById(videoId);
    }

    private VideoResponse toResponse(VideoImovel v) {
        return new VideoResponse(v.getId(), v.getUrl(), v.getNomeOriginal(), v.getTamanho(), v.getTipo(), v.getOrdem());
    }
}
