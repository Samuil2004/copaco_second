package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findById(Long id);
    CategoryEntity findByCategoryName(String name);

}
