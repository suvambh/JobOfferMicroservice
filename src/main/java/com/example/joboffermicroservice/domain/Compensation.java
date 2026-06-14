package com.example.joboffermicroservice.domain;

import java.util.List;

public record Compensation(
    List<SalaryEntry> salaryEntries,
    List<BonusEntry> bonusEntries
) {
    public boolean hasAtLeastOneEntry() {
        return (salaryEntries != null && !salaryEntries.isEmpty())
            || (bonusEntries != null && !bonusEntries.isEmpty());
    }
}
