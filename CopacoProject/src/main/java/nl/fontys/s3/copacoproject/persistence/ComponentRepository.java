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

//    @Query("SELECT DISTINCT c FROM ComponentEntity c " +
//            "JOIN Component_SpecificationList cs1 ON cs1.componentId = c " +
//            "JOIN Component_SpecificationList cs2 ON cs2.componentId = c" +
//            "WHERE c.componentType.id = :componentTypeId " +
//            "AND cs1.specificationType.id = :specificationTypeId1 " +
//            "AND cs1.value IN :values1 " +
//            "AND cs2.specificationType.id = :specificationTypeId2 " +
//            "AND cs2.value IN :values2")
//    Page<ComponentEntity> findComponentsByTypeAndSpecificationsAndPurpose(
//            @Param("componentTypeId") Long componentTypeId,
//            @Param("specificationTypeId1") Long specificationTypeForRule,
//            @Param("values1") List<String> values1,
//            @Param("specificationTypeId2") Long specificationForPurpose,
//            @Param("values2") List<String> values2,
//            Pageable pageable);

    @Query("SELECT DISTINCT c FROM ComponentEntity c " +
            "JOIN Component_SpecificationList cs1 ON cs1.componentId = c " +
            "JOIN Component_SpecificationList cs2 ON cs2.componentId = c " +
            "WHERE c.componentType.id = :componentTypeId " +
            "AND cs1.specificationType.id = :specificationTypeForRule " +
            "AND cs1.value IN :values1 " +
            "AND cs2.specificationType.id = :specificationForPurpose " +
            "AND cs2.value IN :values2")
    Page<ComponentEntity> findComponentsByTypeAndSpecificationsAndPurpose(
            @Param("componentTypeId") Long componentTypeId,
            @Param("specificationTypeForRule") Long specificationTypeForRule,
            @Param("values1") List<String> values1,
            @Param("specificationForPurpose") Long specificationForPurpose,
            @Param("values2") List<String> values2,
            Pageable pageable);

    @Query("SELECT c FROM ComponentEntity c WHERE c.componentType.category.id = :categoryId")
    List<ComponentEntity> findComponentEntitiesByCategory(@Param("categoryId") long categoryId);

    Optional<ComponentEntity> findByComponentId(Long componentId);
    ComponentEntity findComponentEntityByComponentId(Long componentId);

    List<ComponentEntity> findByComponentType_Id(Long componentTypeId);

    List<ComponentEntity> findFirst10ByComponentType_Id(Long componentTypeId);

    List<ComponentEntity> findByComponentType_Id(Long componentTypeId, Pageable pageable);

    @Query("""
    SELECT DISTINCT c
    FROM ComponentEntity c
    JOIN Component_SpecificationList cs ON c.componentId = cs.componentId.componentId
    WHERE c.componentType.id = :componentTypeId
      AND cs.specificationType.id = :specificationTypeId
      AND cs.value IN :values
""")
    List<ComponentEntity> findComponentsByGivenComponentTypeAndSpecificationForMeantFor(
            @Param("componentTypeId") Long componentTypeId,
            @Param("specificationTypeId") Long specificationTypeId,
            @Param("values") List<String> values,
            Pageable pageable
    );

    @Query("SELECT c.componentType.id FROM ComponentEntity c WHERE c.componentId = :componentId")
    Long findComponentTypeIdByComponentId(@Param("componentId") Long componentId);

    @Query("""
    SELECT DISTINCT c
    FROM ComponentEntity c
    JOIN Component_SpecificationList cs ON c.componentId = cs.componentId.componentId
    WHERE c.componentType.id = :componentTypeId
    AND cs.specificationType.id IN (947, 954, 1070)
    AND cs.value = :configurationType
    """)
    Page<ComponentEntity> findComponentEntityByComponentTypeAndConfigurationType(Long componentTypeId, String configurationType, Pageable pageable);

    @Query("""
    SELECT COUNT(c)
    FROM ComponentEntity c
    JOIN Component_SpecificationList cs ON c.componentId = cs.componentId.componentId
    WHERE c.componentType.id = :componentTypeId
    AND cs.specificationType.id IN (947, 954, 1070)
    AND cs.value = :configurationType
    """)
    Integer countByComponentTypeAndConfigurationType(Long componentTypeId, String configurationType);
}
