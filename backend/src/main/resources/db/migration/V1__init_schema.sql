CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE budgets
(
    id              VARCHAR(255)                   NOT NULL,
    created_at      TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    user_id         VARCHAR(255)                   NOT NULL,
    category_id     VARCHAR(255)                   NOT NULL,
    month           VARCHAR(7)                     NOT NULL,
    limit_amount    DECIMAL(12, 2)                 NOT NULL,
    alert_threshold INTEGER                        NOT NULL,
    CONSTRAINT pk_budgets PRIMARY KEY (id)
);

CREATE TABLE categories
(
    id            VARCHAR(255)                   NOT NULL,
    created_at    TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    name          VARCHAR(50)                    NOT NULL,
    category_type VARCHAR(20),
    user_id       VARCHAR(255),
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE chat_messages
(
    id         VARCHAR(255)                   NOT NULL,
    role       VARCHAR(20)                    NOT NULL,
    content    TEXT                           NOT NULL,
    session_id VARCHAR(255)                   NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_chat_messages PRIMARY KEY (id)
);

CREATE TABLE chat_sessions
(
    id         VARCHAR(255)                   NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    title      VARCHAR(255)                   NOT NULL,
    user_id    VARCHAR(255)                   NOT NULL,
    CONSTRAINT pk_chat_sessions PRIMARY KEY (id)
);

CREATE TABLE device_tokens
(
    id           VARCHAR(255)                   NOT NULL,
    created_at   TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    token        VARCHAR(512)                   NOT NULL,
    platform     VARCHAR(10)                    NOT NULL,
    last_seen_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    user_id      VARCHAR(255)                   NOT NULL,
    CONSTRAINT pk_device_tokens PRIMARY KEY (id)
);

CREATE TABLE notifications
(
    id         VARCHAR(255)                   NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    title      VARCHAR(255)                   NOT NULL,
    message    TEXT                           NOT NULL,
    type       VARCHAR(50)                    NOT NULL,
    is_read    BOOLEAN                        NOT NULL,
    read_at    TIMESTAMP(6) WITHOUT TIME ZONE,
    data       JSONB,
    user_id    VARCHAR(255)                   NOT NULL,
    CONSTRAINT pk_notifications PRIMARY KEY (id)
);

CREATE TABLE payment_modes
(
    id         VARCHAR(255)                   NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    name       VARCHAR(100)                   NOT NULL,
    user_id    VARCHAR(255),
    CONSTRAINT pk_payment_modes PRIMARY KEY (id)
);

CREATE TABLE recurring_transactions
(
    id                  VARCHAR(255)                   NOT NULL,
    created_at          TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at          TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    title               VARCHAR(100)                   NOT NULL,
    amount              DECIMAL(12, 2)                 NOT NULL,
    type                VARCHAR(10)                    NOT NULL,
    frequency           VARCHAR(10)                    NOT NULL,
    start_date          date                           NOT NULL,
    end_date            date,
    next_execution_date date                           NOT NULL,
    is_active           BOOLEAN                        NOT NULL,
    note                VARCHAR(255),
    user_id             VARCHAR(255)                   NOT NULL,
    category_id         VARCHAR(255),
    payment_mode_id     VARCHAR(255),
    CONSTRAINT pk_recurring_transactions PRIMARY KEY (id)
);

CREATE TABLE refresh_token
(
    id         VARCHAR(255)                   NOT NULL,
    token      TEXT,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    expires_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    revoked    BOOLEAN                        NOT NULL,
    revoked_at TIMESTAMP(6) WITHOUT TIME ZONE,
    user_id    VARCHAR(255)                   NOT NULL,
    CONSTRAINT pk_refresh_token PRIMARY KEY (id)
);

CREATE TABLE revchanges
(
    rev        BIGINT NOT NULL,
    entityname VARCHAR(255)
);

CREATE TABLE revinfo
(
    rev      BIGINT NOT NULL,
    revtstmp BIGINT,
    CONSTRAINT pk_revinfo PRIMARY KEY (rev)
);

CREATE TABLE savings_goals
(
    id            VARCHAR(255)                   NOT NULL,
    created_at    TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    title         VARCHAR(100)                   NOT NULL,
    target_amount DECIMAL(12, 2)                 NOT NULL,
    saved_amount  DECIMAL(12, 2)                 NOT NULL,
    deadline      date                           NOT NULL,
    status        VARCHAR(20)                    NOT NULL,
    note          TEXT,
    user_id       VARCHAR(255)                   NOT NULL,
    CONSTRAINT pk_savings_goals PRIMARY KEY (id)
);

CREATE TABLE semantic_query_cache
(
    id              VARCHAR(255)                   NOT NULL,
    created_at      TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    user_id         VARCHAR(255)                   NOT NULL,
    query_text      TEXT                           NOT NULL,
    cached_answer   TEXT                           NOT NULL,
    similarity_hits INTEGER                        NOT NULL,
    ttl_expires_at  TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_semantic_query_cache PRIMARY KEY (id)
);

CREATE TABLE transactions
(
    id              VARCHAR(255)                   NOT NULL,
    created_at      TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    amount          DECIMAL(12, 2)                 NOT NULL,
    type            VARCHAR(10)                    NOT NULL,
    date            date                           NOT NULL,
    note            TEXT,
    embedding_id    VARCHAR(255),
    user_id         VARCHAR(255)                   NOT NULL,
    category_id     VARCHAR(255),
    payment_mode_id VARCHAR(255),
    CONSTRAINT pk_transactions PRIMARY KEY (id)
);

CREATE TABLE users
(
    id                    VARCHAR(255)                   NOT NULL,
    created_at            TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at            TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    full_name             VARCHAR(50)                    NOT NULL,
    email                 VARCHAR(100)                   NOT NULL,
    password_hash         VARCHAR(255)                   NOT NULL,
    role                  VARCHAR(255)                   NOT NULL,
    account_status        VARCHAR(255)                   NOT NULL,
    deletion_requested_at TIMESTAMP(6) WITHOUT TIME ZONE,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE refresh_token
    ADD CONSTRAINT uc_refresh_token_token UNIQUE (token);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE budgets
    ADD CONSTRAINT uq_budget_user_category_month UNIQUE (user_id, category_id, month);

ALTER TABLE categories
    ADD CONSTRAINT uq_category_name_user UNIQUE (name, user_id);

ALTER TABLE device_tokens
    ADD CONSTRAINT uq_device_tokens_token UNIQUE (token);

ALTER TABLE payment_modes
    ADD CONSTRAINT uq_payment_mode_name_user UNIQUE (name, user_id);

CREATE INDEX idx_budget_user_month ON budgets (user_id, month);

CREATE INDEX idx_chat_messages_session_created ON chat_messages (session_id, created_at);

CREATE INDEX idx_notifications_user_created ON notifications (user_id, created_at);

CREATE INDEX idx_notifications_user_read ON notifications (user_id, is_read);

CREATE INDEX idx_recurring_scheduler ON recurring_transactions (is_active, next_execution_date);

CREATE INDEX idx_recurring_user_active ON recurring_transactions (user_id, is_active);

CREATE INDEX idx_transactions_user_date ON transactions (user_id, date);

CREATE INDEX idx_users_email ON users (email);

ALTER TABLE budgets
    ADD CONSTRAINT FK_BUDGETS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE budgets
    ADD CONSTRAINT FK_BUDGETS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE categories
    ADD CONSTRAINT FK_CATEGORIES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_categories_user_id ON categories (user_id);

ALTER TABLE chat_messages
    ADD CONSTRAINT FK_CHAT_MESSAGES_ON_SESSION FOREIGN KEY (session_id) REFERENCES chat_sessions (id);

ALTER TABLE chat_sessions
    ADD CONSTRAINT FK_CHAT_SESSIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_chat_sessions_user_id ON chat_sessions (user_id);

ALTER TABLE device_tokens
    ADD CONSTRAINT FK_DEVICE_TOKENS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_device_tokens_user_id ON device_tokens (user_id);

ALTER TABLE notifications
    ADD CONSTRAINT FK_NOTIFICATIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE payment_modes
    ADD CONSTRAINT FK_PAYMENT_MODES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_payment_modes_user_id ON payment_modes (user_id);

ALTER TABLE recurring_transactions
    ADD CONSTRAINT FK_RECURRING_TRANSACTIONS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE recurring_transactions
    ADD CONSTRAINT FK_RECURRING_TRANSACTIONS_ON_PAYMENT_MODE FOREIGN KEY (payment_mode_id) REFERENCES payment_modes (id);

ALTER TABLE recurring_transactions
    ADD CONSTRAINT FK_RECURRING_TRANSACTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE refresh_token
    ADD CONSTRAINT FK_REFRESH_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_refresh_token_user ON refresh_token (user_id);

ALTER TABLE savings_goals
    ADD CONSTRAINT FK_SAVINGS_GOALS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_goal_user_id ON savings_goals (user_id);

ALTER TABLE semantic_query_cache
    ADD CONSTRAINT FK_SEMANTIC_QUERY_CACHE_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);

CREATE INDEX idx_transactions_category_id ON transactions (category_id);

ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_PAYMENT_MODE FOREIGN KEY (payment_mode_id) REFERENCES payment_modes (id);

CREATE INDEX idx_transactions_payment_mode_id ON transactions (payment_mode_id);

ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_transactions_user_id ON transactions (user_id);

ALTER TABLE revchanges
    ADD CONSTRAINT fk_revchanges_on_default_tracking_modified_entities_changelog FOREIGN KEY (rev) REFERENCES revinfo (rev);