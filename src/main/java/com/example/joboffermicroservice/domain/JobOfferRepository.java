package com.example.joboffermicroservice.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JobOfferRepository extends JpaRepository<JobOffer, UUID> {
}
