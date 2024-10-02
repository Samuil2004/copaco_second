package nl.fontys.s3.copacoproject.business.impl;

import nl.fontys.s3.copacoproject.business.AddressManager;
import nl.fontys.s3.copacoproject.business.dto.addressDto.AddressConverter;
import nl.fontys.s3.copacoproject.business.dto.addressDto.CreateNewAddressRequest;
import nl.fontys.s3.copacoproject.business.dto.addressDto.CreateNewAddressResponse;
import nl.fontys.s3.copacoproject.business.dto.addressDto.UpdateAddressRequest;
import nl.fontys.s3.copacoproject.domain.Address;
import nl.fontys.s3.copacoproject.persistence.AddressRepository;
import nl.fontys.s3.copacoproject.persistence.entity.AddressEntity;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class AddressManagerImpl implements AddressManager {
    private final AddressRepository addressRepository;


    public AddressManagerImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public CreateNewAddressResponse createNewAddress(CreateNewAddressRequest request){
        AddressEntity address = saveNewAddress(request);

        return CreateNewAddressResponse.builder()
                .addressId(address.getId())
                .build();
    }
    private AddressEntity saveNewAddress(CreateNewAddressRequest request){
        AddressEntity newAddress = AddressEntity.builder()
                .city(request.getCity())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .street(request.getStreet())
                .build();
        return addressRepository.save(newAddress);
    }
    @Override
    public void updateAddress(UpdateAddressRequest updateAddressRequest){
        AddressEntity address = addressRepository.findById(updateAddressRequest.getAddressId()).orElseThrow();
        address.setCity(updateAddressRequest.getCity());
        address.setCountry(updateAddressRequest.getCountry());
        address.setPostalCode(updateAddressRequest.getPostalCode());
        address.setStreet(updateAddressRequest.getStreet());
        addressRepository.save(address);
    }
    @Override
    public void deleteAddress( Long id){
        addressRepository.deleteById(id);
    }
    @Override
    public Optional<Address> getAddressById(Long id){
        return addressRepository.findById(id).map(AddressConverter::convert);
    }
}
