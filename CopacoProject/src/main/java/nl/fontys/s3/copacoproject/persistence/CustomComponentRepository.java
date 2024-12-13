package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import nl.fontys.s3.copacoproject.persistence.entity.supportingEntities.SpecificationTypeAndValuesForIt;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface CustomComponentRepository{
    List<ComponentEntity> getComponentsWithSpecifications(List<SpecificationTypeAndValuesForIt> specifications, Pageable pageable);

}
