package nl.fontys.s3.copacoproject.persistence.entity;

import nl.fontys.s3.copacoproject.persistence.entity.supportingEntities.SpecificationTypeAndValuesForIt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AutomaticCompatibilityRepository extends JpaRepository<AutomaticCompatibilityEntity, Long> {
    List<AutomaticCompatibilityEntity> findByComponent1Id_IdOrComponent2Id_Id(Long component1Id, Long component2Id);

    //Old
    @Query("SELECT c FROM AutomaticCompatibilityEntity c " +
            "WHERE ((c.component1Id.id = :component1Id AND c.component2Id.id = :component2Id) " +
            "   OR (c.component1Id.id = :component2Id AND c.component2Id.id = :component1Id)) " +
            "AND c.configurationType = :configurationType")
    List<AutomaticCompatibilityEntity> findCompatibilityRecordsBetweenTwoComponentTypeIds(
            @Param("component1Id") Long component1Id,
            @Param("component2Id") Long component2Id,
            @Param("configurationType") String configurationType);


    //New
    //---------------------
    @Query("SELECT DISTINCT r.specificationToConsider1Id.id " +
            "FROM AutomaticCompatibilityEntity ac " +
            "JOIN ac.ruleId r " +
            "WHERE ac.component1Id.id = :component1Id " +
            "AND ac.component2Id.id = :component2Id " +
            "AND ac.configurationType = :configurationType")
    List<Long> findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(
            @Param("component1Id") Long component1Id,
            @Param("component2Id") Long component2Id,
            @Param("configurationType") String configurationType);


//    @Query("SELECT new nl.fontys.s3.copacoproject.persistence.entity.supportingEntities.SpecificationTypeAndValuesForIt(r.specificationToConsider2Id.id, r.valueOfSecondSpecification) " +
//            "FROM AutomaticCompatibilityEntity ac " +
//            "JOIN ac.ruleId r " +
//            "WHERE ac.component1Id.id = :component1Id " +
//            "  AND ac.component2Id.id = :component2Id " +
//            "  AND ac.configurationType = :configurationType " +
//            "  AND r.specificationToConsider1Id.id = :specification1Id " +
//            "  AND (r.valueOfFirstSpecification IN :valuesOfFirstSpecification OR r.valueOfFirstSpecification IS NULL)")
//    List<SpecificationTypeAndValuesForIt> findSpecification2IdsAndValuesOfSecondSpecification(
//            @Param("component1Id") Long component1Id,
//            @Param("component2Id") Long component2Id,
//            @Param("configurationType") String configurationType,
//            @Param("specification1Id") Long specification1Id,
//            @Param("valuesOfFirstSpecification") List<String> valuesOfFirstSpecification);

//    @Query(value = "SELECT r.specification2_id, STRING_AGG(r.value_of_second_specification, ', ') AS value_of_second_specification " +
//            "FROM automatic_compatibility ac " +
//            "JOIN rule_entity r ON ac.rule_id = r.id " +
//            "WHERE ac.component1_id = :component1Id " +
//            "  AND ac.component2_id = :component2Id " +
//            "  AND ac.configuration_type = :configurationType " +
//            "  AND r.specification1_id = :specification1Id " +
//            "  AND (r.value_of_first_specification IN :valuesOfFirstSpecification " +
//            "       OR r.value_of_first_specification IS NULL) " +
//            "GROUP BY r.specification2_id", nativeQuery = true)
//    List<SpecificationTypeAndValuesForIt> findSpecification2IdsAndValuesOfSecondSpecification(
//            @Param("component1Id") Long component1Id,
//            @Param("component2Id") Long component2Id,
//            @Param("configurationType") String configurationType,
//            @Param("specification1Id") Long specification1Id,
//            @Param("valuesOfFirstSpecification") List<String> valuesOfFirstSpecification);



//        @Query(value = """
//                    SELECT
//                        r.specification2_id AS specification2Id,
//                        GROUP_CONCAT(r.value_of_second_specification SEPARATOR ', ') AS valueOfSecondSpecification
//                    FROM [automatic_compatibility] ac
//                    JOIN [Rule_entity] r ON ac.rule_id = r.id
//                    WHERE ac.component1_id = :component1Id
//                      AND ac.component2_id = :component2Id
//                      AND ac.configuration_type = :configurationType
//                      AND r.specification1_id = :specification1Id
//                      AND (r.value_of_first_specification IN :valueOfFirstSpecifications OR r.value_of_first_specification IS NULL)
//                    GROUP BY r.specification2_id
//                    """, nativeQuery = true)
//        List<SpecificationTypeAndValuesForIt> findSpecification2IdsAndValuesOfSecondSpecification(
//                @Param("component1Id") Long component1Id,
//                @Param("component2Id") Long component2Id,
//                @Param("configurationType") String configurationType,
//                @Param("specification1Id") Long specification1Id,
//                @Param("valueOfFirstSpecifications") List<String> valueOfFirstSpecifications
//        );
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
}


