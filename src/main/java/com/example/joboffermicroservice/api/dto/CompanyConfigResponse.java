package com.example.joboffermicroservice.api;

import java.util.UUID;

public record CompanyConfigResponse(
    UUID companyId,
    boolean approvalRequired,
    boolean partialSaveEnabled,
    boolean manualPostingRequired
) {}
