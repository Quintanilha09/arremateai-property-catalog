-- V001__schema_property_catalog.sql

CREATE TABLE IF NOT EXISTS imovel (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero_leilao VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(1000) NOT NULL,
    valor_avaliacao NUMERIC(15,2) NOT NULL,
    data_leilao DATE NOT NULL,
    uf VARCHAR(2) NOT NULL,
    instituicao VARCHAR(300) NOT NULL,
    link_edital VARCHAR(1000),
    link_leilao VARCHAR(1000),
    tipo_leilao VARCHAR(30),
    cidade VARCHAR(100),
    bairro VARCHAR(200),
    area_total NUMERIC(10,2),
    tipo_imovel VARCHAR(50),
    quartos INTEGER,
    banheiros INTEGER,
    vagas INTEGER,
    endereco VARCHAR(500),
    cep VARCHAR(10),
    latitude NUMERIC(10,8),
    longitude NUMERIC(11,8),
    condicao VARCHAR(50),
    aceita_financiamento BOOLEAN DEFAULT FALSE,
    observacoes VARCHAR(2000),
    status VARCHAR(20) DEFAULT 'DISPONIVEL',
    vendedor_id UUID,
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_imovel_uf ON imovel(uf);
CREATE INDEX IF NOT EXISTS idx_imovel_data_leilao ON imovel(data_leilao);
CREATE INDEX IF NOT EXISTS idx_imovel_valor ON imovel(valor_avaliacao);
CREATE INDEX IF NOT EXISTS idx_imovel_status ON imovel(status);
CREATE INDEX IF NOT EXISTS idx_imovel_vendedor ON imovel(vendedor_id);
CREATE INDEX IF NOT EXISTS idx_imovel_ativo ON imovel(ativo);

CREATE TABLE IF NOT EXISTS imagem_imovel (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    imovel_id UUID NOT NULL REFERENCES imovel(id) ON DELETE CASCADE,
    url VARCHAR(1000) NOT NULL,
    legenda VARCHAR(500),
    principal BOOLEAN DEFAULT FALSE,
    ordem INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_imagem_imovel_id ON imagem_imovel(imovel_id);

CREATE TABLE IF NOT EXISTS video_imovel (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    imovel_id UUID NOT NULL REFERENCES imovel(id) ON DELETE CASCADE,
    url VARCHAR(1000) NOT NULL,
    nome_original VARCHAR(500),
    tamanho BIGINT,
    tipo VARCHAR(50),
    ordem INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_video_imovel_id ON video_imovel(imovel_id);

CREATE TABLE IF NOT EXISTS leiloeira (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    url VARCHAR(500) NOT NULL,
    tipo_integracao VARCHAR(20) NOT NULL,
    logo_url TEXT,
    configuracao_json TEXT,
    status VARCHAR(20) DEFAULT 'ATIVA',
    ultima_sincronizacao TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS leilao (
    id BIGSERIAL PRIMARY KEY,
    leiloeira_id BIGINT NOT NULL REFERENCES leiloeira(id),
    titulo VARCHAR(500) NOT NULL,
    descricao TEXT,
    data_inicio TIMESTAMP,
    data_encerramento TIMESTAMP,
    localizacao VARCHAR(200),
    status VARCHAR(20) DEFAULT 'AGENDADO',
    url_edital VARCHAR(1000),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_leilao_leiloeira ON leilao(leiloeira_id);
CREATE INDEX IF NOT EXISTS idx_leilao_status ON leilao(status);

CREATE TABLE IF NOT EXISTS produto (
    id BIGSERIAL PRIMARY KEY,
    leilao_id BIGINT NOT NULL REFERENCES leilao(id),
    leiloeira_id BIGINT NOT NULL REFERENCES leiloeira(id),
    titulo VARCHAR(500) NOT NULL,
    descricao TEXT,
    categoria VARCHAR(100),
    subcategoria VARCHAR(100),
    condicao VARCHAR(20),
    valor_avaliacao NUMERIC(15,2),
    lance_minimo NUMERIC(15,2),
    lance_atual NUMERIC(15,2),
    fotos_urls TEXT,
    especificacoes TEXT,
    localizacao VARCHAR(200),
    data_limite TIMESTAMP,
    status VARCHAR(20) DEFAULT 'DISPONIVEL',
    payload_original TEXT,
    url_original VARCHAR(1000),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_produto_status ON produto(status);
CREATE INDEX IF NOT EXISTS idx_produto_categoria ON produto(categoria);
CREATE INDEX IF NOT EXISTS idx_produto_leilao ON produto(leilao_id);
