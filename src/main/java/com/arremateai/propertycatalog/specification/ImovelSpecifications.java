package com.arremateai.propertycatalog.specification;

import com.arremateai.propertycatalog.domain.Imovel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ImovelSpecifications {

    private ImovelSpecifications() {}

    public static Specification<Imovel> apenasAtivos() {
        return (root, query, cb) -> cb.isTrue(root.get("ativo"));
    }

    public static class Builder {
        private final List<Specification<Imovel>> specs = new ArrayList<>();

        public Builder() {
            specs.add(apenasAtivos());
        }

        public Builder comUf(String uf) {
            if (uf != null && !uf.isBlank()) {
                specs.add((root, q, cb) -> cb.equal(cb.upper(root.get("uf")), uf.toUpperCase()));
            }
            return this;
        }

        public Builder comCidade(String cidade) {
            if (cidade != null && !cidade.isBlank()) {
                specs.add((root, q, cb) -> cb.like(cb.lower(root.get("cidade")), "%" + cidade.toLowerCase() + "%"));
            }
            return this;
        }

        public Builder comTipoImovel(String tipoImovel) {
            if (tipoImovel != null && !tipoImovel.isBlank()) {
                specs.add((root, q, cb) -> cb.like(cb.lower(root.get("tipoImovel")), "%" + tipoImovel.toLowerCase() + "%"));
            }
            return this;
        }

        public Builder comInstituicao(String instituicao) {
            if (instituicao != null && !instituicao.isBlank()) {
                specs.add((root, q, cb) -> cb.like(cb.lower(root.get("instituicao")), "%" + instituicao.toLowerCase() + "%"));
            }
            return this;
        }

        public Builder comValorMinimo(BigDecimal valorMin) {
            if (valorMin != null) {
                specs.add((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("valorAvaliacao"), valorMin));
            }
            return this;
        }

        public Builder comValorMaximo(BigDecimal valorMax) {
            if (valorMax != null) {
                specs.add((root, q, cb) -> cb.lessThanOrEqualTo(root.get("valorAvaliacao"), valorMax));
            }
            return this;
        }

        public Builder comBuscaTexto(String busca) {
            if (busca != null && !busca.isBlank()) {
                String pattern = "%" + busca.toLowerCase() + "%";
                specs.add((root, q, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    for (String field : List.of("descricao", "cidade", "bairro", "endereco", "tipoImovel", "instituicao", "numeroLeilao")) {
                        predicates.add(cb.like(cb.lower(root.get(field)), pattern));
                    }
                    return cb.or(predicates.toArray(new Predicate[0]));
                });
            }
            return this;
        }

        public Builder comQuartosMinimo(Integer quartosMin) {
            if (quartosMin != null) {
                specs.add((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("quartos"), quartosMin));
            }
            return this;
        }

        public Builder comBanheirosMinimo(Integer banheirosMin) {
            if (banheirosMin != null) {
                specs.add((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("banheiros"), banheirosMin));
            }
            return this;
        }

        public Builder comVagasMinimo(Integer vagasMin) {
            if (vagasMin != null) {
                specs.add((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("vagas"), vagasMin));
            }
            return this;
        }

        public Builder comAreaMinima(BigDecimal areaMin) {
            if (areaMin != null) {
                specs.add((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("areaTotal"), areaMin));
            }
            return this;
        }

        public Builder comAreaMaxima(BigDecimal areaMax) {
            if (areaMax != null) {
                specs.add((root, q, cb) -> cb.lessThanOrEqualTo(root.get("areaTotal"), areaMax));
            }
            return this;
        }

        public Specification<Imovel> construir() {
            return specs.stream().reduce(Specification::and).orElse(apenasAtivos());
        }
    }
}
