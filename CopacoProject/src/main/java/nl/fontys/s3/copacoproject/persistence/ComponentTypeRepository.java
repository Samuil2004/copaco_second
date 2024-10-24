package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComponentTypeRepository extends JpaRepository<ComponentTypeEntity, Long> {
    Optional<ComponentTypeEntity> findById(Long id);


}
