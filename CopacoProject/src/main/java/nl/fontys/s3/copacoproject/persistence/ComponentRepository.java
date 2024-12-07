package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ComponentRepository extends JpaRepository<ComponentEntity,Long> {

    @Query("SELECT c FROM ComponentEntity c " +
            "JOIN Component_SpecificationList cs ON cs.componentId = c " +
            "WHERE c.componentType.id = :componentTypeId " +
            "AND cs.specificationType.id = :specificationTypeId " +
            "AND cs.value IN :values")
    List<ComponentEntity> findComponentsByTypeAndSpecification(
            @Param("componentTypeId") Long componentTypeId,
            @Param("specificationTypeId") Long specificationTypeId,
            @Param("values") List<String> values);


    @Query("SELECT c FROM ComponentEntity c " +
            "JOIN Component_SpecificationList cs ON cs.componentId = c " +
            "WHERE c.componentType.id = :componentTypeId " +
            "AND cs.specificationType.id = :specificationTypeId " +
            "AND cs.value IN :values")
    Page<ComponentEntity> findComponentsByTypeAndSpecification(
            @Param("componentTypeId") Long componentTypeId,
            @Param("specificationTypeId") Long specificationTypeId,
            @Param("values") List<String> values,
            Pageable pageable);

    @Query("SELECT c FROM ComponentEntity c WHERE c.componentType.category.id = :categoryId")
    List<ComponentEntity> findComponentEntitiesByCategory(@Param("categoryId") long categoryId);

    Optional<ComponentEntity> findByComponentId(Long componentId);
    ComponentEntity findComponentEntityByComponentId(Long componentId);

    List<ComponentEntity> findByComponentType_Id(Long componentTypeId);

    List<ComponentEntity> findFirst10ByComponentType_Id(Long componentTypeId);

    List<ComponentEntity> findByComponentType_Id(Long componentTypeId, Pageable pageable);

    @Query("SELECT c.componentType.id FROM ComponentEntity c WHERE c.componentId = :componentId")
    Long findComponentTypeIdByComponentId(@Param("componentId") Long componentId);
}
