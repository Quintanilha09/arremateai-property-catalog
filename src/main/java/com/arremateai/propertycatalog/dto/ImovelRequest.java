package com.arremateai.propertycatalog.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ImovelRequest(
        @NotBlank(message = "Número do leilão é obrigatório")
        @Size(max = 100)
        String numeroLeilao,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(max = 1000)
        String descricao,

        @NotNull(message = "Valor de avaliação é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor deve ser positivo")
        BigDecimal valorAvaliacao,

        @NotBlank(message = "Data do leilão é obrigatória")
        String dataLeilao,

        @NotBlank(message = "UF é obrigatória")
        @Pattern(regexp = "[A-Z]{2}", message = "UF deve ter 2 letras maiúsculas")
        String uf,

        @NotBlank(message = "Instituição é obrigatória")
        @Size(max = 300)
        String instituicao,

        String linkEdital,
        String linkLeilao,

        @Pattern(regexp = "JUDICIAL|EXTRAJUDICIAL|ADMINISTRATIVO|VOLUNTARIO",
                message = "Tipo de leilão inválido")
        String tipoLeilao,

        String cidade,
        String bairro,
        BigDecimal areaTotal,
        String tipoImovel,
        Integer quartos,
        Integer banheiros,
        Integer vagas,
        String endereco,
        String cep,
        BigDecimal latitude,
        BigDecimal longitude,

        @Pattern(regexp = "NOVO|USADO|REFORMADO", message = "Condição inválida")
        String condicao,

        Boolean aceitaFinanciamento,
        String observacoes,

        @Pattern(regexp = "DISPONIVEL|VENDIDO|SUSPENSO", message = "Status inválido")
        String status
) {}
