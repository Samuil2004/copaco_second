package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemplateRepository extends JpaRepository<TemplateEntity, Long> {
    TemplateEntity findTemplateEntityById(long id);
    List<TemplateEntity> findTemplateEntitiesByName(String name);
}
