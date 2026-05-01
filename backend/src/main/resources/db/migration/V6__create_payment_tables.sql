CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    iyzico_token VARCHAR(255),
    payment_page_url TEXT,
    price NUMERIC(12, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT uk_payments_order_id UNIQUE (order_id)
);

CREATE INDEX idx_payments_iyzico_token ON payments (iyzico_token);
