package nl.fontys.s3.copacoproject.business.impl;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.CategoryManager;
import nl.fontys.s3.copacoproject.business.ComponentTypeManager;
import nl.fontys.s3.copacoproject.business.TemplateManager;
import nl.fontys.s3.copacoproject.business.converters.ComponentTypeConverter;
import nl.fontys.s3.copacoproject.business.converters.TemplateConverter;
import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.ComponentTypeItemInTemplate;
import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.CreateTemplateRequest;
import nl.fontys.s3.copacoproject.domain.Brand;
import nl.fontys.s3.copacoproject.domain.Category;
import nl.fontys.s3.copacoproject.domain.ComponentType;
import nl.fontys.s3.copacoproject.domain.Template;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeList_TemplateRepository;
import nl.fontys.s3.copacoproject.persistence.TemplateRepository;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeList_Template;
import nl.fontys.s3.copacoproject.persistence.entity.TemplateEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateManagerImpl implements TemplateManager {
    private final TemplateRepository templateRepository;
    private final ComponentTypeList_TemplateRepository componentTypeListRepository;
    private final ComponentTypeManager componentTypeManager;
    private final CategoryManager categoryManager;
    private final BrandManager brandManager;

    @Override
    public void createTemplate(CreateTemplateRequest request) {
        Category category = categoryManager.findCategoryById(request.getCategoryId());
        Brand brand = brandManager.getBrandById(request.getBrandId());

        Template template = Template.builder()
                .brand(brand)
                .name(request.getName())
                .category(category)
                .imageUrl(request.getImageUrl())
                .build();

        //save the template
        long id = templateRepository.save(TemplateConverter.convertFromBaseToEntity(template)).getId();
        template.setTemplateId(id);

        //add components in the template's list
        for(ComponentTypeItemInTemplate item : request.getComponentTypes()){
            ComponentType componentType = componentTypeManager.getComponentTypeById(item.getComponentTypeId());
            ComponentTypeList_Template listItem= ComponentTypeList_Template.builder()
                    .template(TemplateConverter.convertFromBaseToEntity(template))
                    .componentType(ComponentTypeConverter.convertFromBaseToEntity(componentType))
                    .orderOfImportance(item.getOrderOfImportance())
                    .build();
            componentTypeListRepository.save(listItem);
        }
    }

    @Override
    public void deleteTemplate(long id) {
        templateRepository.deleteById(id);
    }
    @Override
    public Template getTemplateById(long id) {
        return TemplateConverter.convertFromEntityToBase(templateRepository.findTemplateEntityById(id));
    }
    @Override
    public List<Template> getTemplatesByName(String name) {
        List<Template> templates = new ArrayList<>();
        List<TemplateEntity> templateEntities = templateRepository.findTemplateEntitiesByName(name);
        for(TemplateEntity templateEntity : templateEntities){
            templates.add(TemplateConverter.convertFromEntityToBase(templateEntity));
        }
        return templates;
    }
    @Override
    public List<Template> getTemplates() {
        List<Template> templates = new ArrayList<>();
        List<TemplateEntity> templateEntities = templateRepository.findAll();
        for(TemplateEntity templateEntity : templateEntities){
            templates.add(TemplateConverter.convertFromEntityToBase(templateEntity));
        }
        return templates;
    }
}
