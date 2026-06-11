package com.example.joboffermicroservice.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface JobOfferRepository extends JpaRepository<JobOffer, UUID> {
    Page<JobOffer> findByCompanyId(UUID companyId, Pageable pageable);
    Page<JobOffer> findByStatus(JobOfferStatus status, Pageable pageable);
    Page<JobOffer> findByCompanyIdAndStatus(UUID companyId, JobOfferStatus status, Pageable pageable);
}

