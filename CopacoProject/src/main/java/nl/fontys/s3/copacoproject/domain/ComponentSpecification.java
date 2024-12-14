package nl.fontys.s3.copacoproject.domain;
import org.springframework.data.jpa.domain.Specification;

import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComponentSpecification {
    public static Specification<ComponentEntity> buildDynamicSpecification(
            Long componentTypeId,
            Map<Long, List<String>> specificationsMap) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Ensure distinct results
            query.distinct(true);

            // Filter by Component Type ID
            if (componentTypeId != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("componentType").get("id"), componentTypeId));
            }

            // Create a separate join for each specification in the map
            int joinIndex = 0;
            for (Map.Entry<Long, List<String>> entry : specificationsMap.entrySet()) {
                Long specificationTypeId = entry.getKey();
                List<String> values = entry.getValue();

                // Create a join with the Component_SpecificationList entity
                Join<Object, Object> specJoin = root.join("componentId", JoinType.INNER);

                // Filter by specificationTypeId
                Predicate specTypePredicate = criteriaBuilder.equal(
                        specJoin.get("specificationType").get("id"), specificationTypeId);

                // Filter by values
                Predicate valuePredicate = specJoin.get("value").in(values);

                // Combine conditions for this join
                predicates.add(criteriaBuilder.and(specTypePredicate, valuePredicate));
            }

            // Combine all predicates into a single AND condition
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
