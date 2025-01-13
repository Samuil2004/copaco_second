package nl.fontys.s3.copacoproject.business.dto.address_dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CreateNewAddressResponse {
    private Long addressId;

}
