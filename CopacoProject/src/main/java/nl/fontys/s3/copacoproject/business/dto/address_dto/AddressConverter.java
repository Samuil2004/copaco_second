package nl.fontys.s3.copacoproject.business.dto.address_dto;

import nl.fontys.s3.copacoproject.domain.Address;
import nl.fontys.s3.copacoproject.persistence.entity.AddressEntity;

public final class AddressConverter {
    public AddressConverter() {
    }
    public static  Address convert(AddressEntity addressEntity){
        return Address.builder()
                .addressId(addressEntity.getId())
                .country(addressEntity.getCountry())
                .city(addressEntity.getCity())
                .street(addressEntity.getStreet())
                .postalCode(addressEntity.getPostalCode())
                .build();
    }
}
