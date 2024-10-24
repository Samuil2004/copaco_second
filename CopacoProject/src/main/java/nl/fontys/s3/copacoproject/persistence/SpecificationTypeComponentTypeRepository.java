package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeEntity;
import nl.fontys.s3.copacoproject.persistence.entity.SpecficationTypeList_ComponentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecificationTypeComponentTypeRepository extends JpaRepository<SpecficationTypeList_ComponentType, Long> {
    List<SpecficationTypeList_ComponentType> findAllByComponentType(ComponentTypeEntity componentType);
}
