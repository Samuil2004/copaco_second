package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
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

    Optional<ComponentEntity> findByComponentId(Long componentId);

}
