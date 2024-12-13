package nl.fontys.s3.copacoproject.persistence.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
import nl.fontys.s3.copacoproject.persistence.CustomComponentRepository;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import nl.fontys.s3.copacoproject.persistence.entity.supportingEntities.SpecificationTypeAndValuesForIt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@AllArgsConstructor
@Repository
@EnableJpaRepositories(basePackages = "nl.fontys.s3.copacoproject.persistence")
public class ComponentRepositoryCustomImpl implements CustomComponentRepository {

    private EntityManager entityManager;

    @Override
    public List<ComponentEntity> getComponentsWithSpecifications(List<SpecificationTypeAndValuesForIt> specifications, Pageable pageable) {

//        StringBuilder sqlQuery = new StringBuilder("SELECT c.* FROM Component c " +
//                "JOIN Component_Specification cs ON c.id = cs.component_id " +
//                "WHERE ");
//
//        // Dynamically build the conditions
//        List<String> conditions = new ArrayList<>();
//        for (int i = 0; i < specifications.size(); i++) {
//            SpecificationTypeAndValuesForIt spec = specifications.get(i);
//            conditions.add("(cs.specification_type_id = :specificationId" + i +
//                    " AND cs.value IN :values" + i + ")");
//        }
//
//        // Join all conditions with OR
//        sqlQuery.append(String.join(" AND ", conditions));
//        sqlQuery.append(" ORDER BY c.componentId");
//
//        // Prepare the query
//        Query query = entityManager.createNativeQuery(sqlQuery.toString(), ComponentEntity.class);
//
//        // Set parameters dynamically
//        for (int i = 0; i < specifications.size(); i++) {
//            SpecificationTypeAndValuesForIt spec = specifications.get(i);
//            query.setParameter("specificationId" + i, spec.getSpecificationId());
//            query.setParameter("values" + i, Arrays.asList(spec.getValuesToBeConsideredForThisSpecification().split("\\s*,\\s*")));
//        }
//
//        // Set pagination
//        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
//        query.setMaxResults(pageable.getPageSize());
//
//        return query.getResultList();
//        StringBuilder sqlQuery = new StringBuilder("SELECT c FROM ComponentEntity c " +
//                "JOIN Component_Specification cs ON c.id = cs.component_id " +
//                "WHERE ");
//
//        // Dynamically build the conditions
//        List<String> conditions = new ArrayList<>();
//        for (int i = 0; i < specifications.size(); i++) {
//            SpecificationTypeAndValuesForIt spec = specifications.get(i);
//
//            // Create the condition for each specification type and its values
//            conditions.add("(cs.specification_type_id = :specificationId" + i +
//                    " AND cs.value IN :values" + i + ")");
//        }
//
//        // Join all conditions with AND
//        sqlQuery.append(String.join(" AND ", conditions));
//        sqlQuery.append(" ORDER BY c.componentId");
//
//        // Prepare the query
//        Query query = entityManager.createQuery(sqlQuery.toString(), ComponentEntity.class);
//
//        // Set parameters dynamically
//        for (int i = 0; i < specifications.size(); i++) {
//            SpecificationTypeAndValuesForIt spec = specifications.get(i);
//            // Set specification ID parameter
//            query.setParameter("specificationId" + i, spec.getSpecificationId());
//
//            // Set values as a list
//            query.setParameter("values" + i, Arrays.asList(spec.getValuesToBeConsideredForThisSpecification().split("\\s*,\\s*")));
//        }
//
//        // Set pagination
//        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
//        query.setMaxResults(pageable.getPageSize());
//
//        // Execute and return the result
//        return query.getResultList();
        return List.of();
    }
}


