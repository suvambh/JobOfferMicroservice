package com.example.joboffermicroservice.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address {
    private String line1;
    private String line2;
    private String line3;
    private String postalCode;
    private String city;
    private String region;
    private String countryCode;

    public Address() {}

    public Address(String line1, String line2, String line3, String postalCode, String city, String region, String countryCode) {
        this.line1 = line1;
        this.line2 = line2;
        this.line3 = line3;
        this.postalCode = postalCode;
        this.city = city;
        this.region = region;
        this.countryCode = countryCode;
    }

    public String getLine1() { return line1; }
    public String getLine2() { return line2; }
    public String getLine3() { return line3; }
    public String getPostalCode() { return postalCode; }
    public String getCity() { return city; }
    public String getRegion() { return region; }
    public String getCountryCode() { return countryCode; }
}
