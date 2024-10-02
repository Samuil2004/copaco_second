package nl.fontys.s3.copacoproject.persistence.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class AddressEntity {
    private Long id;
    private String country;
    private String city;
    private String street;
    private int number;
    private String postalCode;
}
