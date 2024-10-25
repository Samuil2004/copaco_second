package nl.fontys.s3.copacoproject.persistence.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpecificationTypeList_ComponentTypeRepository extends JpaRepository<SpecficationTypeList_ComponentTypeEntity,Long> {
    @Query("SELECT st FROM SpecficationTypeList_ComponentTypeEntity st " +
            "WHERE st.componentType.id = :componentTypeId " +
            "AND st.specificationType.id = :specificationTypeId")
    SpecficationTypeList_ComponentTypeEntity findByComponentTypeAndSpecificationType(
            @Param("componentTypeId") Long componentTypeId,
            @Param("specificationTypeId") Long specificationTypeId);
}
