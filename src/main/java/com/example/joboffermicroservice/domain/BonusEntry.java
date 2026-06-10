package com.example.joboffermicroservice.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "bonus_entries")
public class BonusEntry {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "job_offer_id")
    private JobOffer jobOffer;

    @Enumerated(EnumType.STRING)
    private BonusType type;

    private BigDecimal amount;
    private String currency;

    protected BonusEntry() {}

    public BonusEntry(JobOffer jobOffer, BonusType type, BigDecimal amount, String currency) {
        this.id = UUID.randomUUID();
        this.jobOffer = jobOffer;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
    }

    public UUID getId() { return id; }
    public BonusType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
}
