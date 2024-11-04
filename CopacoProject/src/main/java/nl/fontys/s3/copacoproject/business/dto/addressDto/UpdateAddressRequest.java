package nl.fontys.s3.copacoproject.business.dto.addressDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UpdateAddressRequest {
    private Long addressId;
    private String street;
    private String houseNumber;
    private String postalCode;
    private String city;
    private String country;
}
