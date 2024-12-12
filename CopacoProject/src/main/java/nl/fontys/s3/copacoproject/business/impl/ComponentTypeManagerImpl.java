package nl.fontys.s3.copacoproject.business.impl;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.ComponentTypeManager;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.GetDistinctComponentTypesByTypeOfConfigurationRequest;
import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.GetDistinctComponentTypesByTypeOfConfigurationResponse;
import nl.fontys.s3.copacoproject.persistence.CategoryRepository;
import nl.fontys.s3.copacoproject.business.converters.ComponentTypeConverter;
import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.GetAllComponentTypeResponse;
import nl.fontys.s3.copacoproject.domain.ComponentType;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.entity.CategoryEntity;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComponentTypeManagerImpl implements ComponentTypeManager {
    private final ComponentTypeRepository componentTypeRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public GetAllComponentTypeResponse getAllComponentTypes() {
        List<ComponentTypeEntity> componentTypeEntities = componentTypeRepository.findAll();
        List<ComponentType> componentTypeBaseClass = componentTypeEntities.stream().map(ComponentTypeConverter::convertFromEntityToBase).toList();
        return GetAllComponentTypeResponse.builder()
                .componentTypes(componentTypeBaseClass)
                .build();
    }
//    @Override
//    public CreateComponentTypeResponse createComponentType(CreateComponentTypeRequest request) {
//        if (request.getComponentTypeName() == null || request.getComponentTypeImageUrl() == null) {
//            throw new IllegalArgumentException("Component type name and image URL must not be null");
//        }
//        CategoryEntity category = categoryManager.findCategoryById(request.getCategory().getCategoryId());
//        ComponentTypeEntity componentTypeEntity = ComponentTypeEntity.builder()
//                .componentTypeName(request.getComponentTypeName())
//                .componentTypeImageUrl(request.getComponentTypeImageUrl())
//                .category(category)
//                .build();
//        componentTypeEntity = componentTypeRepository.save(componentTypeEntity);
//        if (request.getSpecificationTypeIds() != null && !request.getSpecificationTypeIds().isEmpty()) {
//            for (Long specTypeId : request.getSpecificationTypeIds()) {
//                SpecificationTypeEntity specificationTypeEntity = specificationTypeRepository.findById(specTypeId)
//                        .orElseThrow(() -> new IllegalArgumentException("Invalid specification type ID: " + specTypeId));
//
//                SpecficationTypeList_ComponentType joinEntity = SpecficationTypeList_ComponentType.builder()
//                        .componentType(componentTypeEntity)
//                        .specificationType(specificationTypeEntity)
//                        .build();
//                specificationTypeCompTypeRepository.save(joinEntity);
//            }
//        }
//        return CreateComponentTypeResponse.builder()
//                .ComponentTypeId(componentTypeEntity.getId())
//                .build();
//
//    }
    @Override
    public ComponentType getComponentTypeById(long id){
        Optional<ComponentTypeEntity> component = componentTypeRepository.findById(id);
        if (component.isEmpty()) {
            throw new ObjectNotFound("COMPONENT_TYPE_NOT_FOUND");
        }
        return ComponentTypeConverter.convertFromEntityToBase(component.get());
    }

    @Override
    public List<ComponentType> getComponentTypesByCategory(long categoryId) {
        if(!categoryRepository.existsById(categoryId)) {
            throw new ObjectNotFound("Category not found");
        }
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId).get();
        List<ComponentTypeEntity> componentTypeEntities = componentTypeRepository.findComponentTypeEntitiesByCategory(categoryEntity);

        List<ComponentType> componentTypes = new ArrayList<>();
        for(ComponentTypeEntity componentTypeEntity : componentTypeEntities) {
            componentTypes.add(ComponentTypeConverter.convertFromEntityToBase(componentTypeEntity));
        }

        return componentTypes;
    }

    @Override
    public GetDistinctComponentTypesByTypeOfConfigurationResponse findDistinctComponentTypesByTypeOfConfiguration(GetDistinctComponentTypesByTypeOfConfigurationRequest request) {
        List<ComponentTypeEntity> allDistinctComponentTypesFromConfigurationType = componentTypeRepository.findDistinctComponentTypesByTypeOfConfiguration(request.getTypeOfConfiguration());
        if(allDistinctComponentTypesFromConfigurationType.isEmpty()) {
            throw new ObjectNotFound("DISTINCT_COMPONENT_TYPE_NOT_FOUND");
        }
        Map<Long, String> distinctComponentTypes = allDistinctComponentTypesFromConfigurationType.stream()
                .collect(Collectors.toMap(
                        ComponentTypeEntity::getId,
                        ComponentTypeEntity::getComponentTypeName
                        ));

        return GetDistinctComponentTypesByTypeOfConfigurationResponse.builder().distinctComponentTypesFromTypeOfConfiguration(distinctComponentTypes).build();
    }

}
