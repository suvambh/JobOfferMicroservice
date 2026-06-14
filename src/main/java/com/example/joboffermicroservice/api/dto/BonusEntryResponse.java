package com.example.joboffermicroservice.api.dto;

import com.example.joboffermicroservice.domain.BonusType;
import java.math.BigDecimal;

public record BonusEntryResponse(
    BonusType type,
    BigDecimal amount,
    String currency
) {}
