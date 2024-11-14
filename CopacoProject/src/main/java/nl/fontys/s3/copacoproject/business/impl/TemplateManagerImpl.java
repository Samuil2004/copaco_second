package nl.fontys.s3.copacoproject.business.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.CategoryManager;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectExistsAlreadyException;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.TemplateManager;
import nl.fontys.s3.copacoproject.business.converters.BrandConverter;
import nl.fontys.s3.copacoproject.business.converters.CategoryConverter;
import nl.fontys.s3.copacoproject.business.converters.ComponentTypeConverter;
import nl.fontys.s3.copacoproject.business.converters.TemplateConverter;
import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.ComponentTypeItemInTemplate;
import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.CreateTemplateRequest;
import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.UpdateTemplateRequest;
import nl.fontys.s3.copacoproject.domain.Brand;
import nl.fontys.s3.copacoproject.domain.Category;
import nl.fontys.s3.copacoproject.domain.Template;
import nl.fontys.s3.copacoproject.persistence.*;
import nl.fontys.s3.copacoproject.persistence.entity.*;
import nl.fontys.s3.copacoproject.persistence.entity.primaryKeys.ComponentTypeList_Template_CPK;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateManagerImpl implements TemplateManager {
    private final TemplateRepository templateRepository;
    private final ComponentTypeList_TemplateRepository componentTypeListRepository;
    private final ComponentTypeRepository componentTypeRepository;
    private final CategoryManager categoryManager;
    private final BrandManager brandManager;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

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
        if(!templateRepository.existsById(id)) {
            throw new ObjectNotFound("This template does not exist");
        }
        List<ComponentTypeList_Template> componentEntities = templateRepository.findComponentTypeListByTemplateId(id);
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

    @Override
    public void updateTemplate(long templateId, UpdateTemplateRequest request) {
        TemplateEntity templateEntity = templateRepository.findTemplateEntityById(templateId);
        if(templateEntity == null) {
            throw new ObjectNotFound("Template not found");
        }
        if(!categoryRepository.existsById(request.getCategoryId()) || !brandRepository.existsById(request.getBrandId())) {
            throw new InvalidParameterException("Inputs not valid");
        }
        Category category = categoryManager.findCategoryById(request.getCategoryId());
        Brand brand = brandManager.getBrandById(request.getBrandId());

        for(ComponentTypeItemInTemplate item : request.getComponentTypes()){
            if(!componentTypeRepository.existsById(item.getComponentTypeId())){
                throw new InvalidParameterException("Component type not found");
            }
        }
        if(templateRepository.existsTemplateEntityForUpdate(templateId ,request.getName(), request.getBrandId(), request.getCategoryId())) {
            throw new ObjectExistsAlreadyException("Template already exists");
        }

        updateTemplateData(templateEntity, request.getName(), BrandConverter.convertFromBaseToEntity(brand), CategoryConverter.convertFromBaseToEntity(category), request.getImageUrl());
        updateTemplateComponents(templateEntity, request.getComponentTypes());
    }

    private void updateTemplateData(TemplateEntity templateEntity, String newName, BrandEntity brand, CategoryEntity category, String newImage) {
        templateEntity.setName(newName);
        templateEntity.setBrand(brand);
        templateEntity.setCategory(category);
        templateEntity.setImageURL(newImage);
        templateRepository.save(templateEntity);
    }

    private void updateTemplateComponents( TemplateEntity templateEntity, List<ComponentTypeItemInTemplate> componentTypes) {
        //delete removed items from template
        List<ComponentTypeList_Template> existingComponentTypesInTemplate =
                componentTypeListRepository.findComponentTypeList_TemplatesByTemplate(templateEntity);

        Set<Long> updatedComponentTypeIds = componentTypes.stream()
                .map(ComponentTypeItemInTemplate::getComponentTypeId)
                .collect(Collectors.toSet());

        List<ComponentTypeList_Template> itemsToDelete = existingComponentTypesInTemplate.stream()
                .filter(existing -> !updatedComponentTypeIds.contains(existing.getComponentType().getId()))
                .toList();

        componentTypeListRepository.deleteAll(itemsToDelete);


        //save updates in order of importance / add new component types in template
        for(ComponentTypeItemInTemplate item : componentTypes) {
            ComponentTypeEntity componentTypeEntity = componentTypeRepository.findComponentTypeEntityById(item.getComponentTypeId());
            ComponentTypeList_Template_CPK componentInTemplateId = new ComponentTypeList_Template_CPK(templateEntity, componentTypeEntity);

            ComponentTypeList_Template orderedItemInTemplate = new ComponentTypeList_Template();
            if(componentTypeListRepository.existsById(componentInTemplateId)){
                orderedItemInTemplate = componentTypeListRepository.findComponentTypeList_TemplateByTemplateAndComponentType(componentInTemplateId.getTemplate(), componentInTemplateId.getComponentType());
            }
            else{
                orderedItemInTemplate = ComponentTypeList_Template.builder()
                        .orderOfImportance(item.getOrderOfImportance())
                        .componentType(componentTypeEntity)
                        .template(templateEntity)
                        .build();
                componentTypeListRepository.save(orderedItemInTemplate);
            }
        }
    }
}
