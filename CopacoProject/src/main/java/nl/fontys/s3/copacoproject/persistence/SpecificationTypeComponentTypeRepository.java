package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeEntity;
import nl.fontys.s3.copacoproject.persistence.entity.SpecficationTypeList_ComponentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecificationTypeComponentTypeRepository extends JpaRepository<SpecficationTypeList_ComponentTypeEntity, Long> {
    List<SpecficationTypeList_ComponentTypeEntity> findAllByComponentType(ComponentTypeEntity componentType);
}
