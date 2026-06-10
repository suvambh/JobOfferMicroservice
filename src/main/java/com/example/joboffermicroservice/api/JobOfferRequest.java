package com.example.joboffermicroservice.api;

import com.example.joboffermicroservice.domain.LocationType;
import com.example.joboffermicroservice.domain.Address;
import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record JobOfferRequest(
    @NotNull UUID companyId,
    @NotBlank @Size(max = 200) String title,
    @NotNull LocationType locationType,
    @Valid Address address,
    List<@Valid SalaryEntryRequest> salaryEntries,
    List<@Valid BonusEntryRequest> bonusEntries
) {}


