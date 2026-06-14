package com.example.joboffermicroservice.domain;

import com.example.joboffermicroservice.domain.exception.IllegalStateTransitionException;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "job_offers")
public class JobOffer {

    @Id
    private UUID id;

    private UUID companyId;
    private String title;

    @Enumerated(EnumType.STRING)
    private JobOfferStatus status;

    @Enumerated(EnumType.STRING)
    private LocationType locationType;

    @Embedded
    private Address address;

    @Convert(converter = CompensationConverter.class)
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Compensation compensation;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant publishedAt;

    protected JobOffer() {}

    public JobOffer(UUID companyId, String title, LocationType locationType, Address address, Compensation compensation) {
        this.id = UUID.randomUUID();
        this.companyId = companyId;
        this.title = title;
        this.locationType = locationType;
        this.address = address;
        this.compensation = compensation;
        this.status = JobOfferStatus.DRAFT;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // --- Transitions ---

    public void submit(CompanyConfig config) {
        if (this.status != JobOfferStatus.DRAFT)
            throw new IllegalStateTransitionException("Cannot submit from status: " + this.status);
        if (!isComplete()) {
            if (config.isPartialSaveEnabled()) {
                this.status = JobOfferStatus.TO_FINALIZE;
                this.updatedAt = Instant.now();
                return;
            }
            throw new IllegalArgumentException("Cannot submit an incomplete job offer");
        }
        if (config.isApprovalRequired()) this.status = JobOfferStatus.TO_APPROVE;
        else if (config.isManualPostingRequired()) this.status = JobOfferStatus.TO_POST;
        else { this.status = JobOfferStatus.PUBLISHED; this.publishedAt = Instant.now(); }
        this.updatedAt = Instant.now();
    }

    public void finalize(CompanyConfig config) {
        if (this.status != JobOfferStatus.TO_FINALIZE)
            throw new IllegalStateTransitionException("Cannot finalize from status: " + this.status);
        if (!isComplete())
            throw new IllegalArgumentException("Cannot finalize an incomplete job offer");
        if (config.isApprovalRequired()) this.status = JobOfferStatus.TO_APPROVE;
        else if (config.isManualPostingRequired()) this.status = JobOfferStatus.TO_POST;
        else { this.status = JobOfferStatus.PUBLISHED; this.publishedAt = Instant.now(); }
        this.updatedAt = Instant.now();
    }

    public void approve(CompanyConfig config) {
        if (this.status != JobOfferStatus.TO_APPROVE)
            throw new IllegalStateTransitionException("Cannot approve from status: " + this.status);
        if (config.isManualPostingRequired()) this.status = JobOfferStatus.TO_POST;
        else { this.status = JobOfferStatus.PUBLISHED; this.publishedAt = Instant.now(); }
        this.updatedAt = Instant.now();
    }

    public void reject() {
        if (this.status != JobOfferStatus.TO_APPROVE)
            throw new IllegalStateTransitionException("Cannot reject from status: " + this.status);
        this.status = JobOfferStatus.DRAFT;
        this.updatedAt = Instant.now();
    }

    public void post() {
        if (this.status != JobOfferStatus.TO_POST)
            throw new IllegalStateTransitionException("Cannot post from status: " + this.status);
        this.status = JobOfferStatus.PUBLISHED;
        this.publishedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void close() {
        if (this.status != JobOfferStatus.PUBLISHED)
            throw new IllegalStateTransitionException("Cannot close from status: " + this.status);
        this.status = JobOfferStatus.CLOSED;
        this.updatedAt = Instant.now();
    }

    public void expire() {
        if (this.status != JobOfferStatus.PUBLISHED)
            throw new IllegalStateTransitionException("Cannot expire from status: " + this.status);
        this.status = JobOfferStatus.CLOSED;
        this.updatedAt = Instant.now();
    }

    // --- Private helpers ---

    private boolean isComplete() {
        return title != null && !title.isBlank()
            && locationType != null
            && compensation != null && compensation.hasAtLeastOneEntry();
    }

    // --- Computed ---

    public List<String> getAvailableTransitions() {
        return switch (this.status) {
            case DRAFT -> List.of("submit");
            case TO_FINALIZE -> List.of("finalize");
            case TO_APPROVE -> List.of("approve", "reject");
            case TO_POST -> List.of("post");
            case PUBLISHED -> List.of("close", "expire");
            case CLOSED -> List.of();
        };
    }

    // --- Getters ---

    public UUID getId() { return id; }
    public UUID getCompanyId() { return companyId; }
    public String getTitle() { return title; }
    public JobOfferStatus getStatus() { return status; }
    public LocationType getLocationType() { return locationType; }
    public Address getAddress() { return address; }
    public Compensation getCompensation() { return compensation; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getPublishedAt() { return publishedAt; }

    public void setTitle(String title) { this.title = title; this.updatedAt = Instant.now(); }
    public void setLocationType(LocationType locationType) { this.locationType = locationType; this.updatedAt = Instant.now(); }
    public void setAddress(Address address) { this.address = address; this.updatedAt = Instant.now(); }
    public void setCompensation(Compensation compensation) { this.compensation = compensation; this.updatedAt = Instant.now(); }
}
