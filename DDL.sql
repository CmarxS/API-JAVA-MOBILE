
-- ===============================================
-- DDL.sql (versão completa) - MVP Finanças/Hábitos
-- Inclui 'role' no CREATE TABLE TB_USERS
-- ===============================================

-- ==============================
-- Tabela: TB_USERS
-- ==============================
CREATE TABLE TB_USERS (
  id_user     NUMBER(12) GENERATED ALWAYS AS IDENTITY
              CONSTRAINT PK_TB_USERS PRIMARY KEY,
  nome        VARCHAR2(100)      NOT NULL,
  email       VARCHAR2(120)      NOT NULL,
  senha_hash  VARCHAR2(255)      NOT NULL,
  role        VARCHAR2(20)       DEFAULT 'USER' NOT NULL,
  created_at  TIMESTAMP          DEFAULT SYSTIMESTAMP
);

ALTER TABLE TB_USERS
  ADD CONSTRAINT UQ_TB_USERS_EMAIL UNIQUE (email);

ALTER TABLE TB_USERS
  ADD CONSTRAINT CK_TB_USERS_ROLE CHECK (role IN ('USER','ADMIN'));

CREATE INDEX IDX_USERS_CREATED_AT ON TB_USERS(created_at);

-- ==============================
-- Tabela: TB_CATEGORIES
-- ==============================
CREATE TABLE TB_CATEGORIES (
  id_category     NUMBER(12) GENERATED ALWAYS AS IDENTITY
                  CONSTRAINT PK_TB_CATEGORIES PRIMARY KEY,
  nome            VARCHAR2(100) NOT NULL,
  tipo            VARCHAR2(20)  NOT NULL, -- DESPESA | RECEITA
  limite_mensal   NUMBER(10,2),           -- opcional
  created_at      TIMESTAMP DEFAULT SYSTIMESTAMP
);

ALTER TABLE TB_CATEGORIES
  ADD CONSTRAINT UQ_TB_CATEGORIES_NOME UNIQUE (nome);

ALTER TABLE TB_CATEGORIES
  ADD CONSTRAINT CK_TB_CATEGORIES_TIPO CHECK (tipo IN ('DESPESA','RECEITA'));

CREATE INDEX IDX_CATEGORIES_TIPO ON TB_CATEGORIES(tipo);

-- ==============================
-- Tabela: TB_GOALS
-- ==============================
CREATE TABLE TB_GOALS (
  id_goal          NUMBER(12) GENERATED ALWAYS AS IDENTITY
                   CONSTRAINT PK_TB_GOALS PRIMARY KEY,
  id_user          NUMBER(12)   NOT NULL,
  titulo           VARCHAR2(150) NOT NULL,
  tipo             VARCHAR2(12)  NOT NULL,    -- FINANCEIRO | HABITO
  valor_alvo       NUMBER(10,2),              -- se FINANCEIRO
  dias_alvo        NUMBER,                    -- se HABITO
  dias_concluidos  NUMBER DEFAULT 0,
  qtd_alvo_diaria  NUMBER,
  unidade          VARCHAR2(20),
  data_inicio      DATE,
  data_fim         DATE,
  status           VARCHAR2(12) DEFAULT 'ATIVA',
  created_at       TIMESTAMP DEFAULT SYSTIMESTAMP
);

ALTER TABLE TB_GOALS
  ADD CONSTRAINT FK_GOALS_USER FOREIGN KEY (id_user)
  REFERENCES TB_USERS(id_user);

ALTER TABLE TB_GOALS
  ADD CONSTRAINT CK_TB_GOALS_TIPO CHECK (tipo IN ('FINANCEIRO','HABITO'));

ALTER TABLE TB_GOALS
  ADD CONSTRAINT CK_TB_GOALS_STATUS CHECK (status IN ('ATIVA','CONCLUIDA','CANCELADA'));

CREATE INDEX IDX_GOALS_USER ON TB_GOALS(id_user);
CREATE INDEX IDX_GOALS_TIPO ON TB_GOALS(tipo);

-- ==============================
-- Tabela: TB_TRANSACTIONS
-- ==============================
CREATE TABLE TB_TRANSACTIONS (
  id_transaction  NUMBER(12) GENERATED ALWAYS AS IDENTITY
                  CONSTRAINT PK_TB_TRANSACTIONS PRIMARY KEY,
  id_user         NUMBER(12)   NOT NULL,
  id_category     NUMBER(12)   NOT NULL,
  id_goal         NUMBER(12),              -- opcional (aporte para meta financeira)
  tipo            VARCHAR2(12) NOT NULL,   -- DESPESA | RECEITA
  valor           NUMBER(12,2) NOT NULL,
  descricao       VARCHAR2(200),
  merchant        VARCHAR2(100),
  data_transacao  DATE         NOT NULL,
  created_at      TIMESTAMP    DEFAULT SYSTIMESTAMP
);

