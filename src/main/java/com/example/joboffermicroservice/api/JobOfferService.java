package com.example.joboffermicroservice.api;

import com.example.joboffermicroservice.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        validateCompensation(request, config);
        JobOffer offer = new JobOffer(request.companyId(), request.title(), request.locationType(), request.address());
        addEntries(offer, request);
        return jobOfferRepository.save(offer);
    }

    public JobOffer update(UUID id, JobOfferRequest request) {
        JobOffer offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job offer not found: " + id));
        if (offer.getStatus() != JobOfferStatus.DRAFT && offer.getStatus() != JobOfferStatus.TO_FINALIZE)
            throw new IllegalStateTransitionException("Cannot update offer in status: " + offer.getStatus());
        CompanyConfig config = getConfig(offer.getCompanyId());
        validateCompensation(request, config);
        offer.setTitle(request.title());
        offer.setLocationType(request.locationType());
        offer.setAddress(request.address());
        offer.getSalaryEntries().clear();
        offer.getBonusEntries().clear();
        addEntries(offer, request);
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

    // --- Helpers ---

    private void validateCompensation(JobOfferRequest request, CompanyConfig config) {
        if (!config.isPartialSaveEnabled()) {
            if ((request.salaryEntries() == null || request.salaryEntries().isEmpty()) &&
                (request.bonusEntries() == null || request.bonusEntries().isEmpty())) {
                throw new IllegalArgumentException("At least one compensation entry is required");
            }
        }
    }

    public CompanyConfig getConfig(UUID companyId) {
        return companyConfigRepository.findById(companyId)
                .orElse(new CompanyConfig(companyId, false, false, false));
    }

    private void addEntries(JobOffer offer, JobOfferRequest request) {
        if (request.salaryEntries() != null)
            request.salaryEntries().forEach(e -> offer.getSalaryEntries().add(new SalaryEntry(offer, e.type(), e.amount(), e.currency())));
        if (request.bonusEntries() != null)
            request.bonusEntries().forEach(e -> offer.getBonusEntries().add(new BonusEntry(offer, e.type(), e.amount(), e.currency())));
    }
}
