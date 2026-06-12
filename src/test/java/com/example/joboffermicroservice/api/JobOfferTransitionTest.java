package com.example.joboffermicroservice.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class JobOfferTransitionTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void submit_allFlagsFalse_publishes() throws Exception {
        // create offer
        MvcResult result = mockMvc.perform(post("/job-offers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "companyId": "550e8400-e29b-41d4-a716-446655440001",
                        "title": "Dev",
                        "locationType": "COMPANY_ADDRESS",
                        "salaryEntries": [{"type": "MONTHLY", "amount": 4000, "currency": "EUR"}],
                        "bonusEntries": []
                    }
                    """))
            .andExpect(status().isCreated())
            .andReturn();

        String id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();

        // submit — no config set so defaults all false
        mockMvc.perform(post("/job-offers/" + id + "/submit"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }

    @Test
    void submit_fromWrongState_returns409() throws Exception {
        // create and publish offer
        MvcResult result = mockMvc.perform(post("/job-offers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "companyId": "550e8400-e29b-41d4-a716-446655440002",
                        "title": "Dev",
                        "locationType": "COMPANY_ADDRESS",
                        "salaryEntries": [{"type": "MONTHLY", "amount": 4000, "currency": "EUR"}],
                        "bonusEntries": []
                    }
                    """))
            .andReturn();

        String id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(post("/job-offers/" + id + "/submit"));

        // submit again from PUBLISHED — should 409
        mockMvc.perform(post("/job-offers/" + id + "/submit"))
            .andExpect(status().isConflict());
    }
}
