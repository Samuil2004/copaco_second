package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.addressDto.CreateNewAddressRequest;
import nl.fontys.s3.copacoproject.business.dto.addressDto.CreateNewAddressResponse;
import nl.fontys.s3.copacoproject.business.dto.addressDto.UpdateAddressRequest;
import nl.fontys.s3.copacoproject.domain.Address;

import java.util.Optional;


public interface AddressManager {
    CreateNewAddressResponse createNewAddress(CreateNewAddressRequest request);
    void updateAddress(UpdateAddressRequest updateAddressRequest);
    void deleteAddress( Long id);
    Optional<Address> getAddressById(Long id);
}
