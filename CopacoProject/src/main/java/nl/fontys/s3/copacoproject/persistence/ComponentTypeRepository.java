package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.CategoryEntity;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComponentTypeRepository extends JpaRepository<ComponentTypeEntity, Long> {
    ComponentTypeEntity findComponentTypeEntityById(Long id);
    List<ComponentTypeEntity> findComponentTypeEntitiesByCategory(CategoryEntity category);

    @Query("SELECT DISTINCT ct " +
            "FROM ComponentTypeEntity ct " +
            "JOIN ComponentEntity c ON c.componentType.id = ct.id " +
            "JOIN Component_SpecificationList csl ON csl.componentId.componentId = c.componentId " +
            "WHERE csl.specificationType.id IN (1070,1792,947,954) " +
            "AND csl.value = :value")
    List<ComponentTypeEntity> findDistinctComponentTypesByTypeOfConfiguration(
            @Param("value") String value);

    @Query("""
    SELECT ct
    FROM ComponentTypeEntity ct
    JOIN ComponentTypeList_Template ctl ON ct = ctl.componentType
    WHERE ctl.template.id = :templateId
    """)
    List<ComponentTypeEntity> getComponentTypeEntitiesByTemplateId(Long templateId);
}
