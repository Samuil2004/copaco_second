package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.CategoryEntity;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComponentTypeRepository extends JpaRepository<ComponentTypeEntity, Long> {
    //Optional<ComponentTypeEntity> findById(Long id);
    ComponentTypeEntity findComponentTypeEntityById(Long id);
    List<ComponentTypeEntity> findComponentTypeEntitiesByCategory(CategoryEntity category);

}
