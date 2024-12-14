package nl.fontys.s3.copacoproject.persistence.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.persistence.CustomComponentRepository;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import nl.fontys.s3.copacoproject.persistence.entity.Component_SpecificationList;
import org.springframework.stereotype.Repository;


import java.util.*;
import java.util.function.Function;

@AllArgsConstructor
@Repository
public class ComponentRepositoryCustomImpl implements CustomComponentRepository {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
public List<ComponentEntity> findMatchingComponents(Long componentTypeId, Map<Long, List<String>> specificationConditions) {
    // Start building the criteria query
//    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//    CriteriaQuery<ComponentEntity> query = criteriaBuilder.createQuery(ComponentEntity.class);
//    Root<ComponentEntity> componentRoot = query.from(ComponentEntity.class);
//
//    // Subquery for matching components
//    Subquery<Long> subquery = query.subquery(Long.class);
//    Root<Component_SpecificationList> subqueryRoot = subquery.from(Component_SpecificationList.class);
//    subquery.select(subqueryRoot.get("componentId").get("id")); // Select component_id in subquery
//
//    // Build predicates for WHERE clause dynamically
//    List<Predicate> orPredicates = new ArrayList<>();
//    for (Map.Entry<Long, List<String>> entry : specificationConditions.entrySet()) {
//        Long specificationTypeId = entry.getKey();
//        List<String> values = entry.getValue();
//
//        Predicate specTypePredicate = criteriaBuilder.equal(
//                subqueryRoot.get("specificationType").get("id"), specificationTypeId);
//
//        Predicate valuePredicate;
//        if (values.size() == 1) {
//            valuePredicate = criteriaBuilder.equal(subqueryRoot.get("value"), values.get(0));
//        } else {
//            valuePredicate = subqueryRoot.get("value").in(values);
//        }
//
//        // Combine specification type and value predicates
//        orPredicates.add(criteriaBuilder.and(specTypePredicate, valuePredicate));
//    }
//
//    // Combine all OR predicates for the subquery
//    subquery.where(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
//
//    // Group by component_id and add HAVING clause
//    subquery.groupBy(subqueryRoot.get("componentId").get("id"));
//    subquery.having(criteriaBuilder.equal(criteriaBuilder.countDistinct(subqueryRoot.get("specificationType").get("id")),
//            specificationConditions.size()));
//
//    // Add the subquery to the main query
//    query.select(componentRoot)
//            .where(
//                    criteriaBuilder.equal(componentRoot.get("componentType").get("id"), componentTypeId),
//                    criteriaBuilder.in(componentRoot.get("id")).value(subquery)
//            );
//
//    // Execute query and return results
//    return entityManager.createQuery(query).getResultList();
    return List.of();
}

}


