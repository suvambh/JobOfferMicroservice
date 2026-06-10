package com.example.joboffermicroservice.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "salary_entries")
public class SalaryEntry {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "job_offer_id")
    private JobOffer jobOffer;

    @Enumerated(EnumType.STRING)
    private SalaryType type;

    private BigDecimal amount;
    private String currency;

    protected SalaryEntry() {}

    public SalaryEntry(JobOffer jobOffer, SalaryType type, BigDecimal amount, String currency) {
        this.id = UUID.randomUUID();
        this.jobOffer = jobOffer;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
    }

    public UUID getId() { return id; }
    public SalaryType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
}
