-- V002__visualizacao_imovel.sql

CREATE TABLE IF NOT EXISTS visualizacao_imovel (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    imovel_id UUID NOT NULL REFERENCES imovel(id) ON DELETE CASCADE,
    ip_address VARCHAR(45),
    user_id UUID,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_visualizacao_imovel_id ON visualizacao_imovel(imovel_id);
CREATE INDEX IF NOT EXISTS idx_visualizacao_ip_imovel ON visualizacao_imovel(imovel_id, ip_address);
CREATE INDEX IF NOT EXISTS idx_visualizacao_created_at ON visualizacao_imovel(created_at);
