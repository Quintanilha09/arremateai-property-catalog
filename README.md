# 🏠 ArremateAI - Property Catalog Service

Microsserviço responsável por gerenciar o catálogo de imóveis, leilões, buscas avançadas e produtos relacionados.

## 📋 Descrição

O Property Catalog é o coração do negócio ArremateAI. Implementa:

- **CRUD completo de imóveis** (residências, terrenos, comerciais)
- **Gestão de leilões** vinculados a imóveis
- **Busca avançada** com múltiplos filtros (localização, preço, tipo, características)
- **Gerenciamento de imagens e vídeos** dos imóveis
- **Integração com leiloeiras** (Caixa, Santander, etc.)
- **Produtos relacionados** (seguros, financiamentos)
- **Especificações dinâmicas** para queries complexas

## 🛠️ Tecnologias

- **Java 17** (LTS)
- **Spring Boot 3.2.2**
- **Spring Data JPA** - Persistência
- **PostgreSQL 16** - Banco de dados
- **Redis 7** - Cache de buscas
- **MapStruct 1.5.5** - Mapeamento DTO/Entity
- **Hibernate Spatial** - Queries geoespaciais
- **Specifications Pattern** - Queries dinâmicas

## 🏗️ Arquitetura

```
┌──────────────────┐
│  Gateway :8080   │
└────────┬─────────┘
         │
         ▼
┌─────────────────────────────┐
│  Property Catalog Service   │
│      (Port 8084)            │
├─────────────────────────────┤
│ Controllers                 │
│  ├─ ImovelController        │
│  ├─ ProdutoController       │
│  └─ LocalizacaoController   │
├─────────────────────────────┤
│ Services                    │
│  ├─ ImovelService           │
│  ├─ ImagemService           │
│  ├─ VideoService            │
│  ├─ LocalizacaoService      │
│  └─ ProdutoService          │
├─────────────────────────────┤
│ Specifications              │
│  └─ ImovelSpecifications    │
└─────────┬─────────┬─────────┘
          │         │
          ▼         ▼
    PostgreSQL    Redis
     (5434)      (6380)
```

## 📦 Estrutura do Projeto

```
src/main/java/com/arremateai/propertycatalog/
├── PropertyCatalogApplication.java
├── controller/
│   ├── ImovelController.java         # CRUD de imóveis
│   ├── ProdutoController.java        # Produtos (seguros, financiamentos)
│   └── LocalizacaoController.java    # Estados/cidades
├── domain/
│   ├── Imovel.java                   # Entidade principal
│   ├── ImagemImovel.java             # Imagens do imóvel
│   ├── VideoImovel.java              # Vídeos do imóvel
│   ├── Leilao.java                   # Leilões
│   ├── Leiloeira.java                # Leiloeiras (Caixa, etc.)
│   ├── Produto.java                  # Produtos relacionados
│   ├── TipoImovel.java               # Enum (RESIDENCIAL, COMERCIAL, TERRENO)
│   └── StatusImovel.java             # Enum (ATIVO, VENDIDO, REMOVIDO)
├── dto/
│   ├── ImovelRequest.java
│   ├── ImovelResponse.java
│   ├── BuscaDTO.java                 # Filtros de busca
│   ├── ImagemRequest.java
│   ├── VideoRequest.java
│   └── ProdutoResponse.java
├── repository/
│   ├── ImovelRepository.java
│   ├── ImagemImovelRepository.java
│   ├── VideoImovelRepository.java
│   ├── LeilaoRepository.java
│   └── ProdutoRepository.java
├── service/
│   ├── ImovelService.java
│   ├── ImagemService.java
│   ├── VideoService.java
│   ├── LocalizacaoService.java
│   └── ProdutoService.java
├── specification/
│   └── ImovelSpecifications.java     # Dynamic queries
└── converter/
    └── StringArrayConverter.java     # JPA array handling
```

## 🚀 Endpoints Principais

### Imóveis

#### POST `/api/imoveis`
Criar novo imóvel.

**Headers:**
```
Authorization: Bearer {token}
```

**Request:**
```json
{
  "titulo": "Casa 3 quartos no Jardim Paulista",
  "descricao": "Casa espaçosa com 3 quartos, 2 banheiros...",
  "tipo": "RESIDENCIAL",
  "status": "ATIVO",
  "preco": 850000.00,
  "area": 180.5,
  "quartos": 3,
  "banheiros": 2,
  "vagas_garagem": 2,
  "endereco": {
    "cep": "01452-000",
    "rua": "Rua Augusta",
    "numero": "1234",
    "complemento": "Casa",
    "bairro": "Jardim Paulista",
    "cidade": "São Paulo",
    "estado": "SP"
  },
  "caracteristicas": [
    "Piscina",
    "Área de churrasqueira",
    "Jardim"
  ],
  "imagens": [
    {
      "url": "https://cdn.arremateai.com/imoveis/abc123/sala.jpg",
      "ordem": 1,
      "isPrincipal": true
    }
  ]
}
```

