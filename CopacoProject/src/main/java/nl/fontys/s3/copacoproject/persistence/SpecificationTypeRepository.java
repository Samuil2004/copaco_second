package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecificationTypeRepository extends JpaRepository<SpecificationTypeEntity, Long> {
    Optional<SpecificationTypeEntity> findById(Long id);
    SpecificationTypeEntity findBySpecificationTypeName(String specificationTypeName);
}
