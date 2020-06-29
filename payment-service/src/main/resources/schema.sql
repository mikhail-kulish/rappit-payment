CREATE TABLE IF NOT EXISTS payment
(
    id                       BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    created                  TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    completed                TIMESTAMP             NULL,
    customer_id              VARCHAR(36)           NOT NULL,
    payer_email              VARCHAR(70)           NOT NULL,
    payer_name_first         VARCHAR(70)                    DEFAULT NULL,
    payer_name_last          VARCHAR(70)                    DEFAULT NULL,
    payer_address            VARCHAR(170)                   DEFAULT NULL,
    payer_address_additional VARCHAR(128)                   DEFAULT NULL,
    payer_address_city       VARCHAR(70)                    DEFAULT NULL,
    payer_address_state      VARCHAR(70)                    DEFAULT NULL,
    payer_address_postal     VARCHAR(15)                    DEFAULT NULL,
    payer_address_country    VARCHAR(2)                     DEFAULT NULL,
    order_id                 BIGINT                NOT NULL,
    status                   VARCHAR(15)           NOT NULL
);
CREATE INDEX pmnt_customer_id ON payment (customer_id);
CREATE INDEX pmnt_order_id ON payment (order_id);

CREATE TABLE IF NOT EXISTS `order`
(
    id                  BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    merchant_id         BIGINT                NOT NULL,
    created             TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    amount              DECIMAL(10, 2)        NOT NULL,
    currency            VARCHAR(3)            NOT NULL,
    reference           VARCHAR(32)           NOT NULL,
    description         VARCHAR(255)                   DEFAULT NULL,
    type                VARCHAR(15)           NOT NULL DEFAULT 'SINGLE',
    recurring_frequency VARCHAR(15)                    DEFAULT NULL,
    recurring_amount    DECIMAL(10, 2)                 DEFAULT NULL,
    recurring_currency  VARCHAR(3)                     DEFAULT NULL,
    recurring_count     INT                            DEFAULT NULL,
    recurring_start     DATE                           DEFAULT NULL
);
CREATE INDEX order_merchant_id ON `order` (merchant_id);
CREATE INDEX order_id_merchant_id ON `order` (id, merchant_id);
CREATE INDEX order_merchant_id_reference ON `order` (merchant_id, reference);

CREATE TABLE IF NOT EXISTS order_item
(
    order_id         BIGINT         NOT NULL,
    item_price       DECIMAL(10, 2) NOT NULL,
    item_currency    VARCHAR(3)     NOT NULL,
    item_quantity    INT            NOT NULL,
    item_sku         VARCHAR(52),
    item_description VARCHAR(255),
    item_tax         DECIMAL(10, 2) DEFAULT 0
);
CREATE INDEX order_item_order_id ON order_item (order_id);

CREATE TABLE IF NOT EXISTS order_payer
(
    order_id                 BIGINT      NOT NULL PRIMARY KEY,
    payer_email              VARCHAR(70) NOT NULL,
    payer_name_first         VARCHAR(70)  DEFAULT NULL,
    payer_name_last          VARCHAR(70)  DEFAULT NULL,
    payer_address            VARCHAR(170) DEFAULT NULL,
    payer_address_additional VARCHAR(128) DEFAULT NULL,
    payer_address_city       VARCHAR(70)  DEFAULT NULL,
    payer_address_state      VARCHAR(70)  DEFAULT NULL,
    payer_address_postal     VARCHAR(15)  DEFAULT NULL,
    payer_address_country    VARCHAR(2)   DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS payment_transaction
(
    id           VARCHAR(16) NOT NULL PRIMARY KEY,
    created      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    payment_id   BIGINT      NOT NULL,
    account_info VARCHAR(50) NOT NULL,
    account_id   VARCHAR(16)          DEFAULT NULL,
    type         VARCHAR(20) NOT NULL,
);
CREATE INDEX payment_transaction_payment_id ON payment_transaction (payment_id);

CREATE TABLE IF NOT EXISTS receipt
(
    transaction_id           VARCHAR(16)    NOT NULL,
    created                  TIMESTAMP(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    status                   VARCHAR(20)    NOT NULL,
    amount                   DECIMAL(10, 2) NOT NULL,
    currency                 VARCHAR(3)     NOT NULL,
    processor_transaction_id VARCHAR(32)    NOT NULL,
    message                  VARCHAR(128)            DEFAULT NULL,
    code                     VARCHAR(64)             DEFAULT NULL,
    auth_code                VARCHAR(16)             DEFAULT NULL
);
CREATE INDEX receipt_payment_transaction_id ON receipt (transaction_id);

