package nl.fontys.s3.copacoproject.domain;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private Long addressId;
    private String country;
    private String city;
    private String street;
    private int number;
    private String postalCode;
}
