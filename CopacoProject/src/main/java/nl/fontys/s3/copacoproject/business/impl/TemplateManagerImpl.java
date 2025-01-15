package nl.fontys.s3.copacoproject.business.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.CategoryManager;
import nl.fontys.s3.copacoproject.business.exception.InvalidInputException;
import nl.fontys.s3.copacoproject.business.exception.ObjectExistsAlreadyException;
import nl.fontys.s3.copacoproject.business.exception.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.TemplateManager;
import nl.fontys.s3.copacoproject.business.converters.CategoryConverter;
import nl.fontys.s3.copacoproject.business.converters.TemplateConverter;
import nl.fontys.s3.copacoproject.business.dto.template_dto.CreateTemplateRequest;
import nl.fontys.s3.copacoproject.business.dto.template_dto.TemplateObjectResponse;
import nl.fontys.s3.copacoproject.business.dto.template_dto.UpdateTemplateRequest;
import nl.fontys.s3.copacoproject.domain.Category;
import nl.fontys.s3.copacoproject.domain.Template;
import nl.fontys.s3.copacoproject.persistence.*;
import nl.fontys.s3.copacoproject.persistence.entity.*;
import nl.fontys.s3.copacoproject.persistence.entity.primaryKeys.ComponentTypeList_Template_CPK;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateManagerImpl implements TemplateManager {
    private final TemplateRepository templateRepository;
    private final ComponentTypeList_TemplateRepository componentTypeListRepository;
    private final ComponentTypeRepository componentTypeRepository;
    private final CategoryManager categoryManager;
    private final CategoryRepository categoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void createTemplate(CreateTemplateRequest request, MultipartFile file) throws IOException {
        // Retrieve related entities
        Category category = categoryManager.findCategoryById(request.getCategoryId());
        if(category == null || request.getComponentTypes().isEmpty()) {
            throw new InvalidInputException("Inputs not valid");
        }
        if(templateRepository.existsTemplateEntityByNameAndCategory(request.getName(), request.getCategoryId())) {
            throw new InvalidInputException("Template already exists");
        }
        for(Long itemId : request.getComponentTypes()){
            if(!componentTypeRepository.existsById(itemId)){
                throw new InvalidInputException("Component type not found");
            }
        }
        //if image is uploaded set it
        byte[] imageInByte= convertImageFromFileToByte(file);

        // Create and save template
        Template template = Template.builder()
                .name(request.getName())
                .category(category)
                .configurationType(request.getConfigurationType())
                .image(imageInByte)
                .active(true)
                .build();

        TemplateEntity templateEntity = TemplateConverter.convertFromBaseToEntity(template);
        templateEntity = templateRepository.save(templateEntity);

        //save list of componentTypes in template
        saveComponentTypeListTemplate(request.getComponentTypes(), templateEntity);
    }

    private byte[] convertImageFromFileToByte (MultipartFile file) throws IOException {
        byte[] imageInByte;
        if(file != null){
            String fileType = file.getContentType();
            if (!MediaType.IMAGE_JPEG_VALUE.equals(fileType) && !MediaType.IMAGE_PNG_VALUE.equals(fileType)) {
                throw new IllegalArgumentException("Only PNG and JPG files are allowed.");
            }
            else{
                imageInByte = file.getBytes();
            }
        }
        else{
            imageInByte = null;
        }

        return imageInByte;
    }

    // Transactional method to save ComponentTypeList_Template
    @Transactional
    public void saveComponentTypeListTemplate(List<Long> componentTypeList, TemplateEntity templateEntity) {
        for (Long itemId : componentTypeList) {
            ComponentTypeEntity componentTypeEntity = entityManager.find(ComponentTypeEntity.class, itemId);

            // Check if the category matches
            if (!componentTypeEntity.getCategory().equals(templateEntity.getCategory())) {
                throw new InvalidInputException("Category does not match");
            }

            // Check if configurationType matches
            String templateConfigType = templateEntity.getConfigurationType();
            validateConfigurationTypeMatch(templateConfigType, componentTypeEntity);

            // Create and save the ComponentTypeList_Template entity
            ComponentTypeList_Template listItem = ComponentTypeList_Template.builder()
                    .template(templateEntity)
                    .componentType(componentTypeEntity)
                    .build();

            componentTypeListRepository.save(listItem);
        }
    }

    @Override
    public TemplateObjectResponse getTemplateById(long id) {
        if(!templateRepository.existsById(id)) {
            throw new ObjectNotFound("This template does not exist");
        }
        List<ComponentTypeList_Template> componentEntities = templateRepository.findComponentTypeListByTemplateId(id);
        List<String> componentTypes = new ArrayList<>();
        for(ComponentTypeList_Template componentEntity : componentEntities){
            componentTypes.add(componentEntity.getComponentType().getComponentTypeName());
        }
        return TemplateConverter.convertFromEntityToResponse(templateRepository.findTemplateEntityById(id), componentTypes);
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
    public List<TemplateObjectResponse> getFilteredTemplates(int itemsPerPage, int currentPage, Long categoryId, String configurationType) {
        CategoryEntity categoryEntity = null;
        if (categoryId != null && categoryId > 0) {
            if (!categoryRepository.existsById(categoryId)) {

                throw new ObjectNotFound("Category not found");
            }

            categoryEntity = categoryRepository.findCategoryEntityById(categoryId);
        }

        Pageable pageable = PageRequest.of(currentPage-1, itemsPerPage, Sort.by("id").descending());
        Page<TemplateEntity> templateEntitiesPage = templateRepository.findTemplateEntitiesByCategoryAndConfigurationType(categoryEntity, configurationType, pageable);

        if (templateEntitiesPage.isEmpty()) {
            throw new ObjectNotFound("There are no templates");
        }

        List<TemplateObjectResponse> templates = new ArrayList<>();
        for (TemplateEntity templateEntity : templateEntitiesPage) {
            List<ComponentTypeList_Template> componentEntities = templateRepository.findComponentTypeListByTemplateId(templateEntity.getId());
            List<String> componentTypes = new ArrayList<>();
            for (ComponentTypeList_Template componentTypeList_Template : componentEntities) {
                componentTypes.add(componentTypeList_Template.getComponentType().getComponentTypeName());
            }
            templates.add(TemplateConverter.convertFromEntityToResponse(templateEntity, componentTypes));
        }

        return templates;
    }

    @Override
    public List<TemplateObjectResponse> getActiveFilteredTemplates(int itemsPerPage, int currentPage, Long categoryId, String configurationType) {
        CategoryEntity categoryEntity = null;
        if (categoryId != null && categoryId > 0) {
            if (!categoryRepository.existsById(categoryId)) {

                throw new ObjectNotFound("Category not found");
            }

            categoryEntity = categoryRepository.findCategoryEntityById(categoryId);
        }

        Pageable pageable = PageRequest.of(currentPage-1, itemsPerPage, Sort.by("id").descending());
        Page<TemplateEntity> templateEntitiesPage = templateRepository.findActiveTemplateEntitiesByCategoryAndConfigurationType(categoryEntity, configurationType, pageable);

        if (templateEntitiesPage.isEmpty()) {
            throw new ObjectNotFound("There are no templates");
        }

        List<TemplateObjectResponse> templates = new ArrayList<>();
        for (TemplateEntity templateEntity : templateEntitiesPage) {
            List<ComponentTypeList_Template> componentEntities = templateRepository.findComponentTypeListByTemplateId(templateEntity.getId());
            List<String> componentTypes = new ArrayList<>();
            for (ComponentTypeList_Template componentTypeList_Template : componentEntities) {
                componentTypes.add(componentTypeList_Template.getComponentType().getComponentTypeName());
            }
            templates.add(TemplateConverter.convertFromEntityToResponse(templateEntity, componentTypes));
        }

        return templates;
    }

    @Override
    public int getNumberOfTemplates(Long categoryId, String configurationType) {
        CategoryEntity categoryEntity = null;
        if (categoryId!=null && categoryId > 0) {
            if (!categoryRepository.existsById(categoryId)) {
                throw new ObjectNotFound("Category not found");
            }
            categoryEntity = categoryRepository.findCategoryEntityById(categoryId);
        }

        return templateRepository.countTemplateEntitiesByCategoryAndConfigurationType(categoryEntity, configurationType);
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
    public void updateTemplate(long templateId, UpdateTemplateRequest request, MultipartFile file) throws IOException {
        TemplateEntity templateEntity = templateRepository.findTemplateEntityById(templateId);
        if(templateEntity == null) {
            throw new InvalidInputException("Template not found");
        }
        if(!categoryRepository.existsById(request.getCategoryId())) {
            throw new InvalidInputException("Inputs not valid");
        }
        Category category = categoryManager.findCategoryById(request.getCategoryId());

        for(Long itemId : request.getComponentTypes()){
            if(!componentTypeRepository.existsById(itemId)){
                throw new InvalidInputException("Component type not found");
            }
        }
        if(templateRepository.existsTemplateEntityForUpdate(templateId ,request.getName(), request.getCategoryId())) {
            throw new ObjectExistsAlreadyException("Template already exists");
        }

        byte[] imageInBytes = convertImageFromFileToByte(file);

        updateTemplateData(templateEntity, request.getName(), CategoryConverter.convertFromBaseToEntity(category), imageInBytes);
        updateTemplateComponents(templateEntity, request.getComponentTypes());
    }

    @Override
    public void updateTemplateStatus(Long id, boolean active) {
        TemplateEntity templateEntity = templateRepository.findById(id).orElse(null);
        if(templateEntity == null) {
            throw new ObjectNotFound("Template not found");
        }

        templateEntity.setActive(active);
        templateRepository.save(templateEntity);
    }

    private void updateTemplateData(TemplateEntity templateEntity, String newName, CategoryEntity category, byte[] newImage) {
        templateEntity.setName(newName);
        templateEntity.setCategory(category);
        templateEntity.setImage(newImage);
        templateRepository.save(templateEntity);
    }

    private void updateTemplateComponents(TemplateEntity templateEntity, List<Long> componentTypes) {
        deleteRemovedComponents(templateEntity, componentTypes);
        saveOrUpdateComponents(templateEntity, componentTypes);
    }

    private void deleteRemovedComponents(TemplateEntity templateEntity, List<Long> updatedComponentTypeIds) {
        List<ComponentTypeList_Template> existingComponentTypesInTemplate =
                componentTypeListRepository.findComponentTypeList_TemplatesByTemplate(templateEntity);

        List<ComponentTypeList_Template> itemsToDelete = existingComponentTypesInTemplate.stream()
                .filter(existing -> !updatedComponentTypeIds.contains(existing.getComponentType().getId()))
                .toList();

        componentTypeListRepository.deleteAll(itemsToDelete);
    }

    private void saveOrUpdateComponents(TemplateEntity templateEntity, List<Long> componentTypes) {
        String templateConfigType = templateEntity.getConfigurationType();

        for (Long itemId : componentTypes) {
            ComponentTypeEntity componentTypeEntity = componentTypeRepository.findComponentTypeEntityById(itemId);

            validateCategoryMatch(templateEntity, componentTypeEntity);
            validateConfigurationTypeMatch(templateConfigType, componentTypeEntity);

            ComponentTypeList_Template_CPK componentInTemplateId = new ComponentTypeList_Template_CPK(templateEntity, componentTypeEntity);
            saveOrUpdateComponentTypeListTemplate(componentInTemplateId, templateEntity, componentTypeEntity);
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
            if (!componentConfigTypeSet.contains(templateConfigType.toUpperCase())) {
                throw new InvalidInputException("Configuration type does not match");
            }
        }
    }

    private void saveOrUpdateComponentTypeListTemplate(ComponentTypeList_Template_CPK componentInTemplateId, TemplateEntity templateEntity, ComponentTypeEntity componentTypeEntity) {
        if (componentTypeListRepository.existsById(componentInTemplateId)) {
            ComponentTypeList_Template itemInTemplate = componentTypeListRepository.findComponentTypeList_TemplateByTemplateAndComponentType(
                    componentInTemplateId.getTemplate(), componentInTemplateId.getComponentType());
            componentTypeListRepository.save(itemInTemplate);
        } else {
            ComponentTypeList_Template orderedItemInTemplate = ComponentTypeList_Template.builder()
                    .componentType(componentTypeEntity)
                    .template(templateEntity)
                    .build();
            componentTypeListRepository.save(orderedItemInTemplate);
        }
    }

}
