package com.example.joboffermicroservice.domain;

import java.math.BigDecimal;

public class BonusEntry {

    private BonusType type;
    private BigDecimal amount;
    private String currency;

    protected BonusEntry() {}

    public BonusEntry(BonusType type, BigDecimal amount, String currency) {
        this.type = type;
        this.amount = amount;
        this.currency = currency;
    }

    public BonusType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
}
