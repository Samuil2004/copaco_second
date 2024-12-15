package nl.fontys.s3.copacoproject.persistence.entity;

import nl.fontys.s3.copacoproject.business.dto.rule.RuleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompatibilityRepository extends JpaRepository<CompatibilityEntity, Long> {
    List<CompatibilityEntity> findByComponent1Id_IdOrComponent2Id_Id(Long component1Id, Long component2Id);

    //Old
//    @Query("SELECT c FROM AutomaticCompatibilityEntity c " +
//            "WHERE (c.component1Id = :component1 AND c.component2Id = :component2) " +
//            "OR (c.component1Id = :component2 AND c.component2Id = :component1)")
//    List<AutomaticCompatibilityEntity> findCompatibilityRecordsBetweenTwoComponentTypes(
//            @Param("component1") ComponentTypeEntity component1,
//            @Param("component2") ComponentTypeEntity component2);

    //New

    @Query("SELECT c FROM CompatibilityEntity c " +
            "WHERE (c.component1Id.id = :component1Id AND c.component2Id.id = :component2Id) " +
            "   OR (c.component1Id.id = :component2Id AND c.component2Id.id = :component1Id)")
    List<CompatibilityEntity> findCompatibilityRecordsBetweenTwoComponentTypeIds(
            @Param("component1Id") Long component1Id,
            @Param("component2Id") Long component2Id);

    @Query("""
    SELECT new nl.fontys.s3.copacoproject.business.dto.rule.RuleResponse(
        r.id,
        new nl.fontys.s3.copacoproject.business.dto.rule.ComponentTypeInRuleResponse(
            ct1.id,
            ct1.componentTypeName,
            s1.id,
            s1.specificationTypeName,
            r.valueOfFirstSpecification
        ),
        new nl.fontys.s3.copacoproject.business.dto.rule.ComponentTypeInRuleResponse(
            ct2.id,
            ct2.componentTypeName,
            s2.id,
            s2.specificationTypeName,
            r.valueOfSecondSpecification
        ),
        r.configurationType
    )
    FROM CompatibilityEntity c
    JOIN c.ruleId r
    JOIN c.component1Id ct1
    JOIN c.component2Id ct2
    JOIN r.specificationToConsider1Id s1InCompType
    JOIN s1InCompType.specificationType s1
    JOIN r.specificationToConsider2Id s2InCompType
    JOIN s2InCompType.specificationType s2
    WHERE c.configurationType = :configurationType
    """)
    Page<RuleResponse> findRulesByConfigurationType(@Param("configurationType") String configurationType, Pageable pageable);



}
