-- docker/postgres/init.sql
CREATE TABLE IF NOT EXISTS BENEFICIO (
  ID BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  NOME VARCHAR(100) NOT NULL,
  DESCRICAO VARCHAR(255),
  VALOR DECIMAL(15,2) NOT NULL,
  ATIVO BOOLEAN DEFAULT TRUE,
  VERSION BIGINT DEFAULT 0
);

-- Inserir dados iniciais
INSERT INTO BENEFICIO (NOME, DESCRICAO, VALOR, ATIVO) VALUES
('Vale Alimentação', 'Benefício para alimentação', 1000.00, TRUE),
('Vale Transporte', 'Auxílio transporte', 500.00, TRUE),
('Plano de Saúde', 'Assistência médica', 800.00, TRUE),
('Bonus Performance', 'Bonus por performance', 300.00, TRUE);

-- Confirmar criação
SELECT 'Tabela BENEFICIO criada e dados inseridos com sucesso!' as status;