package com.example.joboffermicroservice.api.dto;

public record CompanyConfigRequest(
    boolean approvalRequired,
    boolean partialSaveEnabled,
    boolean manualPostingRequired
) {}
