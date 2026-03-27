package com.arremateai.propertycatalog.controller;

import com.arremateai.propertycatalog.dto.CidadeResponse;
import com.arremateai.propertycatalog.dto.EstadoResponse;
import com.arremateai.propertycatalog.service.LocalizacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/localizacao")
@RequiredArgsConstructor
public class LocalizacaoController {

    private final LocalizacaoService localizacaoService;

    @GetMapping("/estados")
    public ResponseEntity<List<EstadoResponse>> listarEstados() {
        return ResponseEntity.ok(localizacaoService.listarEstados());
    }

    @GetMapping("/estados/{sigla}/cidades")
    public ResponseEntity<List<CidadeResponse>> listarCidades(@PathVariable String sigla) {
        return ResponseEntity.ok(localizacaoService.listarCidadesPorEstado(sigla.toUpperCase()));
    }
}
