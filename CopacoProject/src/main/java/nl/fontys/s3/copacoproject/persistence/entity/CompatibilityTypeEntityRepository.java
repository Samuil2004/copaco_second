package nl.fontys.s3.copacoproject.persistence.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompatibilityTypeEntityRepository extends JpaRepository<CompatibilityTypeEntity, Long> {
}
