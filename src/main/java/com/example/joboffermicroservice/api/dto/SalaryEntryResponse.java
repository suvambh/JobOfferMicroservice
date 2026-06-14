package com.example.joboffermicroservice.api.dto;

import com.example.joboffermicroservice.domain.SalaryType;
import java.math.BigDecimal;

public record SalaryEntryResponse(
    SalaryType type,
    BigDecimal amount,
    String currency
) {}
