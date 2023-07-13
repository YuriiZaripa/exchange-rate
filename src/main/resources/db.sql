CREATE DATABASE currency_exchange;

CREATE TABLE exchange_rate (
                               id SERIAL PRIMARY KEY,
                               currency_code VARCHAR(5) NOT NULL,
                               cost NUMERIC(19,8) NOT NULL
);