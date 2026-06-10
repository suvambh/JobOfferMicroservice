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
    address_line1 VARCHAR(100),
    address_line2 VARCHAR(100),
    address_line3 VARCHAR(100),
    address_postal_code VARCHAR(20),
    address_city VARCHAR(100),
    address_region VARCHAR(100),
    address_country_code CHAR(2),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    published_at TIMESTAMP
);

CREATE TABLE salary_entries (
    id UUID PRIMARY KEY,
    job_offer_id UUID NOT NULL REFERENCES job_offers(id),
    type VARCHAR(50) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    currency CHAR(3) NOT NULL
);

CREATE TABLE bonus_entries (
    id UUID PRIMARY KEY,
    job_offer_id UUID NOT NULL REFERENCES job_offers(id),
    type VARCHAR(50) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    currency CHAR(3) NOT NULL
);