**Response 201:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "titulo": "Casa 3 quartos no Jardim Paulista",
  "preco": 850000.00,
  "tipo": "RESIDENCIAL",
  "status": "ATIVO",
  "endereco": {
    "cidade": "São Paulo",
    "estado": "SP",
    "bairro": "Jardim Paulista"
  },
  "imagemPrincipal": "https://cdn.arremateai.com/imoveis/abc123/sala.jpg",
  "createdAt": "2026-03-27T10:30:00Z"
}
```

#### GET `/api/imoveis`
Buscar imóveis com filtros.

**Query Parameters:**
```
?cidade=São Paulo
&estado=SP
&tipoImovel=RESIDENCIAL
&precoMin=500000
&precoMax=1000000
&quartos=3
&banheiros=2
&areaMin=150
&page=0
&size=20
&sort=preco,asc
```

**Response 200:**
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "titulo": "Casa 3 quartos no Jardim Paulista",
      "preco": 850000.00,
      "tipo": "RESIDENCIAL",
      "area": 180.5,
      "quartos": 3,
      "banheiros": 2,
      "endereco": {
        "cidade": "São Paulo",
        "estado": "SP"
      },
      "imagemPrincipal": "https://cdn.arremateai.com/..."
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 145,
  "totalPages": 8
}
```

#### GET `/api/imoveis/{id}`
Detalhes completos do imóvel.

**Response 200:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "titulo": "Casa 3 quartos no Jardim Paulista",
  "descricao": "Casa espaçosa com...",
  "preco": 850000.00,
  "tipo": "RESIDENCIAL",
  "status": "ATIVO",
  "area": 180.5,
  "quartos": 3,
  "banheiros": 2,
  "vagasGaragem": 2,
  "endereco": {
    "cep": "01452-000",
    "rua": "Rua Augusta",
    "numero": "1234",
    "cidade": "São Paulo",
    "estado": "SP"
  },
  "caracteristicas": [
    "Piscina",
    "Área de churrasqueira"
  ],
  "imagens": [
    {
      "id": "img-001",
      "url": "https://cdn.arremateai.com/imoveis/abc123/sala.jpg",
      "ordem": 1,
      "isPrincipal": true
    }
  ],
  "videos": [
    {
      "id": "video-001",
      "url": "https://cdn.arremateai.com/imoveis/abc123/tour.mp4",
      "titulo": "Tour Virtual"
    }
  ],
  "vendedor": {
    "id": "vendedor-uuid",
    "nome": "Imobiliária XYZ"
  },
  "visualizacoes": 342,
  "favoritadoPor": 15,
  "createdAt": "2026-03-27T10:30:00Z",
  "updatedAt": "2026-03-27T10:30:00Z"
}
```

#### PUT `/api/imoveis/{id}`
Atualizar imóvel (apenas proprietário ou admin).

#### DELETE `/api/imoveis/{id}`
Soft delete do imóvel (muda status para REMOVIDO).

### Busca Avançada

#### POST `/api/imoveis/busca-avancada`
Busca com múltiplos critérios dinâmicos.

**Request:**
```json
{
  "filtros": {
    "tipos": ["RESIDENCIAL", "APARTAMENTO"],
    "estados": ["SP", "RJ"],
    "precoMin": 300000,
    "precoMax": 800000,
    "quartosMin": 2,
    "caracteristicas": ["Piscina", "Portaria 24h"],
    "emLeilao": true
  },
  "ordenacao": {
    "campo": "preco",
    "direcao": "ASC"
  },
  "paginacao": {
    "page": 0,
    "size": 20
  }
}
```

### Localização

#### GET `/api/localizacao/estados`
Lista todos os estados brasileiros.

**Response 200:**
```json
[
  { "uf": "SP", "nome": "São Paulo" },
  { "uf": "RJ", "nome": "Rio de Janeiro" }
]
```

#### GET `/api/localizacao/estados/{uf}/cidades`
Lista cidades de um estado.

**Response 200:**
```json
[
  "São Paulo",
  "Campinas",
  "Santos"
]
```

### Leilões

#### GET `/api/leiloes`
Lista leilões ativos.

**Response 200:**
```json
[
  {
    "id": "leilao-uuid",
    "titulo": "Leilão Caixa - 1º Leilão SP",
    "dataInicio": "2026-04-01T10:00:00Z",
    "dataFim": "2026-04-01T16:00:00Z",
    "leiloeira": {
      "id": "caixa-sp",
      "nome": "Caixa Econômica Federal - SP"
    },
    "totalImoveis": 45,
    "status": "ATIVO"
  }
]
```

#### GET `/api/leiloes/{id}/imoveis`
Imóveis de um leilão específico.

### Produtos

#### GET `/api/produtos`
Lista produtos relacionados (seguros, financiamentos).

**Response 200:**
```json
[
  {
    "id": "produto-uuid",
    "nome": "Seguro Residencial Premium",
    "tipo": "SEGURO",
    "descricao": "Cobertura completa para residências",
    "precoBase": 89.90,
    "parceiro": "Seguradora ABC"
  }
]
```

## 🔍 Busca com Specifications

O serviço utiliza **Spring Data JPA Specifications** para construir queries dinâmicas:

```java
public class ImovelSpecifications {
    public static Specification<Imovel> comTipo(TipoImovel tipo) {
        return (root, query, cb) -> 
            tipo == null ? null : cb.equal(root.get("tipo"), tipo);
    }
    
