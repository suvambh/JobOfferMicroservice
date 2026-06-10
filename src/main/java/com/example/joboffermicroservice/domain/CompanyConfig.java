package com.example.joboffermicroservice.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "company_config")
public class CompanyConfig {

    @Id
    private UUID companyId;

    private boolean approvalRequired = false;
    private boolean partialSaveEnabled = false;
    private boolean manualPostingRequired = false;

    protected CompanyConfig() {}

    public CompanyConfig(UUID companyId, boolean approvalRequired, boolean partialSaveEnabled, boolean manualPostingRequired) {
        this.companyId = companyId;
        this.approvalRequired = approvalRequired;
        this.partialSaveEnabled = partialSaveEnabled;
        this.manualPostingRequired = manualPostingRequired;
    }

    public UUID getCompanyId() { return companyId; }
    public boolean isApprovalRequired() { return approvalRequired; }
    public boolean isPartialSaveEnabled() { return partialSaveEnabled; }
    public boolean isManualPostingRequired() { return manualPostingRequired; }

    public void setApprovalRequired(boolean approvalRequired) { this.approvalRequired = approvalRequired; }
    public void setPartialSaveEnabled(boolean partialSaveEnabled) { this.partialSaveEnabled = partialSaveEnabled; }
    public void setManualPostingRequired(boolean manualPostingRequired) { this.manualPostingRequired = manualPostingRequired; }
}
