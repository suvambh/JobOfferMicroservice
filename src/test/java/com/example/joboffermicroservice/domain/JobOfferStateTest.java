package com.example.joboffermicroservice.domain;

import com.example.joboffermicroservice.domain.exception.IllegalStateTransitionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JobOfferStateTest {

    private UUID companyId;
    private JobOffer completeOffer;
    private JobOffer incompleteOffer;

    @BeforeEach
    void setup() {
        companyId = UUID.randomUUID();
        completeOffer = new JobOffer(companyId, "Senior Dev", LocationType.COMPANY_ADDRESS, null);
        completeOffer.getSalaryEntries().add(new SalaryEntry(completeOffer, SalaryType.MONTHLY, new BigDecimal("5000"), "EUR"));

        incompleteOffer = new JobOffer(companyId, "Junior Dev", LocationType.COMPANY_ADDRESS, null);
    }

    // --- submit() happy paths ---

    @Test
    void submit_allFlagsFalse_publishes() {
        CompanyConfig config = new CompanyConfig(companyId, false, false, false);
        completeOffer.submit(config);
        assertEquals(JobOfferStatus.PUBLISHED, completeOffer.getStatus());
        assertNotNull(completeOffer.getPublishedAt());
    }

    @Test
    void submit_approvalRequired_goesToApprove() {
        CompanyConfig config = new CompanyConfig(companyId, true, false, false);
        completeOffer.submit(config);
        assertEquals(JobOfferStatus.TO_APPROVE, completeOffer.getStatus());
    }

    @Test
    void submit_manualPostingRequired_goesToPost() {
        CompanyConfig config = new CompanyConfig(companyId, false, false, true);
        completeOffer.submit(config);
        assertEquals(JobOfferStatus.TO_POST, completeOffer.getStatus());
    }

    @Test
    void submit_incompleteWithPartialSave_goesToFinalize() {
        CompanyConfig config = new CompanyConfig(companyId, false, true, false);
        incompleteOffer.submit(config);
        assertEquals(JobOfferStatus.TO_FINALIZE, incompleteOffer.getStatus());
    }

    @Test
    void submit_incompleteWithoutPartialSave_throwsException() {
        CompanyConfig config = new CompanyConfig(companyId, false, false, false);
        assertThrows(IllegalArgumentException.class, () -> incompleteOffer.submit(config));
    }

    // --- finalize() ---

    @Test
    void finalize_completeOffer_allFlagsFalse_publishes() {
        CompanyConfig config = new CompanyConfig(companyId, false, true, false);
        incompleteOffer.submit(config); // goes to TO_FINALIZE
        incompleteOffer.getSalaryEntries().add(new SalaryEntry(incompleteOffer, SalaryType.MONTHLY, new BigDecimal("3000"), "EUR"));
        incompleteOffer.finalize(config);
        assertEquals(JobOfferStatus.PUBLISHED, incompleteOffer.getStatus());
    }

    @Test
    void finalize_stillIncomplete_throwsException() {
        CompanyConfig config = new CompanyConfig(companyId, false, true, false);
        incompleteOffer.submit(config); // goes to TO_FINALIZE
        assertThrows(IllegalArgumentException.class, () -> incompleteOffer.finalize(config));
    }

    // --- reject() ---

    @Test
    void reject_fromApprove_goesBackToDraft() {
        CompanyConfig config = new CompanyConfig(companyId, true, false, false);
        completeOffer.submit(config); // TO_APPROVE
        completeOffer.reject();
        assertEquals(JobOfferStatus.DRAFT, completeOffer.getStatus());
    }

    @Test
    void reject_fromDraft_throwsException() {
        assertThrows(IllegalStateTransitionException.class, () -> completeOffer.reject());
    }

    // --- close() / expire() ---

    @Test
    void close_fromPublished_closes() {
        CompanyConfig config = new CompanyConfig(companyId, false, false, false);
        completeOffer.submit(config); // PUBLISHED
        completeOffer.close();
        assertEquals(JobOfferStatus.CLOSED, completeOffer.getStatus());
    }

    @Test
    void expire_fromPublished_closes() {
        CompanyConfig config = new CompanyConfig(companyId, false, false, false);
        completeOffer.submit(config); // PUBLISHED
        completeOffer.expire();
        assertEquals(JobOfferStatus.CLOSED, completeOffer.getStatus());
    }

    // --- publishedAt ---

    @Test
    void publishedAt_isNullBeforePublishing() {
        assertNull(completeOffer.getPublishedAt());
    }

    @Test
    void publishedAt_isSetAfterPublishing() {
        CompanyConfig config = new CompanyConfig(companyId, false, false, false);
        completeOffer.submit(config);
        assertNotNull(completeOffer.getPublishedAt());
    }
}
