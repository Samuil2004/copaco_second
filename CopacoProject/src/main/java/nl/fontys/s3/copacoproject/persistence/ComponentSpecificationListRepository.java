package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import nl.fontys.s3.copacoproject.persistence.entity.Component_SpecificationList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComponentSpecificationListRepository extends JpaRepository<Component_SpecificationList,Long> {
    List<Component_SpecificationList> findByComponentId(ComponentEntity component);

}
