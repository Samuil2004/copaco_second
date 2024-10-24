package nl.fontys.s3.copacoproject.business.impl;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.CategoryManager;
import nl.fontys.s3.copacoproject.business.ComponentTypeManager;
import nl.fontys.s3.copacoproject.persistence.SpecificationTypeComponentTypeRepository;
import nl.fontys.s3.copacoproject.business.converters.ComponentTypeConverter;
import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.CreateComponentTypeRequest;
import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.CreateComponentTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.GetAllComponentTypeResponse;
import nl.fontys.s3.copacoproject.domain.ComponentType;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.SpecificationTypeRepository;
import nl.fontys.s3.copacoproject.persistence.entity.CategoryEntity;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeEntity;
import nl.fontys.s3.copacoproject.persistence.entity.SpecficationTypeList_ComponentType;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ComponentTypeManagerImpl implements ComponentTypeManager {
    private final ComponentTypeRepository componentTypeRepository;
    private final SpecificationTypeComponentTypeRepository specificationTypeCompTypeRepository;
    private final SpecificationTypeRepository specificationTypeRepository;
    private final CategoryManager categoryManager;
    @Override
    public GetAllComponentTypeResponse getAllComponentTypes() {
        List<ComponentTypeEntity> componentTypeEntities = componentTypeRepository.findAll();

        List<ComponentType> componentTypes = componentTypeEntities.stream().map(entity -> {
            List<SpecificationTypeEntity> specifications = specificationTypeCompTypeRepository
                    .findAllByComponentType(entity)
                    .stream()
                    .map(SpecficationTypeList_ComponentType::getSpecificationType)
                    .collect(toList());
            return ComponentTypeConverter.convertFromEntityToBase(entity, specifications);
        }).collect(toList());
        return GetAllComponentTypeResponse.builder()
                .componentTypes(componentTypes)
                .build();
    }
    @Override
    public CreateComponentTypeResponse createComponentType(CreateComponentTypeRequest request) {
        if (request.getComponentTypeName() == null || request.getComponentTypeImageUrl() == null) {
            throw new IllegalArgumentException("Component type name and image URL must not be null");
        }
        CategoryEntity category = categoryManager.findCategoryById(request.getCategory().getCategoryId());
        ComponentTypeEntity componentTypeEntity = ComponentTypeEntity.builder()
                .componentTypeName(request.getComponentTypeName())
                .componentTypeImageUrl(request.getComponentTypeImageUrl())
                .category(category)
                .build();
        componentTypeEntity = componentTypeRepository.save(componentTypeEntity);
        if (request.getSpecificationTypeIds() != null && !request.getSpecificationTypeIds().isEmpty()) {
            for (Long specTypeId : request.getSpecificationTypeIds()) {
                SpecificationTypeEntity specificationTypeEntity = specificationTypeRepository.findById(specTypeId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid specification type ID: " + specTypeId));

                SpecficationTypeList_ComponentType joinEntity = SpecficationTypeList_ComponentType.builder()
                        .componentType(componentTypeEntity)
                        .specificationType(specificationTypeEntity)
                        .build();
                specificationTypeCompTypeRepository.save(joinEntity);
            }
        }
        return CreateComponentTypeResponse.builder()
                .ComponentTypeId(componentTypeEntity.getId())
                .build();

    }
    @Override
    public Optional<ComponentType> getComponentTypeById(long id){
        ComponentTypeEntity component = componentTypeRepository.findById(id).orElse(null);
        if (component == null) {
            return Optional.empty();
        }
        List<SpecificationTypeEntity> specifications = specificationTypeCompTypeRepository
                .findAllByComponentType(component)
                .stream()
                .map(SpecficationTypeList_ComponentType::getSpecificationType)
                .collect(toList());

        return Optional.of(ComponentTypeConverter.convertFromEntityToBase(component, specifications));
    }

  /*  @Override
    public void updateComponentType(UpdateComponentTypeRequest request) {
    }*/
    @Override
    public void deleteComponentType(long id) {
        componentTypeRepository.deleteById(id);
    }
}
