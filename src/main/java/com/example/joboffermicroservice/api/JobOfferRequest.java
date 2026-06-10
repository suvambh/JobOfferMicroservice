package com.example.joboffermicroservice.api;

import com.example.joboffermicroservice.domain.LocationType;
import com.example.joboffermicroservice.domain.Address;
import java.util.List;
import java.util.UUID;

public record JobOfferRequest(
    UUID companyId,
    String title,
    LocationType locationType,
    Address address,
    List<SalaryEntryRequest> salaryEntries,
    List<BonusEntryRequest> bonusEntries
) {}
