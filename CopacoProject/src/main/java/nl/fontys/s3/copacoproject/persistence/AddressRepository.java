package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.AddressEntity;

import java.util.List;
import java.util.Optional;

public interface AddressRepository {
    List<AddressEntity> findAll();
    Optional<AddressEntity> findById(Long id);
    AddressEntity save(AddressEntity address);
    void deleteById(Long id);
}
