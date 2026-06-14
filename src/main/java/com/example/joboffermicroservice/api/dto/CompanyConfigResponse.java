package com.example.joboffermicroservice.api.dto;

import java.util.UUID;

public record CompanyConfigResponse(
    UUID companyId,
    boolean approvalRequired,
    boolean partialSaveEnabled,
    boolean manualPostingRequired
) {}
