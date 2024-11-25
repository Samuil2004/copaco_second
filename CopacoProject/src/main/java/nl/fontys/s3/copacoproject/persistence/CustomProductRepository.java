package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.CustomProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomProductRepository extends JpaRepository<CustomProductEntity, Long> {
    CustomProductEntity findById(long id);
}
