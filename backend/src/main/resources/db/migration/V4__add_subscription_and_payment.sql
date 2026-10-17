-- Add free chat usage count to users
ALTER TABLE users ADD COLUMN free_chat_count INTEGER NOT NULL DEFAULT 0;

-- Subscriptions table
CREATE TABLE subscriptions
(
    id         VARCHAR(255)                   NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    user_id    VARCHAR(255)                   NOT NULL,
    plan       VARCHAR(50)                    NOT NULL,
    status     VARCHAR(50)                    NOT NULL,
    start_date TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    end_date   TIMESTAMP(6) WITHOUT TIME ZONE,
    CONSTRAINT pk_subscriptions PRIMARY KEY (id),
    CONSTRAINT fk_subscriptions_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_subscriptions_user_id ON subscriptions (user_id);
CREATE INDEX idx_subscriptions_user_status ON subscriptions (user_id, status);

-- Payment transactions table
CREATE TABLE payment_transactions
(
    id                  VARCHAR(255)                   NOT NULL,
    created_at          TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at          TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    user_id             VARCHAR(255)                   NOT NULL,
    plan                VARCHAR(50)                    NOT NULL,
    provider            VARCHAR(50)                    NOT NULL,
    provider_order_id   VARCHAR(255)                   NOT NULL,
    provider_payment_id VARCHAR(255),
    amount_in_paise     BIGINT                         NOT NULL,
    currency            VARCHAR(10)                    NOT NULL,
    status              VARCHAR(50)                    NOT NULL,
    CONSTRAINT pk_payment_transactions PRIMARY KEY (id),
    CONSTRAINT fk_payment_transactions_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE UNIQUE INDEX idx_payment_transactions_order_id ON payment_transactions (provider_order_id);
CREATE INDEX idx_payment_transactions_user_id ON payment_transactions (user_id);
