package nl.fontys.s3.copacoproject.persistence.impl;

import nl.fontys.s3.copacoproject.persistence.AddressRepository;
import nl.fontys.s3.copacoproject.persistence.entity.AddressEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class FakeAddressRepository implements AddressRepository {
    private static long NEXT_ID = 1;
    private final List<AddressEntity> savedAddresses;
    public FakeAddressRepository() {
        savedAddresses = new ArrayList<>();
    }
    @Override
    public List<AddressEntity> findAll() {
        return Collections.unmodifiableList(this.savedAddresses);
    }

    @Override
    public Optional<AddressEntity> findById(Long id) {
        return this.savedAddresses
                .stream()
                .filter(addressEntity -> addressEntity.getId() ==id)
                .findFirst();
    }

    @Override
    public AddressEntity save(AddressEntity address) {
        if (address != null) {
            address.setId(NEXT_ID++);
            savedAddresses.add(address);
            return address;
        } else {
            return null;
        }
    }

    @Override
    public void deleteById(Long id) {
        this.savedAddresses.removeIf(addressEntity -> addressEntity.getId() == id);

    }

}
