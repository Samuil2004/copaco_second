package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeList_Template;
import nl.fontys.s3.copacoproject.persistence.entity.primaryKeys.ComponentTypeList_Template_CPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComponentTypeList_TemplateRepository extends JpaRepository<ComponentTypeList_Template, ComponentTypeList_Template_CPK> {
}
