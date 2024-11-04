package nl.fontys.s3.copacoproject.business.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.CategoryManager;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.TemplateManager;
import nl.fontys.s3.copacoproject.business.converters.TemplateConverter;
import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.ComponentTypeItemInTemplate;
import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.CreateTemplateRequest;
import nl.fontys.s3.copacoproject.domain.Brand;
import nl.fontys.s3.copacoproject.domain.Category;
import nl.fontys.s3.copacoproject.domain.Template;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeList_TemplateRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.TemplateRepository;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeEntity;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeList_Template;
import nl.fontys.s3.copacoproject.persistence.entity.TemplateEntity;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TemplateManagerImpl implements TemplateManager {
    private final TemplateRepository templateRepository;
    private final ComponentTypeList_TemplateRepository componentTypeListRepository;
    private final ComponentTypeRepository componentTypeRepository;
    private final CategoryManager categoryManager;
    private final BrandManager brandManager;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void createTemplate(CreateTemplateRequest request) {
        // Retrieve related entities
        Category category = categoryManager.findCategoryById(request.getCategoryId());
        Brand brand = brandManager.getBrandById(request.getBrandId());

        if(category == null || brand == null || request.getComponentTypes().isEmpty()) {
            throw new InvalidParameterException("Inputs not valid");
        }
        if(templateRepository.existsTemplateEntityByNameAndBrandAndCategory(request.getName(), request.getBrandId(), request.getCategoryId())) {
            throw new InvalidParameterException("Template already exists");
        }
        for(ComponentTypeItemInTemplate item : request.getComponentTypes()){
            if(!componentTypeRepository.existsById(item.getComponentTypeId())){
                throw new InvalidParameterException("Component type not found");
            }
        }

        // Create and save template
        Template template = Template.builder()
                .brand(brand)
                .name(request.getName())
                .category(category)
                .imageUrl(request.getImageUrl())
                .build();

        TemplateEntity templateEntity = TemplateConverter.convertFromBaseToEntity(template);
        templateEntity = templateRepository.save(templateEntity);

        //save list of componentTypes in template
        for (ComponentTypeItemInTemplate item : request.getComponentTypes()) {
            //ComponentType componentType = componentTypeManager.getComponentTypeById(item.getComponentTypeId());

            ComponentTypeEntity componentTypeEntity = entityManager.find(ComponentTypeEntity.class, item.getComponentTypeId());
//            if(componentTypeEntity == null) {
//                throw new InvalidParameterException("Component type not found");
//            }
            ComponentTypeList_Template listItem = ComponentTypeList_Template.builder()
                    .template(templateEntity)
                    .componentType(componentTypeEntity)
                    .orderOfImportance(item.getOrderOfImportance())
                    .build();

            saveComponentTypeListTemplate(listItem);
        }
    }

    // Transactional method to save ComponentTypeList_Template
    @Transactional
    public void saveComponentTypeListTemplate(ComponentTypeList_Template listItem) {
        componentTypeListRepository.save(listItem);
    }


    @Override
    @Transactional
    public void deleteTemplate(long id) {
        if(templateRepository.existsById(id)){
            componentTypeListRepository.deleteByTemplateId(id);
            templateRepository.deleteById(id);
        }
        else throw new ObjectNotFound("Template not found");
    }
    @Override
    public Template getTemplateById(long id) {
        List<ComponentTypeList_Template> componentEntities = templateRepository.findComponentTypeListByTemplateId(id);
        if(componentEntities == null || componentEntities.isEmpty()) {
            throw new ObjectNotFound("This template does not exist");
        }
        return TemplateConverter.convertFromEntityToBase(templateRepository.findTemplateEntityById(id), componentEntities);
    }
    @Override
    public List<Template> getTemplatesByName(String name) {
        List<Template> templates = new ArrayList<>();
        List<TemplateEntity> templateEntities = templateRepository.findTemplateEntitiesByName(name);
        if(templateEntities == null || templateEntities.isEmpty()) {
            throw new ObjectNotFound("This template does not exist");
        }
        else{
            for(TemplateEntity templateEntity : templateEntities){
                List<ComponentTypeList_Template> componentEntities = templateRepository.findComponentTypeListByTemplateId(templateEntity.getId());
                templates.add(TemplateConverter.convertFromEntityToBase(templateEntity, componentEntities));
            }
            return templates;
        }

    }
    @Override
    public List<Template> getTemplates() {
        List<Template> templates = new ArrayList<>();
        List<TemplateEntity> templateEntities = templateRepository.findAll();
        for(TemplateEntity templateEntity : templateEntities){
            List<ComponentTypeList_Template> componentEntities = templateRepository.findComponentTypeListByTemplateId(templateEntity.getId());
            templates.add(TemplateConverter.convertFromEntityToBase(templateEntity, componentEntities));
        }
        return templates;
    }
}
