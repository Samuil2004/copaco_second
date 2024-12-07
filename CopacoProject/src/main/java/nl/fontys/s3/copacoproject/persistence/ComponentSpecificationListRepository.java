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
}
//test