package nl.fontys.s3.copacoproject.persistence;
import java.util.*;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import nl.fontys.s3.copacoproject.persistence.entity.Component_SpecificationList;
import nl.fontys.s3.copacoproject.persistence.entity.supportingEntities.SpecificationTypeAndValuesForIt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
public interface ComponentRepository extends JpaRepository<ComponentEntity, Long>, JpaSpecificationExecutor<ComponentEntity> {

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

    @Query("SELECT DISTINCT c " +
    "FROM ComponentEntity c " +
    "JOIN Component_SpecificationList cs ON c.componentId = cs.componentId.componentId " +
    "WHERE c.componentType.id = :componentTypeId " +
      "AND cs.specificationType.id = :specificationTypeId " +
      "AND cs.value IN :values ")
    List<ComponentEntity> findComponentsByGivenComponentTypeAndSpecificationForMeantFor(
            @Param("componentTypeId") Long componentTypeId,
            @Param("specificationTypeId") Long specificationTypeId,
            @Param("values") List<String> values,
            Pageable pageable
    );

    @Query("SELECT c.componentType.id FROM ComponentEntity c WHERE c.componentId = :componentId")
    Long findComponentTypeIdByComponentId(@Param("componentId") Long componentId);

//    @Query("SELECT c FROM ComponentEntity c " +
//            "JOIN Component_SpecificationList cs1 ON cs1.componentId.componentId = c.componentId " +
//            "JOIN Component_SpecificationList cs2 ON cs2.componentId.componentId = c.componentId " +
//            "JOIN Component_SpecificationList cs3 ON cs3.componentId.componentId = c.componentId " +
//            "WHERE c.componentType.id = 5 " +
//            "AND cs1.specificationType.id = 1036 AND CAST(cs1.value AS decimal(10,2)) >= :totalPowerSupply " +
//            "AND cs2.specificationType.id = 947 AND cs2.value = :configurationType " +
//            "AND cs3.specificationType.id = 1293 AND CAST(cs3.value AS decimal(10,2)) >= :total12ThLineSupply")
//    List<ComponentEntity> findCompatiblePowerSupply(
//            @Param("totalPowerSupply") Double totalPowerSupply,
//            @Param("configurationType") String configurationType,
//            @Param("total12ThLineSupply") Double total12ThLineSupply,
//            Pageable pageable
//    );

    @Query(value = "SELECT c.* FROM component c " +
            "JOIN component_specification cs1 ON cs1.component_id = c.id " +
            "JOIN component_specification cs2 ON cs2.component_id = c.id " +
            "JOIN component_specification cs3 ON cs3.component_id = c.id " +
            "WHERE c.component_type_id = 5 " +
            "AND cs1.specification_type_id = 1036 AND TRY_CAST(cs1.value AS DECIMAL(10,2)) >= :totalPowerSupply " +
            "AND cs2.specification_type_id = 947 AND cs2.value = :configurationType " +
            "AND cs3.specification_type_id = 1293 AND TRY_CAST(cs3.value AS DECIMAL(10,2)) >= :total12ThLineSupply", nativeQuery = true)
    List<ComponentEntity> findComponentsBySpecificationsNative(
            @Param("totalPowerSupply") Double totalPowerSupply,
            @Param("configurationType") String configurationType,
            @Param("total12ThLineSupply") Double total12ThLineSupply,
            Pageable pageable
    );



//    static Specification<ComponentEntity> dynamicSpecification(
//            Long componentTypeId,
//            Map<Long, List<String>> specificationConditions
//    ) {
//        return (root, query, criteriaBuilder) -> {
//            // Ensure distinct results
//            query.distinct(true);
//
//            // List to hold all predicates
//            List<Predicate> predicates = new ArrayList<>();
//
//            predicates.add(criteriaBuilder.equal(root.get("componentType").get("id"), componentTypeId));
//
//            // Dynamically join and filter specifications
//            for (Map.Entry<Long, List<String>> entry : specificationConditions.entrySet()) {
//                // Create a join for each specification type
//                Subquery<Long> subquery = query.subquery(Long.class);
//                Root<Component_SpecificationList> specRoot = subquery.from(Component_SpecificationList.class);
//
//                // Correlated join between main query and subquery
//                Predicate specTypePredicate = criteriaBuilder.equal(
//                        specRoot.get("specificationType").get("id"),
//                        entry.getKey()
//                );
//
//                // Value predicate (either exact match or IN clause)
//                Predicate valuePredicate = entry.getValue().size() == 1
//                        ? criteriaBuilder.equal(specRoot.get("value"), entry.getValue().get(0))
//                        : specRoot.get("value").in(entry.getValue());
//
//                // Correlation predicate
//                Predicate correlationPredicate = criteriaBuilder.equal(
//                        specRoot.get("componentId").get("componentId"),
//                        root.get("componentId")
//                );
//
//                // Combine predicates in subquery
//                subquery.select(specRoot.get("componentId"))
//                        .where(criteriaBuilder.and(specTypePredicate, valuePredicate, correlationPredicate));
//
//                // Add exists condition to main query
//                predicates.add(criteriaBuilder.exists(subquery));
//            }
//
//            // Convert predicates to array and return
//            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
//        };
//    }

    static Specification<ComponentEntity> dynamicSpecification(
            Long componentTypeId,
            Map<Long, List<String>> specificationConditions) {

        return (root, query, criteriaBuilder) -> {
            // Start building the criteria query
            query.distinct(true);  // Ensure distinct results

            // Subquery for matching components
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Component_SpecificationList> subqueryRoot = subquery.from(Component_SpecificationList.class);
            subquery.select(subqueryRoot.get("componentId").get("id")); // Select component_id in subquery

            // Build predicates for WHERE clause dynamically
            List<Predicate> orPredicates = new ArrayList<>();
            for (Map.Entry<Long, List<String>> entry : specificationConditions.entrySet()) {
                Long specificationTypeId = entry.getKey();
                List<String> values = entry.getValue();

                Predicate specTypePredicate = criteriaBuilder.equal(
                        subqueryRoot.get("specificationType").get("id"), specificationTypeId);

                Predicate valuePredicate;
                if (values.size() == 1) {
                    valuePredicate = criteriaBuilder.equal(subqueryRoot.get("value"), values.get(0));
                } else {
                    valuePredicate = subqueryRoot.get("value").in(values);
                }

                // Combine specification type and value predicates
                orPredicates.add(criteriaBuilder.and(specTypePredicate, valuePredicate));
            }

            // Combine all OR predicates for the subquery
            subquery.where(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));

            // Group by component_id and add HAVING clause
            subquery.groupBy(subqueryRoot.get("componentId").get("id"));
            subquery.having(criteriaBuilder.equal(
                    criteriaBuilder.countDistinct(subqueryRoot.get("specificationType").get("id")),
                    specificationConditions.size()));

            // Add the subquery to the main query
            Predicate componentTypePredicate = criteriaBuilder.equal(
                    root.get("componentType").get("id"), componentTypeId);
            Predicate componentInSubqueryPredicate = criteriaBuilder.in(root.get("id")).value(subquery);

            // Combine both predicates (component type and the subquery)
            return criteriaBuilder.and(componentTypePredicate, componentInSubqueryPredicate);
        };
    }


}
