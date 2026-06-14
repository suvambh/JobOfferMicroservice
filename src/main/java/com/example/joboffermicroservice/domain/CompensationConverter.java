package com.example.joboffermicroservice.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CompensationConverter implements AttributeConverter<Compensation, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Compensation compensation) {
        try {
            return mapper.writeValueAsString(compensation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize compensation", e);
        }
    }

    @Override
    public Compensation convertToEntityAttribute(String json) {
        try {
            return mapper.readValue(json, Compensation.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize compensation", e);
        }
    }
}
