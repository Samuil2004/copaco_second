package nl.fontys.s3.copacoproject.persistence.entity;

import nl.fontys.s3.copacoproject.business.dto.rule.RuleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompatibilityRepository extends JpaRepository<CompatibilityEntity, Long> {
    List<CompatibilityEntity> findByComponent1Id_IdOrComponent2Id_Id(Long component1Id, Long component2Id);

    //Old
    @Query("SELECT c FROM CompatibilityEntity c " +
            "WHERE ((c.component1Id.id = :component1Id AND c.component2Id.id = :component2Id) " +
            "   OR (c.component1Id.id = :component2Id AND c.component2Id.id = :component1Id)) " +
            "AND c.configurationType = :configurationType")
    List<CompatibilityEntity> findCompatibilityRecordsBetweenTwoComponentTypeIds(
            @Param("component1Id") Long component1Id,
            @Param("component2Id") Long component2Id,
            @Param("configurationType") String configurationType);


    //New
    //---------------------
    @Query("SELECT DISTINCT r.specificationToConsider1Id.id " +
            "FROM CompatibilityEntity ac " +
            "JOIN ac.ruleId r " +
            "WHERE ac.component1Id.id = :component1Id " +
            "AND ac.component2Id.id = :component2Id " +
            "AND ac.configurationType = :configurationType")
    List<Long> findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(
            @Param("component1Id") Long component1Id,
            @Param("component2Id") Long component2Id,
            @Param("configurationType") String configurationType);

@Query(value = """
        SELECT
            r.specification2_id AS specification2Id,
            STRING_AGG(r.value_of_second_specification, ', ') AS valueOfSecondSpecification
        FROM [Automatic_compatibility] ac
        JOIN [Rule_entity] r ON ac.rule_id = r.id
        WHERE ac.component1_id = :component1Id
          AND ac.component2_id = :component2Id
          AND ac.configuration_type = :configurationType
          AND r.specification1_id = :specification1Id
          AND (r.value_of_first_specification IN :valueOfFirstSpecifications OR r.value_of_first_specification IS NULL)
        GROUP BY r.specification2_id
        """, nativeQuery = true)
List<Object[]> findSpecification2IdsAndValuesOfSecondSpecification(
        @Param("component1Id") Long component1Id,
        @Param("component2Id") Long component2Id,
        @Param("configurationType") String configurationType,
        @Param("specification1Id") Long specification1Id,
        @Param("valueOfFirstSpecifications") List<String> valueOfFirstSpecifications
);

    @Query(value = """
        SELECT
             ctst.specification_type_id AS specification2Id,
            STRING_AGG(r.value_of_second_specification, ', ') AS valueOfSecondSpecification
        FROM [Automatic_compatibility] ac
        JOIN [Rule_entity] r ON ac.rule_id = r.id
        JOIN specfication_type_component_type ctst on ctst.id = r.specification2_id
        WHERE ac.component1_id = :component1Id
          AND ac.component2_id = :component2Id
          AND ac.configuration_type = :configurationType
          AND r.specification1_id = :specification1Id
          AND (r.value_of_first_specification IN :valueOfFirstSpecifications OR r.value_of_first_specification IS NULL)
        GROUP BY ctst.specification_type_id
        """, nativeQuery = true)
    List<Object[]> findSpecification2IdsAndValuesOfSecondSpecification2(
            @Param("component1Id") Long component1Id,
            @Param("component2Id") Long component2Id,
            @Param("configurationType") String configurationType,
            @Param("specification1Id") Long specification1Id,
            @Param("valueOfFirstSpecifications") List<String> valueOfFirstSpecifications
    );


    @Query(value = """
    SELECT 
        ctst.specification_type_id AS specification1Id, 
        STRING_AGG(r.value_of_first_specification, ', ') AS valueOfFirstSpecification
    FROM [Automatic_compatibility] ac
    JOIN [Rule_entity] r ON ac.rule_id = r.id
    JOIN specfication_type_component_type ctst ON ctst.id = r.specification1_id
    WHERE ac.component1_id = :component1Id
      AND ac.component2_id = :component2Id
      AND ac.configuration_type = :configurationType
      AND r.specification1_id = :specificationIdForFirstComponent
    GROUP BY ctst.specification_type_id
    """, nativeQuery = true)
    List<Object[]> findSpecification1IdsAndValuesOfFirstSpecification1ForFirstComponentType(
            @Param("component1Id") Long component1Id,
            @Param("component2Id") Long component2Id,
            @Param("specificationIdForFirstComponent") Long specificationIdForFirstComponent,
            @Param("configurationType") String configurationType
    );


    @Query("SELECT stct.specificationType.id " +
            "FROM RuleEntity r " +
            "JOIN CompatibilityEntity ac on ac.ruleId.id = r.id " +
            "JOIN SpecficationTypeList_ComponentTypeEntity stct on stct.id = r.specificationToConsider2Id.id " +
            "WHERE ac.component1Id.id = :component1Id " +
            "AND ac.component2Id.id = :component2Id " +
            "AND r.specificationToConsider1Id.id = :specification1Id " +
            "AND r.valueOfSecondSpecification IS NULL " +
            "AND ac.configurationType = :configurationType")
    Long getSecondComponentSpecificationId(
            @Param("component1Id") Long component1Id,
            @Param("component2Id") Long component2Id,
            @Param("specification1Id") Long specification1Id,
            @Param("configurationType") String configurationType);

    @Query("SELECT distinct cs.value " +
            "FROM Component_SpecificationList cs " +
            "JOIN ComponentEntity c on c.componentId = cs.componentId.componentId " +
            "WHERE cs.specificationType.id = :specificationTypeId " +
            "AND c.componentType.id = :componentTypeId")
    List<String> getDistinctValuesForASpecification(
            @Param("specificationTypeId") Long specificationTypeId,
            @Param("componentTypeId") Long componentTypeId);





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
    WHERE ( :configurationType IS NULL OR :configurationType = '' OR c.configurationType LIKE %:configurationType%)
    """)
    Page<RuleResponse> findRulesByConfigurationType(@Param("configurationType") String configurationType, Pageable pageable);

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
    WHERE r.id = :ruleId
    """)
    Optional<RuleResponse> findRuleById(@Param("ruleId") long ruleId);


    @Query("SELECT c FROM CompatibilityEntity c WHERE c.ruleId.id = :ruleId")
    Optional<CompatibilityEntity> findByRuleId(@Param("ruleId") Long ruleId);
}


