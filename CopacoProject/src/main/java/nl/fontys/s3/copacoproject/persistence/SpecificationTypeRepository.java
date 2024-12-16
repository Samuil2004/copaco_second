package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpecificationTypeRepository extends JpaRepository<SpecificationTypeEntity, Long> {
    Optional<SpecificationTypeEntity> findById(Long id);
    @Query("SELECT s.specificationType FROM SpecficationTypeList_ComponentTypeEntity s WHERE s.componentType.id = (SELECT c.componentType.id FROM ComponentEntity c WHERE c.componentId = :componentId)")
    List<SpecificationTypeEntity> findSpecificationTypeEntitiesByComponentId(@Param("componentId") Long componentId);
    @Query("SELECT s.specificationType " +
            "FROM SpecficationTypeList_ComponentTypeEntity s " +
            "WHERE s.componentType.id = :componentTypeId")
    Page<SpecificationTypeEntity> findSpecificationTypeEntitiesByComponentTypeId(@Param("componentTypeId") Long componentTypeId,
                                                                                 Pageable pageable);
    @Query("SELECT COUNT(s) " +
            "FROM SpecficationTypeList_ComponentTypeEntity s " +
            "WHERE s.componentType.id = :componentTypeId")
    int countSpecificationTypesByComponentTypeId(Long componentTypeId);
}
