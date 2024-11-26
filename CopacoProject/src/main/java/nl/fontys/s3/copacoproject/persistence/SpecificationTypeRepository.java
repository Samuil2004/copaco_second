package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpecificationTypeRepository extends JpaRepository<SpecificationTypeEntity, Long> {
    Optional<SpecificationTypeEntity> findById(Long id);
    SpecificationTypeEntity findBySpecificationTypeName(String specificationTypeName);
    @Query("SELECT s.specificationType FROM SpecficationTypeList_ComponentTypeEntity s WHERE s.componentType.id = (SELECT c.componentType.id FROM ComponentEntity c WHERE c.componentId = :componentId)")
    List<SpecificationTypeEntity> findSpecificationTypeEntitiesByComponentId(@Param("componentId") Long componentId);
}
