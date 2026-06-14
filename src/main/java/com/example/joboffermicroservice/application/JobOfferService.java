package com.example.joboffermicroservice.application;

import com.example.joboffermicroservice.api.dto.JobOfferRequest;
import com.example.joboffermicroservice.api.exception.NotFoundException;
import com.example.joboffermicroservice.domain.exception.IllegalStateTransitionException;
import com.example.joboffermicroservice.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class JobOfferService {

    private final JobOfferRepository jobOfferRepository;
    private final CompanyConfigRepository companyConfigRepository;

    public JobOfferService(JobOfferRepository jobOfferRepository, CompanyConfigRepository companyConfigRepository) {
        this.jobOfferRepository = jobOfferRepository;
        this.companyConfigRepository = companyConfigRepository;
    }

    public JobOffer create(JobOfferRequest request) {
        CompanyConfig config = getConfig(request.companyId());
        validateCompleteOffer(request, config);
        Compensation compensation = buildCompensation(request);
        JobOffer offer = new JobOffer(request.companyId(), request.title(), request.locationType(), request.address(), compensation);
        return jobOfferRepository.save(offer);
    }

    public JobOffer update(UUID id, JobOfferRequest request) {
        JobOffer offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job offer not found: " + id));
        if (offer.getStatus() != JobOfferStatus.DRAFT && offer.getStatus() != JobOfferStatus.TO_FINALIZE)
            throw new IllegalStateTransitionException("Cannot update offer in status: " + offer.getStatus());
        CompanyConfig config = getConfig(offer.getCompanyId());
        validateCompleteOffer(request, config);
        offer.setTitle(request.title());
        offer.setLocationType(request.locationType());
        offer.setAddress(request.address());
        offer.setCompensation(buildCompensation(request));
        return jobOfferRepository.save(offer);
    }

    public JobOffer getById(UUID id) {
        return jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job offer not found: " + id));
    }

    public Page<JobOffer> list(UUID companyId, JobOfferStatus status, Pageable pageable) {
        if (companyId != null && status != null)
            return jobOfferRepository.findByCompanyIdAndStatus(companyId, status, pageable);
        if (companyId != null)
            return jobOfferRepository.findByCompanyId(companyId, pageable);
        if (status != null)
            return jobOfferRepository.findByStatus(status, pageable);
        return jobOfferRepository.findAll(pageable);
    }

    public JobOffer submit(UUID id) {
        JobOffer offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job offer not found: " + id));
        offer.submit(getConfig(offer.getCompanyId()));
        return jobOfferRepository.save(offer);
    }

    public JobOffer finalize(UUID id) {
        JobOffer offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job offer not found: " + id));
        offer.finalize(getConfig(offer.getCompanyId()));
        return jobOfferRepository.save(offer);
    }

    public JobOffer approve(UUID id) {
        JobOffer offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job offer not found: " + id));
        offer.approve(getConfig(offer.getCompanyId()));
        return jobOfferRepository.save(offer);
    }

    public JobOffer reject(UUID id) {
        JobOffer offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job offer not found: " + id));
        offer.reject();
        return jobOfferRepository.save(offer);
    }

    public JobOffer post(UUID id) {
        JobOffer offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job offer not found: " + id));
        offer.post();
        return jobOfferRepository.save(offer);
    }

    public JobOffer close(UUID id) {
        JobOffer offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job offer not found: " + id));
        offer.close();
        return jobOfferRepository.save(offer);
    }

    public JobOffer expire(UUID id) {
        JobOffer offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job offer not found: " + id));
        offer.expire();
        return jobOfferRepository.save(offer);
    }

    // --- Helpers ---

    private Compensation buildCompensation(JobOfferRequest request) {
        List<SalaryEntry> salaries = request.salaryEntries() == null ? List.of() :
                request.salaryEntries().stream()
                        .map(e -> new SalaryEntry(e.type(), e.amount(), e.currency()))
                        .toList();
        List<BonusEntry> bonuses = request.bonusEntries() == null ? List.of() :
                request.bonusEntries().stream()
                        .map(e -> new BonusEntry(e.type(), e.amount(), e.currency()))
                        .toList();
        return new Compensation(salaries, bonuses);
    }

    private void validateCompleteOffer(JobOfferRequest request, CompanyConfig config) {
        if (request.locationType() == LocationType.COMPANY_ADDRESS && request.address() != null)
            throw new IllegalArgumentException("Address should not be provided when locationType is COMPANY_ADDRESS");

        if (!config.isPartialSaveEnabled()) {
            if (request.title() == null || request.title().isBlank())
                throw new IllegalArgumentException("Title is required");
            if (request.locationType() == null)
                throw new IllegalArgumentException("Location type is required");
            if (request.locationType() == LocationType.CUSTOM) {
                if (request.address() == null)
                    throw new IllegalArgumentException("Address is required when locationType is CUSTOM");
                if (request.address().getCity() == null || request.address().getCity().isBlank())
                    throw new IllegalArgumentException("City is required in address");
            }
            if (!buildCompensation(request).hasAtLeastOneEntry())
                throw new IllegalArgumentException("At least one compensation entry is required");
        }
    }

    public CompanyConfig getConfig(UUID companyId) {
        return companyConfigRepository.findById(companyId)
                .orElse(new CompanyConfig(companyId, false, false, false));


    }
}
