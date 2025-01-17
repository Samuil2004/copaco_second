package nl.fontys.s3.copacoproject.business.impl;

import nl.fontys.s3.copacoproject.business.dto.address_dto.AddressConverter;
import nl.fontys.s3.copacoproject.business.dto.address_dto.CreateNewAddressRequest;
import nl.fontys.s3.copacoproject.business.dto.address_dto.CreateNewAddressResponse;
import nl.fontys.s3.copacoproject.business.dto.address_dto.UpdateAddressRequest;
import nl.fontys.s3.copacoproject.domain.Address;
import nl.fontys.s3.copacoproject.persistence.AddressRepository;
import nl.fontys.s3.copacoproject.persistence.entity.AddressEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressManagerImplTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressManagerImpl addressManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createNewAddress_ShouldReturnResponse_WhenRequestIsValid() {
        CreateNewAddressRequest request = CreateNewAddressRequest.builder()
                .city("Eindhoven")
                .country("Netherlands")
                .postalCode("1234 AB")
                .street("Fontys Street")
                .build();

        AddressEntity savedEntity = AddressEntity.builder()
                .id(1L)
                .city("Eindhoven")
                .country("Netherlands")
                .postalCode("1234 AB")
                .street("Fontys Street")
                .build();

        when(addressRepository.save(any(AddressEntity.class))).thenReturn(savedEntity);

        CreateNewAddressResponse response = addressManager.createNewAddress(request);

        assertNotNull(response);
        assertEquals(1L, response.getAddressId());
        verify(addressRepository, times(1)).save(any(AddressEntity.class));
    }

    @Test
    void updateAddress_ShouldUpdateAddress_WhenIdIsValid() {
        UpdateAddressRequest request = UpdateAddressRequest.builder()
                .addressId(1L)
                .city("Updated City")
                .country("Updated Country")
                .postalCode("5678 CD")
                .street("Updated Street")
                .build();

        AddressEntity existingEntity = AddressEntity.builder()
                .id(1L)
                .city("Eindhoven")
                .country("Netherlands")
                .postalCode("1234 AB")
                .street("Fontys Street")
                .build();

        when(addressRepository.findById(1L)).thenReturn(Optional.of(existingEntity));
        when(addressRepository.save(any(AddressEntity.class))).thenReturn(existingEntity);

        addressManager.updateAddress(request);

        verify(addressRepository, times(1)).findById(1L);
        verify(addressRepository, times(1)).save(existingEntity);

        assertEquals("Updated City", existingEntity.getCity());
        assertEquals("Updated Country", existingEntity.getCountry());
        assertEquals("5678 CD", existingEntity.getPostalCode());
        assertEquals("Updated Street", existingEntity.getStreet());
    }

    @Test
    void updateAddress_ShouldThrowException_WhenIdIsInvalid() {
        UpdateAddressRequest request = UpdateAddressRequest.builder()
                .addressId(99L)
                .city("Updated City")
                .country("Updated Country")
                .postalCode("5678 CD")
                .street("Updated Street")
                .build();

        when(addressRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> addressManager.updateAddress(request));
        verify(addressRepository, times(1)).findById(99L);
        verify(addressRepository, never()).save(any(AddressEntity.class));
    }

    @Test
    void deleteAddress_ShouldDelete_WhenIdIsValid() {
        long id = 1L;

        doNothing().when(addressRepository).deleteById(id);

        addressManager.deleteAddress(id);

        verify(addressRepository, times(1)).deleteById(id);
    }

    @Test
    void getAddressById_ShouldReturnAddress_WhenIdIsValid() {
        long id = 1L;
        AddressEntity addressEntity = AddressEntity.builder()
                .id(id)
                .city("Eindhoven")
                .country("Netherlands")
                .postalCode("1234 AB")
                .street("Fontys Street")
                .build();

        Address address = AddressConverter.convert(addressEntity);

        when(addressRepository.findById(id)).thenReturn(Optional.of(addressEntity));

        Optional<Address> result = addressManager.getAddressById(id);

        assertTrue(result.isPresent());
        assertEquals(address.getCity(), result.get().getCity());
        assertEquals(address.getCountry(), result.get().getCountry());
        verify(addressRepository, times(1)).findById(id);
    }

    @Test
    void getAddressById_ShouldReturnEmpty_WhenIdIsInvalid() {
        long id = 99L;

        when(addressRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Address> result = addressManager.getAddressById(id);

        assertTrue(result.isEmpty());
        verify(addressRepository, times(1)).findById(id);
    }
}
