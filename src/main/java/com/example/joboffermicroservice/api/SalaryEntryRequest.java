package com.example.joboffermicroservice.api;

import com.example.joboffermicroservice.domain.SalaryType;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;

public record SalaryEntryRequest(
    @NotNull SalaryType type,
    @NotNull @DecimalMin("0.0") BigDecimal amount,
    @NotBlank @Size(min = 3, max = 3) String currency
) {}


