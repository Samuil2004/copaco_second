package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.CategoryEntity;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeList_Template;
import nl.fontys.s3.copacoproject.persistence.entity.TemplateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.util.List;

public interface TemplateRepository extends JpaRepository<TemplateEntity, Long> {
    TemplateEntity findTemplateEntityById(long id);
    List<TemplateEntity> findTemplateEntitiesByName(String name);
    @Query("""
    SELECT t
    FROM TemplateEntity t
    WHERE (:category IS NULL OR t.category = :category)
      AND (:configurationType IS NULL OR :configurationType = '' OR t.configurationType LIKE %:configurationType%)
    """)
    Page<TemplateEntity> findTemplateEntitiesByCategoryAndConfigurationType(
            @Param("category") CategoryEntity category,
            @Param("configurationType") String configurationType,
            Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(te) > 0 THEN true ELSE false END FROM TemplateEntity te " +
            "WHERE te.name = :templateName AND te.brand.id = :brandId AND te.category.id = :categoryId")
    boolean existsTemplateEntityByNameAndBrandAndCategory(@Param("templateName") String templateName,
                                                          @Param("brandId") long brandId,
                                                          @Param("categoryId") long categoryId);
    @Query("SELECT CASE WHEN COUNT(te) > 0 THEN true ELSE false END FROM TemplateEntity te " +
            "WHERE te.name = :templateName AND te.brand.id = :brandId AND te.category.id = :categoryId AND te.id!= :templateId")
    boolean existsTemplateEntityForUpdate(@Param("templateId") long templateId,
                                          @Param("templateName") String templateName,
                                          @Param("brandId") long brandId,
                                          @Param("categoryId") long categoryId);
    @Query("SELECT c FROM ComponentTypeList_Template c WHERE c.template.id = :template_id")
    List<ComponentTypeList_Template> findComponentTypeListByTemplateId(@Param("template_id") long templateId);

    @Query("""
    SELECT COUNT(t)
    FROM TemplateEntity t
    WHERE ((:category IS NULL OR t.category = :category)
    AND (:configurationType IS NULL OR :configurationType = '' OR t.configurationType LIKE %:configurationType%))""")
    int countTemplateEntitiesByCategoryAndConfigurationType(@Nullable @Param("category") CategoryEntity category,
                                                            @Nullable @Param("configurationType") String configurationType);



}
