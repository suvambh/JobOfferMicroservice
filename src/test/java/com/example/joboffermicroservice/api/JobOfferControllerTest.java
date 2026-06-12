package com.example.joboffermicroservice.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class JobOfferControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createOffer_withValidData_returns201() throws Exception {
        mockMvc.perform(post("/job-offers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "companyId": "550e8400-e29b-41d4-a716-446655440000",
                        "title": "Senior Java Developer",
                        "locationType": "COMPANY_ADDRESS",
                        "salaryEntries": [{"type": "MONTHLY", "amount": 5000, "currency": "EUR"}],
                        "bonusEntries": []
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("DRAFT"))
            .andExpect(jsonPath("$.title").value("Senior Java Developer"));
    }

    @Test
    void createOffer_withNoCompensation_returns400() throws Exception {
        mockMvc.perform(post("/job-offers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "companyId": "550e8400-e29b-41d4-a716-446655440000",
                        "title": "Junior Dev",
                        "locationType": "COMPANY_ADDRESS",
                        "salaryEntries": [],
                        "bonusEntries": []
                    }
                    """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createOffer_withMissingTitle_returns400() throws Exception {
        mockMvc.perform(post("/job-offers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "companyId": "550e8400-e29b-41d4-a716-446655440000",
                        "locationType": "COMPANY_ADDRESS",
                        "salaryEntries": [{"type": "MONTHLY", "amount": 5000, "currency": "EUR"}],
                        "bonusEntries": []
                    }
                    """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getOffer_withInvalidId_returns404() throws Exception {
        mockMvc.perform(get("/job-offers/00000000-0000-0000-0000-000000000000"))
            .andExpect(status().isNotFound());
    }
}
