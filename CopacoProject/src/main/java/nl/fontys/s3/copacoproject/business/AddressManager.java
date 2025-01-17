package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.address_dto.CreateNewAddressRequest;
import nl.fontys.s3.copacoproject.business.dto.address_dto.CreateNewAddressResponse;
import nl.fontys.s3.copacoproject.business.dto.address_dto.UpdateAddressRequest;
import nl.fontys.s3.copacoproject.domain.Address;

import java.util.Optional;


public interface AddressManager {
    CreateNewAddressResponse createNewAddress(CreateNewAddressRequest request);
    void updateAddress(UpdateAddressRequest updateAddressRequest);
    void deleteAddress( Long id);
    Optional<Address> getAddressById(Long id);
}
