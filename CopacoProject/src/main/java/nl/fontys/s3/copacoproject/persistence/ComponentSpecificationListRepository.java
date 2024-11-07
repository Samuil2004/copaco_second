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
            "WHERE cs.componentId = :componentId " +
            "AND cs.specificationType = :specificationTypeId")
    List<Component_SpecificationList> findByComponentIdAndSpecificationTypeId(
            @Param("componentId") ComponentEntity componentId,
            @Param("specificationTypeId") SpecificationTypeEntity specificationTypeId);
}
//test