    public static Specification<Imovel> comPrecoEntre(Double min, Double max) {
        return (root, query, cb) -> {
            if (min != null && max != null) {
                return cb.between(root.get("preco"), min, max);
            }
            if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("preco"), min);
            }
            if (max != null) {
                return cb.lessThanOrEqualTo(root.get("preco"), max);
            }
            return null;
        };
    }
}
```

## ⚙️ Variáveis de Ambiente

```bash
# Server
SERVER_PORT=8084

# Database
DB_HOST=localhost
DB_PORT=5434
DB_NAME=property_catalog_db
DB_USERNAME=postgres
DB_PASSWORD=postgres

# Redis (Cache)
REDIS_HOST=localhost
REDIS_PORT=6380
CACHE_TTL=3600

# AWS S3 (Imagens)
AWS_REGION=us-east-1
AWS_S3_BUCKET=arremateai-property-images
AWS_CLOUDFRONT_DOMAIN=cdn.arremateai.com

# Media Service Integration
MEDIA_SERVICE_URL=http://localhost:8085

# Pagination
DEFAULT_PAGE_SIZE=20
MAX_PAGE_SIZE=100

# Search
SEARCH_CACHE_ENABLED=true
SEARCH_CACHE_TTL=1800
```

## 🏃 Como Executar

```bash
# Clone o repositório
git clone https://github.com/Quintanilha09/arremateai-property-catalog.git
cd arremateai-property-catalog

# Suba o banco de dados
docker-compose up -d postgres redis

# Execute a aplicação
./mvnw spring-boot:run
```

## 📊 Banco de Dados

### Schema Principal

#### `imovel`
```sql
CREATE TABLE imovel (
    id UUID PRIMARY KEY,
    vendedor_id UUID NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    tipo VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    preco DECIMAL(15,2) NOT NULL,
    area DECIMAL(10,2),
    quartos INT,
    banheiros INT,
    vagas_garagem INT,
    cep VARCHAR(9),
    endereco_completo TEXT,
    cidade VARCHAR(100),
    estado VARCHAR(2),
    caracteristicas TEXT[],
    visualizacoes INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_imovel_cidade ON imovel(cidade);
CREATE INDEX idx_imovel_estado ON imovel(estado);
CREATE INDEX idx_imovel_tipo ON imovel(tipo);
CREATE INDEX idx_imovel_preco ON imovel(preco);
CREATE INDEX idx_imovel_status ON imovel(status);
```

#### `imagem_imovel`
```sql
CREATE TABLE imagem_imovel (
    id UUID PRIMARY KEY,
    imovel_id UUID NOT NULL REFERENCES imovel(id) ON DELETE CASCADE,
    url VARCHAR(500) NOT NULL,
    ordem INT DEFAULT 0,
    is_principal BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW()
);
```

## 🧪 Testes

```bash
# Unit tests
./mvnw test

# Integration tests (requer Docker)
./mvnw verify

# Coverage
./mvnw jacoco:report
```

## 📈 Performance

### Caching Strategy
- **Buscas populares**: Cache Redis (30 min)
- **Detalhes de imóvel**: Cache Redis (1 hora)
- **Listings**: Paginação com cache
- **Imagens**: CDN CloudFront

### Índices de Banco
- `cidade`, `estado`, `tipo`, `preco`, `status`
- Composite index: `(estado, cidade, tipo)`
- Full-text search em `titulo` e `descricao`

## 📄 Licença

Proprietary - © 2026 ArremateAI
