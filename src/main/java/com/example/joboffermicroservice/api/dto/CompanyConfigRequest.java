package com.example.joboffermicroservice.api;

public record CompanyConfigRequest(
    boolean approvalRequired,
    boolean partialSaveEnabled,
    boolean manualPostingRequired
) {}
