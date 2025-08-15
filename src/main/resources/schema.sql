CREATE TABLE IF NOT EXISTS transactions
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    trx_date
    DATE
    NOT
    NULL,
    description
    VARCHAR(255) NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    category VARCHAR(100) NOT NULL
);