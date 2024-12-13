package nl.fontys.s3.copacoproject.persistence.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RuleEntityRepository extends JpaRepository<RuleEntity, Long> {

    @Query("SELECT r.valueOfSecondSpecification " +
            "FROM RuleEntity r " +
            "WHERE r.specificationToConsider1Id.id = :specification1Id " +
            "AND r.specificationToConsider2Id.id = :specification2Id " +
            "AND r.valueOfFirstSpecification IN :values")
    List<String> findValuesOfSecondSpecificationForManualCompatibility(
            @Param("specification1Id") Long specification1Id,
            @Param("specification2Id") Long specification2Id,
            @Param("values") List<String> values);

    List<RuleEntity> findBySpecificationToConsider1IdIdAndSpecificationToConsider2IdId(
            Long specification1Id,
            Long specification2Id
    );

    List<RuleEntity> findBySpecificationToConsider1IdIdAndSpecificationToConsider2IdIdAndConfigurationType(
            Long specification1Id,
            Long specification2Id,
            String configurationType
    );

}
