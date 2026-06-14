package com.example.joboffermicroservice.api.dto;

import com.example.joboffermicroservice.domain.BonusType;
import java.math.BigDecimal;
import java.util.UUID;

public record BonusEntryResponse(
    UUID id,
    BonusType type,
    BigDecimal amount,
    String currency
) {}