ALTER TABLE TB_TRANSACTIONS
  ADD CONSTRAINT FK_TRANS_USER FOREIGN KEY (id_user)
  REFERENCES TB_USERS(id_user);

ALTER TABLE TB_TRANSACTIONS
  ADD CONSTRAINT FK_TRANS_CATEGORY FOREIGN KEY (id_category)
  REFERENCES TB_CATEGORIES(id_category);

ALTER TABLE TB_TRANSACTIONS
  ADD CONSTRAINT FK_TRANS_GOAL FOREIGN KEY (id_goal)
  REFERENCES TB_GOALS(id_goal);

ALTER TABLE TB_TRANSACTIONS
  ADD CONSTRAINT CK_TB_TRANS_TIPO CHECK (tipo IN ('DESPESA','RECEITA'));

CREATE INDEX IDX_TRANS_USER_DT ON TB_TRANSACTIONS(id_user, data_transacao);
CREATE INDEX IDX_TRANS_CATEGORY ON TB_TRANSACTIONS(id_category);
CREATE INDEX IDX_TRANS_TIPO ON TB_TRANSACTIONS(tipo);

-- ==============================
-- Tabela de Auditoria (infra de logs)
-- ==============================
CREATE TABLE TB_AUDIT_LOG (
  id_audit      NUMBER(12) GENERATED ALWAYS AS IDENTITY
                CONSTRAINT PK_TB_AUDIT_LOG PRIMARY KEY,
  table_name    VARCHAR2(30)   NOT NULL,
  operation     CHAR(1)        NOT NULL,           -- 'I','U','D'
  pk_val        VARCHAR2(100)  NOT NULL,
  actor_user    VARCHAR2(128)  DEFAULT SYS_CONTEXT('USERENV','SESSION_USER'),
  client_id     VARCHAR2(256)  DEFAULT SYS_CONTEXT('USERENV','CLIENT_IDENTIFIER'),
  host_name     VARCHAR2(256)  DEFAULT SYS_CONTEXT('USERENV','HOST'),
  ip_address    VARCHAR2(64)   DEFAULT SYS_CONTEXT('USERENV','IP_ADDRESS'),
  created_at    TIMESTAMP      DEFAULT SYSTIMESTAMP,
  payload_old   CLOB,
  payload_new   CLOB
);

CREATE INDEX IDX_AUDIT_TBL_DT ON TB_AUDIT_LOG(table_name, created_at);

-- ==============================
-- Views auxiliares
-- ==============================

-- Gastos (DESPESA) por categoria e mês
CREATE OR REPLACE VIEW VW_SPENT_BY_CATEGORY_MONTH AS
SELECT
  c.id_category,
  c.nome AS categoria,
  TO_CHAR(t.data_transacao, 'YYYY-MM') AS ano_mes,
  SUM(CASE WHEN t.tipo = 'DESPESA' THEN t.valor ELSE 0 END) AS total_despesa
FROM TB_TRANSACTIONS t
JOIN TB_CATEGORIES  c ON c.id_category = t.id_category
GROUP BY c.id_category, c.nome, TO_CHAR(t.data_transacao, 'YYYY-MM');

-- Progresso simples das metas (financeiras: soma de receitas vinculadas; hábito: % dias concluídos)
CREATE OR REPLACE VIEW VW_GOALS_PROGRESS AS
SELECT
  g.id_goal,
  g.id_user,
  g.titulo,
  g.tipo,
  CASE 
    WHEN g.tipo = 'FINANCEIRO' THEN
      ROUND( NVL( (SELECT SUM(t.valor) 
                   FROM TB_TRANSACTIONS t 
                   WHERE t.id_goal = g.id_goal AND t.tipo = 'RECEITA'), 0)
           / NULLIF(g.valor_alvo,0) * 100, 2 )
    WHEN g.tipo = 'HABITO' THEN
      ROUND( NVL(g.dias_concluidos,0) / NULLIF(g.dias_alvo,0) * 100, 2 )
  END AS progresso_pct,
  g.status
FROM TB_GOALS g;

--DROP TABLE TB_JSON_ERROR_LOG;
-- Tabela de log simplificada
CREATE TABLE TB_JSON_ERROR_LOG (
    id_error NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    error_code VARCHAR2(50),
    error_message VARCHAR2(500),
    user_id NUMBER,
    error_timestamp TIMESTAMP DEFAULT SYSTIMESTAMP
);