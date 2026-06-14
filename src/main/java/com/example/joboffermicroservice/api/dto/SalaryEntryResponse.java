package com.example.joboffermicroservice.api.dto;

import com.example.joboffermicroservice.domain.SalaryType;
import java.math.BigDecimal;
import java.util.UUID;

public record SalaryEntryResponse(
    UUID id,
    SalaryType type,
    BigDecimal amount,
    String currency
) {}
