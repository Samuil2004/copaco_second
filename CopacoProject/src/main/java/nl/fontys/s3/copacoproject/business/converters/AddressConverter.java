package nl.fontys.s3.copacoproject.business.converters;

import nl.fontys.s3.copacoproject.domain.Address;
import nl.fontys.s3.copacoproject.persistence.entity.AddressEntity;

public final class AddressConverter {
    public static AddressEntity convertFromBaseToEntity(Address address) {
        return AddressEntity.builder()
                .id(address.getAddressId())
                .country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .number(address.getNumber())
                .postalCode(address.getPostalCode()).build();
    }

    public static Address convertFromEntityToBase(AddressEntity addressEntity) {
        return Address.builder()
                .addressId(addressEntity.getId())
                .country(addressEntity.getCountry())
                .city(addressEntity.getCity())
                .street(addressEntity.getStreet())
                .number(addressEntity.getNumber())
                .postalCode(addressEntity.getPostalCode())
                .build();
    }
}
