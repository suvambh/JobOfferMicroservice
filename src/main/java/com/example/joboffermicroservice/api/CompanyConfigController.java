package com.example.joboffermicroservice.api;

import com.example.joboffermicroservice.domain.CompanyConfig;
import com.example.joboffermicroservice.domain.CompanyConfigRepository;
import com.example.joboffermicroservice.api.dto.CompanyConfigResponse;
import com.example.joboffermicroservice.api.dto.CompanyConfigRequest;
import com.example.joboffermicroservice.api.exception.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequestMapping("/companies")
public class CompanyConfigController {

    private final CompanyConfigRepository companyConfigRepository;

    public CompanyConfigController(CompanyConfigRepository companyConfigRepository) {
        this.companyConfigRepository = companyConfigRepository;
    }

    @GetMapping("/{companyId}/config")
    public ResponseEntity<CompanyConfigResponse> getConfig(@PathVariable UUID companyId) {
        CompanyConfig config = companyConfigRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Company config not found: " + companyId));
        return ResponseEntity.ok(toResponse(config));
    }

    @PutMapping("/{companyId}/config")
    public ResponseEntity<CompanyConfigResponse> updateConfig(
            @PathVariable UUID companyId,
            @RequestBody CompanyConfigRequest request) {
        CompanyConfig config = new CompanyConfig(
                companyId,
                request.approvalRequired(),
                request.partialSaveEnabled(),
                request.manualPostingRequired()
        );
        companyConfigRepository.save(config);
        return ResponseEntity.ok(toResponse(config));
    }

    private CompanyConfigResponse toResponse(CompanyConfig config) {
        return new CompanyConfigResponse(
                config.getCompanyId(),
                config.isApprovalRequired(),
                config.isPartialSaveEnabled(),
                config.isManualPostingRequired()
        );
    }
}
