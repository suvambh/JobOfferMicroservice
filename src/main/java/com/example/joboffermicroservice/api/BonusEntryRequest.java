package com.example.joboffermicroservice.api;

import com.example.joboffermicroservice.domain.BonusType;
import java.math.BigDecimal;

public record BonusEntryRequest(
    BonusType type,
    BigDecimal amount,
    String currency
) {}
