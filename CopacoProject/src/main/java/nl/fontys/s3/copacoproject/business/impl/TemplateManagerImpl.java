package nl.fontys.s3.copacoproject.business.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.BrandManager;
import nl.fontys.s3.copacoproject.business.CategoryManager;
import nl.fontys.s3.copacoproject.business.Exceptions.InvalidInputException;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectExistsAlreadyException;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.TemplateManager;
import nl.fontys.s3.copacoproject.business.converters.BrandConverter;
import nl.fontys.s3.copacoproject.business.converters.CategoryConverter;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
        if(!brandRepository.existsById(request.getBrandId())) {
            throw new InvalidInputException("This brand does not exist");
        }
        if(category == null || brand == null || request.getComponentTypes().isEmpty()) {
            throw new InvalidInputException("Inputs not valid");
        }
        if(templateRepository.existsTemplateEntityByNameAndBrandAndCategory(request.getName(), request.getBrandId(), request.getCategoryId())) {
            throw new InvalidInputException("Template already exists");
        }
        for(ComponentTypeItemInTemplate item : request.getComponentTypes()){
            if(!componentTypeRepository.existsById(item.getComponentTypeId())){
                throw new InvalidInputException("Component type not found");
            }
        }
        // Create and save template
        Template template = Template.builder()
                .brand(brand)
                .name(request.getName())
                .category(category)
                .configurationType(request.getConfigurationType())
                .imageUrl(request.getImageUrl())
                .build();

        TemplateEntity templateEntity = TemplateConverter.convertFromBaseToEntity(template);
        templateEntity = templateRepository.save(templateEntity);

        //save list of componentTypes in template
        saveComponentTypeListTemplate(request.getComponentTypes(), templateEntity);
    }

    // Transactional method to save ComponentTypeList_Template
    @Transactional
    public void saveComponentTypeListTemplate(List<ComponentTypeItemInTemplate> componentTypeList, TemplateEntity templateEntity) {
        for (ComponentTypeItemInTemplate item : componentTypeList) {
            ComponentTypeEntity componentTypeEntity = entityManager.find(ComponentTypeEntity.class, item.getComponentTypeId());

            // Check if the category matches
            if (!componentTypeEntity.getCategory().equals(templateEntity.getCategory())) {
                throw new InvalidInputException("Category does not match");
            }

            // Check if configurationType matches
            String templateConfigType = templateEntity.getConfigurationType();
            String componentConfigTypes = componentTypeEntity.getConfigurationType();

            // Use a Set for faster lookups
            if (templateConfigType != null && componentConfigTypes != null) {
                Set<String> componentConfigTypeSet = Arrays.stream(componentConfigTypes.split(","))
                        .map(String::trim) // Trim spaces for cleaner comparisons
                        .collect(Collectors.toSet());
                if (!componentConfigTypeSet.contains(templateConfigType)) {
                    throw new InvalidInputException("Configuration type does not match");
                }
            }

            // Create and save the ComponentTypeList_Template entity
            ComponentTypeList_Template listItem = ComponentTypeList_Template.builder()
                    .template(templateEntity)
                    .componentType(componentTypeEntity)
                    .orderOfImportance(item.getOrderOfImportance())
                    .build();

            componentTypeListRepository.save(listItem);
        }
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
    public List<Template> getFilteredTemplates(int itemsPerPage, int currentPage, long categoryId) {
        CategoryEntity categoryEntity = null;
        if (categoryId > 0) {
            if (!categoryRepository.existsById(categoryId)) {
                throw new ObjectNotFound("Category not found");
            }
            categoryEntity = categoryRepository.findCategoryEntityById(categoryId);
        }

        Pageable pageable = PageRequest.of(currentPage, itemsPerPage, Sort.by("id").descending());
        Page<TemplateEntity> templateEntitiesPage = templateRepository.findTemplateEntitiesByCategory(categoryEntity, pageable);

        if (templateEntitiesPage.isEmpty()) {
            throw new ObjectNotFound("There are no templates");
        }

        List<Template> templates = new ArrayList<>();
        for (TemplateEntity templateEntity : templateEntitiesPage) {
            List<ComponentTypeList_Template> componentEntities = templateRepository.findComponentTypeListByTemplateId(templateEntity.getId());
            templates.add(TemplateConverter.convertFromEntityToBase(templateEntity, componentEntities));
        }

        return templates;
    }

    @Override
    public int getNumberOfTemplates(Long categoryId) {
        CategoryEntity categoryEntity;
        if (categoryId > 0) {
            if (!categoryRepository.existsById(categoryId)) {
                throw new ObjectNotFound("Category not found");
            }
            categoryEntity = categoryRepository.findCategoryEntityById(categoryId);
        }
        else{
            throw new InvalidInputException("Invalid category id");
        }

        return templateRepository.countTemplateEntitiesByCategory(categoryEntity);
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
    @Transactional
    public void updateTemplate(long templateId, UpdateTemplateRequest request) {
        TemplateEntity templateEntity = templateRepository.findTemplateEntityById(templateId);
        if(templateEntity == null) {
            throw new InvalidInputException("Template not found");
        }
        if(!categoryRepository.existsById(request.getCategoryId()) || !brandRepository.existsById(request.getBrandId())) {
            throw new InvalidInputException("Inputs not valid");
        }
        Category category = categoryManager.findCategoryById(request.getCategoryId());
        Brand brand = brandManager.getBrandById(request.getBrandId());

        for(ComponentTypeItemInTemplate item : request.getComponentTypes()){
            if(!componentTypeRepository.existsById(item.getComponentTypeId())){
                throw new InvalidInputException("Component type not found");
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

    private void updateTemplateComponents(TemplateEntity templateEntity, List<ComponentTypeItemInTemplate> componentTypes) {
        deleteRemovedComponents(templateEntity, componentTypes);
        saveOrUpdateComponents(templateEntity, componentTypes);
    }

    private void deleteRemovedComponents(TemplateEntity templateEntity, List<ComponentTypeItemInTemplate> componentTypes) {
        List<ComponentTypeList_Template> existingComponentTypesInTemplate =
                componentTypeListRepository.findComponentTypeList_TemplatesByTemplate(templateEntity);

        Set<Long> updatedComponentTypeIds = componentTypes.stream()
                .map(ComponentTypeItemInTemplate::getComponentTypeId)
                .collect(Collectors.toSet());

        List<ComponentTypeList_Template> itemsToDelete = existingComponentTypesInTemplate.stream()
                .filter(existing -> !updatedComponentTypeIds.contains(existing.getComponentType().getId()))
                .toList();

        componentTypeListRepository.deleteAll(itemsToDelete);
    }

    private void saveOrUpdateComponents(TemplateEntity templateEntity, List<ComponentTypeItemInTemplate> componentTypes) {
        String templateConfigType = templateEntity.getConfigurationType();

        for (ComponentTypeItemInTemplate item : componentTypes) {
            ComponentTypeEntity componentTypeEntity = componentTypeRepository.findComponentTypeEntityById(item.getComponentTypeId());

            validateCategoryMatch(templateEntity, componentTypeEntity);
            validateConfigurationTypeMatch(templateConfigType, componentTypeEntity);

            ComponentTypeList_Template_CPK componentInTemplateId = new ComponentTypeList_Template_CPK(templateEntity, componentTypeEntity);
            saveOrUpdateComponentTypeListTemplate(componentInTemplateId, item, templateEntity, componentTypeEntity);
        }
    }

    private void validateCategoryMatch(TemplateEntity templateEntity, ComponentTypeEntity componentTypeEntity) {
        if (!componentTypeEntity.getCategory().equals(templateEntity.getCategory())) {
            throw new InvalidInputException("Category does not match");
        }
    }

    private void validateConfigurationTypeMatch(String templateConfigType, ComponentTypeEntity componentTypeEntity) {
        String componentConfigTypes = componentTypeEntity.getConfigurationType();
        if (templateConfigType != null && componentConfigTypes != null) {
            Set<String> componentConfigTypeSet = Arrays.stream(componentConfigTypes.split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());
            if (!componentConfigTypeSet.contains(templateConfigType)) {
                throw new InvalidInputException("Configuration type does not match");
            }
        }
    }

    private void saveOrUpdateComponentTypeListTemplate(ComponentTypeList_Template_CPK componentInTemplateId, ComponentTypeItemInTemplate item, TemplateEntity templateEntity, ComponentTypeEntity componentTypeEntity) {
        if (componentTypeListRepository.existsById(componentInTemplateId)) {
            ComponentTypeList_Template orderedItemInTemplate = componentTypeListRepository.findComponentTypeList_TemplateByTemplateAndComponentType(
                    componentInTemplateId.getTemplate(), componentInTemplateId.getComponentType());
            orderedItemInTemplate.setOrderOfImportance(item.getOrderOfImportance());
            componentTypeListRepository.save(orderedItemInTemplate);
        } else {
            ComponentTypeList_Template orderedItemInTemplate = ComponentTypeList_Template.builder()
                    .orderOfImportance(item.getOrderOfImportance())
                    .componentType(componentTypeEntity)
                    .template(templateEntity)
                    .build();
            componentTypeListRepository.save(orderedItemInTemplate);
        }
    }

}
