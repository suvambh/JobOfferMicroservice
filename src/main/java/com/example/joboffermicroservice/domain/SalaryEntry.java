package com.example.joboffermicroservice.domain;

import java.math.BigDecimal;

public class SalaryEntry {

    private SalaryType type;
    private BigDecimal amount;
    private String currency;

    protected SalaryEntry() {}

    public SalaryEntry(SalaryType type, BigDecimal amount, String currency) {
        this.type = type;
        this.amount = amount;
        this.currency = currency;
    }

    public SalaryType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
}
