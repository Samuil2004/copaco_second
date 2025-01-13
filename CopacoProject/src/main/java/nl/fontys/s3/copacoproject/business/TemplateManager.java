package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.template_dto.CreateTemplateRequest;
import nl.fontys.s3.copacoproject.business.dto.template_dto.TemplateObjectResponse;
import nl.fontys.s3.copacoproject.business.dto.template_dto.UpdateTemplateRequest;
import nl.fontys.s3.copacoproject.domain.Template;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TemplateManager {
    void createTemplate(CreateTemplateRequest request, MultipartFile file) throws IOException;
    TemplateObjectResponse getTemplateById(long id);
    List<Template> getTemplates();
    List<Template> getTemplatesByName(String name);
    List<TemplateObjectResponse> getFilteredTemplates(int itemsPerPage, int currentPage, Long categoryId, String configurationType);
    int getNumberOfTemplates(Long categoryId, String configurationType);
    void updateTemplate(long templateId, UpdateTemplateRequest request, MultipartFile file) throws IOException;
    void updateTemplateStatus(Long id, boolean active);
}
