package com.example.joboffermicroservice.api;

import com.example.joboffermicroservice.domain.SalaryType;
import java.math.BigDecimal;

public record SalaryEntryRequest(
    SalaryType type,
    BigDecimal amount,
    String currency
) {}
