package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeList_Template;
import nl.fontys.s3.copacoproject.persistence.entity.TemplateEntity;
import nl.fontys.s3.copacoproject.persistence.entity.primaryKeys.ComponentTypeList_Template_CPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ComponentTypeList_TemplateRepository extends JpaRepository<ComponentTypeList_Template, ComponentTypeList_Template_CPK> {
    @Transactional
    @Modifying
    @Query("DELETE FROM ComponentTypeList_Template ct WHERE ct.template.id = :templateId")
    void deleteByTemplateId(@Param("templateId") Long templateId);

    List<ComponentTypeList_Template> findComponentTypeList_TemplatesByTemplate(TemplateEntity template);
}
