package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeEntity;
import nl.fontys.s3.copacoproject.persistence.entity.SpecficationTypeList_ComponentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpecificationTypeComponentTypeRepository extends JpaRepository<SpecficationTypeList_ComponentTypeEntity, Long> {
    List<SpecficationTypeList_ComponentTypeEntity> findAllByComponentType(ComponentTypeEntity componentType);

    @Query("SELECT e.id FROM SpecficationTypeList_ComponentTypeEntity e " +
            "WHERE e.componentType.id = :componentTypeId " +
            "AND e.specificationType.id = :specificationTypeId")
    Long findIdByComponentTypeIdAndSpecificationTypeId(@Param("componentTypeId") Long componentTypeId,
                                                       @Param("specificationTypeId") Long specificationTypeId);


    @Query("SELECT e.specificationType.id FROM SpecficationTypeList_ComponentTypeEntity e " +
            "WHERE e.componentType.id = :componentTypeId " +
            "AND e.id = :relationId")
    Long findSpecificationTypeIdByComponentTypeIdAndComponentTypeSpecificationRelationId
            (@Param("componentTypeId") Long componentTypeId,
            @Param("relationId") Long relationId);

}
