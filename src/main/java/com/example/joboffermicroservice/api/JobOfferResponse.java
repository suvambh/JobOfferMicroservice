package com.example.joboffermicroservice.api;

import com.example.joboffermicroservice.domain.JobOfferStatus;
import com.example.joboffermicroservice.domain.LocationType;
import com.example.joboffermicroservice.domain.Address;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record JobOfferResponse(
    UUID id,
    UUID companyId,
    String title,
    JobOfferStatus status,
    LocationType locationType,
    Address address,
    List<SalaryEntryResponse> salaryEntries,
    List<BonusEntryResponse> bonusEntries,
    Instant createdAt,
    Instant updatedAt,
    Instant publishedAt,
    List<String> availableTransitions
) {}
