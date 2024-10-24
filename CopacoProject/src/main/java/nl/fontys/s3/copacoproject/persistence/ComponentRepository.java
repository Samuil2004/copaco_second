package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComponentRepository extends JpaRepository<ComponentEntity,Long> {
    List<ComponentEntity> getAllComponents();
    List<ComponentEntity> getComponentsByType(String type);
    ComponentEntity getComponentEntitiesByComponentId(Long Id);


}
