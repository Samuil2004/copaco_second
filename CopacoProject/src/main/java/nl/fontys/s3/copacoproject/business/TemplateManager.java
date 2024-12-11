package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.CreateTemplateRequest;
import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.UpdateTemplateRequest;
import nl.fontys.s3.copacoproject.domain.Template;

import java.util.List;

public interface TemplateManager {
    void createTemplate(CreateTemplateRequest request);
    void deleteTemplate(long id);
    Template getTemplateById(long id);
    List<Template> getTemplates();
    List<Template> getTemplatesByName(String name);
    List<Template> getFilteredTemplates(int itemsPerPage, int currentPage, Long categoryId, String configurationType);
    int getNumberOfTemplates(Long categoryId, String configurationType);
    void updateTemplate(long templateId, UpdateTemplateRequest request);
}
