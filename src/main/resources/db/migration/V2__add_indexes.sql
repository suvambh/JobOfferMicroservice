CREATE INDEX idx_job_offers_company_id ON job_offers(company_id);
CREATE INDEX idx_job_offers_status ON job_offers(status);
CREATE INDEX idx_job_offers_company_status ON job_offers(company_id, status);
