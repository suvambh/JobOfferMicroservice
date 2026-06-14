package com.example.joboffermicroservice.api;

import com.example.joboffermicroservice.api.dto.BonusEntryResponse;
import com.example.joboffermicroservice.api.dto.JobOfferRequest;
import com.example.joboffermicroservice.api.dto.JobOfferResponse;
import com.example.joboffermicroservice.api.dto.SalaryEntryResponse;
import com.example.joboffermicroservice.application.JobOfferService;
import com.example.joboffermicroservice.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/job-offers")
public class JobOfferController {

    private final JobOfferService jobOfferService;

    public JobOfferController(JobOfferService jobOfferService) {
        this.jobOfferService = jobOfferService;
    }

    @PostMapping
    public ResponseEntity<JobOfferResponse> create(@Valid @RequestBody JobOfferRequest request) {
        JobOffer offer = jobOfferService.create(request);
        return ResponseEntity.status(201).body(toResponse(offer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobOfferResponse> update(@PathVariable UUID id, @Valid @RequestBody JobOfferRequest request) {
        JobOffer offer = jobOfferService.update(id, request);
        return ResponseEntity.ok(toResponse(offer));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobOfferResponse> getById(@PathVariable UUID id) {
        JobOffer offer = jobOfferService.getById(id);
        CompanyConfig config = jobOfferService.getConfig(offer.getCompanyId());
        return ResponseEntity.ok(toResponse(offer, config));
    }

    @GetMapping
    public ResponseEntity<Page<JobOfferResponse>> list(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) JobOfferStatus status,
            Pageable pageable) {
        Page<JobOfferResponse> responses = jobOfferService.list(companyId, status, pageable)
                .map(this::toResponse);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<JobOfferResponse> submit(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(jobOfferService.submit(id)));
    }

    @PostMapping("/{id}/finalize")
    public ResponseEntity<JobOfferResponse> finalize(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(jobOfferService.finalize(id)));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<JobOfferResponse> approve(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(jobOfferService.approve(id)));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<JobOfferResponse> reject(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(jobOfferService.reject(id)));
    }

    @PostMapping("/{id}/post")
    public ResponseEntity<JobOfferResponse> post(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(jobOfferService.post(id)));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<JobOfferResponse> close(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(jobOfferService.close(id)));
    }

    @PostMapping("/{id}/expire")
    public ResponseEntity<JobOfferResponse> expire(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(jobOfferService.expire(id)));
    }

    // --- Helpers ---

    private JobOfferResponse toResponse(JobOffer offer) {
        return buildResponse(offer, List.of());
    }

    private JobOfferResponse toResponse(JobOffer offer, CompanyConfig config) {
        return buildResponse(offer, offer.getAvailableTransitions(config));
    }

    private JobOfferResponse buildResponse(JobOffer offer, List<String> transitions) {
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
                transitions
        );
    }

}
