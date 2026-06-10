CREATE TABLE company_config (
    company_id UUID PRIMARY KEY,
    approval_required BOOLEAN NOT NULL DEFAULT FALSE,
    partial_save_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    manual_posting_required BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE job_offers (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL,
    title VARCHAR(200) NOT NULL,
    status VARCHAR(50) NOT NULL,
    location_type VARCHAR(50),
    line1 VARCHAR(100),
    line2 VARCHAR(100),
    line3 VARCHAR(100),
    postal_code VARCHAR(20),
    city VARCHAR(100),
    region VARCHAR(100),
    country_code VARCHAR(2),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    published_at TIMESTAMP
);

CREATE TABLE salary_entries (
    id UUID PRIMARY KEY,
    job_offer_id UUID NOT NULL REFERENCES job_offers(id),
    type VARCHAR(50) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL
);

CREATE TABLE bonus_entries (
    id UUID PRIMARY KEY,
    job_offer_id UUID NOT NULL REFERENCES job_offers(id),
    type VARCHAR(50) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL
);
