package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import nl.fontys.s3.copacoproject.persistence.entity.Component_SpecificationList;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComponentSpecificationListRepository extends JpaRepository<Component_SpecificationList,Long> {
    List<Component_SpecificationList> findByComponentId(ComponentEntity component);

    @Query("SELECT cs FROM Component_SpecificationList cs " +
            "WHERE cs.componentId.componentId = :componentId")
    List<Component_SpecificationList> findByComponentId(@Param("componentId") Long componentId);

//@Query("SELECT cs FROM Component_SpecificationList cs " +
//        "WHERE cs.componentId.componentId = :componentId " +
//        "AND cs.specificationType = :specificationType")
//List<Component_SpecificationList> findByComponentIdAndSpecificationTypeId(
//        @Param("componentId") Long componentId,
//        @Param("specificationType") SpecificationTypeEntity specificationType);

    @Query("SELECT cs FROM Component_SpecificationList cs " +
            "WHERE cs.componentId.componentId = :componentId " +
            "AND cs.specificationType = :specificationType")
    List<Component_SpecificationList> findByComponentIdAndSpecificationTypeId(
            @Param("componentId") Long componentId,
            @Param("specificationType") SpecificationTypeEntity specificationType
    );

    // Query to fetch all values for the given combination
    @Query("SELECT csl.value " +
            "FROM Component_SpecificationList csl " +
            "WHERE csl.componentId.componentId = :componentId AND csl.specificationType.id = :specificationId")
    List<String> findValuesByComponentAndSpecification(long componentId, long specificationId);

    @Query("SELECT DISTINCT cs.value FROM Component_SpecificationList cs " +
            "WHERE cs.specificationType.id = 1070")
    List<String> getDistinctConfigurationTypes();

    @Query("SELECT DISTINCT cs.value " +
            "FROM Component_SpecificationList cs " +
            "JOIN cs.componentId c " +
            "JOIN c.componentType ct " +
            "WHERE ct.category.id = :categoryId AND cs.specificationType.id = 1070")
    List<String> getDistinctConfigurationTypesInCategory(@Param("categoryId") Long categoryId);


    @Query("SELECT cs.value " +
            "FROM Component_SpecificationList cs " +
            "WHERE cs.componentId.componentId = :componentId " +
            "AND cs.specificationType.id = :specificationTypeId")
    List<String> findValuesBySpecificationTypeIdAndComponentId(
            @Param("componentId") Long componentId,
            @Param("specificationTypeId") Long specificationTypeId);


    //the following query checks if a component satisfies a rule for a given component id, specification and expected values
    @Query("SELECT CASE WHEN COUNT(cs) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Component_SpecificationList cs " +
            "WHERE cs.componentId.componentId = :componentId " +
            "  AND cs.specificationType.id = :specificationTypeId " +
            "  AND cs.value IN :values")
    boolean existsByComponentIdAndSpecificationTypeIdAndValueIn(
            @Param("componentId") Long componentId,
            @Param("specificationTypeId") Long specificationTypeId,
            @Param("values") List<String> values);

}
