package com.example.joboffermicroservice.api;

import com.example.joboffermicroservice.domain.JobOfferStatus;
import java.util.UUID;

public record JobOfferStatusResponse(UUID id, JobOfferStatus status) {}
