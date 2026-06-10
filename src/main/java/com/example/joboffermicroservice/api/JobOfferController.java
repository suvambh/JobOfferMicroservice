package com.example.joboffermicroservice.api;

import com.example.joboffermicroservice.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/job-offers")
public class JobOfferController {

    private final JobOfferRepository jobOfferRepository;
    private final CompanyConfigRepository companyConfigRepository;

    public JobOfferController(JobOfferRepository jobOfferRepository, CompanyConfigRepository companyConfigRepository) {
        this.jobOfferRepository = jobOfferRepository;
        this.companyConfigRepository = companyConfigRepository;
    }

    @PostMapping
    public ResponseEntity<JobOfferResponse> create(@RequestBody JobOfferRequest request) {
        JobOffer offer = new JobOffer(request.companyId(), request.title(), request.locationType(), request.address());
        addEntries(offer, request);
        jobOfferRepository.save(offer);
        CompanyConfig config = getConfig(request.companyId());
        return ResponseEntity.status(201).body(toResponse(offer, config));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobOfferResponse> update(@PathVariable UUID id, @RequestBody JobOfferRequest request) {
        JobOffer offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job offer not found: " + id));
        if (offer.getStatus() != JobOfferStatus.DRAFT && offer.getStatus() != JobOfferStatus.TO_FINALIZE)
            throw new IllegalStateTransitionException("Cannot update offer in status: " + offer.getStatus());
        offer.setTitle(request.title());
        offer.setLocationType(request.locationType());
        offer.setAddress(request.address());
        jobOfferRepository.save(offer);
        CompanyConfig config = getConfig(offer.getCompanyId());
        return ResponseEntity.ok(toResponse(offer, config));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobOfferResponse> getById(@PathVariable UUID id) {
        JobOffer offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job offer not found: " + id));
        CompanyConfig config = getConfig(offer.getCompanyId());
        return ResponseEntity.ok(toResponse(offer, config));
    }

    @GetMapping
    public ResponseEntity<List<JobOfferResponse>> list(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) JobOfferStatus status) {
        List<JobOffer> offers = jobOfferRepository.findAll();
        if (companyId != null) offers = offers.stream().filter(o -> o.getCompanyId().equals(companyId)).toList();
        if (status != null) offers = offers.stream().filter(o -> o.getStatus() == status).toList();
        List<JobOfferResponse> responses = offers.stream()
                .map(o -> toResponse(o, getConfig(o.getCompanyId())))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<JobOfferResponse> submit(@PathVariable UUID id) {
        JobOffer offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job offer not found: " + id));
        CompanyConfig config = getConfig(offer.getCompanyId());
        offer.submit(config);
        jobOfferRepository.save(offer);
        return ResponseEntity.ok(toResponse(offer, config));
    }

    @PostMapping("/{id}/finalize")
    public ResponseEntity<JobOfferResponse> finalize(@PathVariable UUID id) {
        JobOffer offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job offer not found: " + id));
        CompanyConfig config = getConfig(offer.getCompanyId());
        offer.finalize(config);
        jobOfferRepository.save(offer);
        return ResponseEntity.ok(toResponse(offer, config));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<JobOfferResponse> approve(@PathVariable UUID id) {
        JobOffer offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job offer not found: " + id));
        CompanyConfig config = getConfig(offer.getCompanyId());
        offer.approve(config);
        jobOfferRepository.save(offer);
        return ResponseEntity.ok(toResponse(offer, config));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<JobOfferResponse> reject(@PathVariable UUID id) {
        JobOffer offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job offer not found: " + id));
        offer.reject();
        jobOfferRepository.save(offer);
        CompanyConfig config = getConfig(offer.getCompanyId());
        return ResponseEntity.ok(toResponse(offer, config));
    }

    @PostMapping("/{id}/post")
    public ResponseEntity<JobOfferResponse> post(@PathVariable UUID id) {
        JobOffer offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job offer not found: " + id));
        offer.post();
        jobOfferRepository.save(offer);
        CompanyConfig config = getConfig(offer.getCompanyId());
        return ResponseEntity.ok(toResponse(offer, config));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<JobOfferResponse> close(@PathVariable UUID id) {
        JobOffer offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job offer not found: " + id));
        offer.close();
        jobOfferRepository.save(offer);
        CompanyConfig config = getConfig(offer.getCompanyId());
        return ResponseEntity.ok(toResponse(offer, config));
    }

    // --- Helpers ---

    private CompanyConfig getConfig(UUID companyId) {
        return companyConfigRepository.findById(companyId)
                .orElse(new CompanyConfig(companyId, false, false, false));
    }

    private void addEntries(JobOffer offer, JobOfferRequest request) {
        if (request.salaryEntries() != null)
            request.salaryEntries().forEach(e -> offer.getSalaryEntries().add(new SalaryEntry(offer, e.type(), e.amount(), e.currency())));
        if (request.bonusEntries() != null)
            request.bonusEntries().forEach(e -> offer.getBonusEntries().add(new BonusEntry(offer, e.type(), e.amount(), e.currency())));
    }

    private JobOfferResponse toResponse(JobOffer offer, CompanyConfig config) {
        List<SalaryEntryResponse> salaries = offer.getSalaryEntries().stream()
                .map(e -> new SalaryEntryResponse(e.getId(), e.getType(), e.getAmount(), e.getCurrency()))
                .toList();
        List<BonusEntryResponse> bonuses = offer.getBonusEntries().stream()
                .map(e -> new BonusEntryResponse(e.getId(), e.getType(), e.getAmount(), e.getCurrency()))
                .toList();
        return new JobOfferResponse(
                offer.getId(), offer.getCompanyId(), offer.getTitle(), offer.getStatus(),
                offer.getLocationType(), offer.getAddress(), salaries, bonuses,
                offer.getCreatedAt(), offer.getUpdatedAt(), offer.getPublishedAt(),
                offer.getAvailableTransitions()
        );
    }
}
