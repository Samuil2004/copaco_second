package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.domain.Component;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;

import java.util.List;

public interface ComponentRepository {
    List<ComponentEntity> getAllComponents();
    List<ComponentEntity> getComponentsByType(String type);

}
