package nl.fontys.s3.copacoproject.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Address {
    private Long addressId;
    private String country;
    private String city;
    private String street;
    private String postalCode;
}
