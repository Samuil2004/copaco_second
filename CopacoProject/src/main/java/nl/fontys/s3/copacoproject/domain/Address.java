package nl.fontys.s3.copacoproject.domain;

import lombok.Builder;

@Builder
public class Address {
    private Integer addressId;
    private String country;
    private String city;
    private String street;
    private String postalCode;
}
