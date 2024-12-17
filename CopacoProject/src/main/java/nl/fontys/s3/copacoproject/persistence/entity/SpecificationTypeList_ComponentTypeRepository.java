package nl.fontys.s3.copacoproject.persistence.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface SpecificationTypeList_ComponentTypeRepository extends JpaRepository<SpecficationTypeList_ComponentTypeEntity,Long> {
    @Query("SELECT st FROM SpecficationTypeList_ComponentTypeEntity st " +
            "WHERE st.componentType.id = :componentTypeId " +
            "AND st.specificationType.id = :specificationTypeId")
    SpecficationTypeList_ComponentTypeEntity findByComponentTypeAndSpecificationType(
            @Param("componentTypeId") Long componentTypeId,
            @Param("specificationTypeId") Long specificationTypeId);

    @Query("SELECT DISTINCT cs.value AS valueOfSpecificationInComponents " +
            "FROM SpecficationTypeList_ComponentTypeEntity cst " +
            "JOIN Component_SpecificationList cs " +
            "ON cs.specificationType=cst.specificationType " +
            "WHERE cst= :item " +
            "ORDER BY valueOfSpecificationInComponents ASC")
    Page<String> findSpecificationValuesBySpecificationTypeAndComponentType(SpecficationTypeList_ComponentTypeEntity item,
                                                                            Pageable pageable);
}
