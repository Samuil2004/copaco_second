package nl.fontys.s3.copacoproject.business.impl;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.ComponentManager;
import nl.fontys.s3.copacoproject.business.exception.InvalidInputException;
import nl.fontys.s3.copacoproject.business.exception.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.SpecificationIdsForComponentPurpose;
import nl.fontys.s3.copacoproject.business.converters.ComponentConverter;
import nl.fontys.s3.copacoproject.business.dto.GetComponentResponse;
import nl.fontys.s3.copacoproject.business.dto.component.SimpleComponentResponse;
import nl.fontys.s3.copacoproject.domain.Component;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import nl.fontys.s3.copacoproject.persistence.*;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeEntity;
import nl.fontys.s3.copacoproject.persistence.entity.Component_SpecificationList;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
public class ComponentManagerImpl implements ComponentManager {

    private final ComponentRepository componentRepository;
    private final ComponentSpecificationListRepository  componentSpecificationListRepository;
    private final CategoryRepository categoryRepository;
    private final ComponentTypeRepository componentTypeRepository;
    private final SpecificationIdsForComponentPurpose specificationIdsForComponentPurpose;

    @Override
    public List<GetComponentResponse> getAllComponents() {
        List<ComponentEntity> allComponentsEntities = componentRepository.findAll();
        List<GetComponentResponse> allComponentsResponse = new ArrayList<>();
        for (ComponentEntity componentEntity : allComponentsEntities) {
            List<Component_SpecificationList> allSpecificationsForComponent = componentSpecificationListRepository.findByComponentId(componentEntity);
            Map<SpecificationTypeEntity, List<String>> dictionaryWithTheSpecificationAndAllValuesForComponent = new HashMap<>();

            for(Component_SpecificationList specificationList : allSpecificationsForComponent){
                SpecificationTypeEntity specType = specificationList.getSpecificationType();
                String value = specificationList.getValue();

                List<String> valuesList = dictionaryWithTheSpecificationAndAllValuesForComponent.get(specType);

                if (valuesList == null) {
                    valuesList = new ArrayList<>();
                    valuesList.add(value);
                    dictionaryWithTheSpecificationAndAllValuesForComponent.put(specType, valuesList);
                } else {
                    valuesList.add(value);
                }
            }
            Component componentBase = ComponentConverter.convertFromEntityToBase(componentEntity,dictionaryWithTheSpecificationAndAllValuesForComponent);


            Map<String, List<String>> specificationsForResponse = new HashMap<>();
            for (Map.Entry<SpecificationType, List<String>> entry : componentBase.getSpecifications().entrySet()) {
                SpecificationType specType = entry.getKey();
                List<String> values = entry.getValue();
                String specTypeName = specType.getSpecificationTypeName();
                specificationsForResponse.put(specTypeName, values);
            }

            GetComponentResponse getComponentResponse = GetComponentResponse.builder()
                    .componentId(componentEntity.getComponentId())
                    .componentName(componentEntity.getComponentName())
                    .componentTypeId(componentEntity.getComponentType().getId())
                    .componentTypeName(componentEntity.getComponentType().getComponentTypeName())
                    .categoryName(componentEntity.getComponentType().getCategory().getCategoryName())
                    .componentImageUrl(componentEntity.getComponentImageUrl())
                    .componentPrice(componentEntity.getComponentPrice())
                    .specifications(specificationsForResponse)
                    .build();
            allComponentsResponse.add(getComponentResponse);
        }

        return allComponentsResponse;
    }

    @Override
    public List<Component> getAllComponentsByCategory(long categoryId) {
        if(!categoryRepository.existsById(categoryId)){
            throw new ObjectNotFound("This category does not exist");
        }
        List<ComponentEntity> allComponentsEntities = componentRepository.findComponentEntitiesByCategory(categoryId);
        List<Component> allComponentsBase = new ArrayList<>();
        for (ComponentEntity componentEntity : allComponentsEntities) {
            List<Component_SpecificationList> allSpecificationsForComponent = componentSpecificationListRepository.findByComponentId(componentEntity);
            Map<SpecificationTypeEntity, List<String>> dictionaryWithTheSpecificationAndAllValuesForComponent = new HashMap<>();

            for(Component_SpecificationList specificationList : allSpecificationsForComponent){
                SpecificationTypeEntity specType = specificationList.getSpecificationType();
                String value = specificationList.getValue();

                List<String> valuesList = dictionaryWithTheSpecificationAndAllValuesForComponent.get(specType);

                if (valuesList == null) {
                    valuesList = new ArrayList<>();
                    valuesList.add(value);
                    dictionaryWithTheSpecificationAndAllValuesForComponent.put(specType, valuesList);
                } else {
                    valuesList.add(value);
                }
            }
            Component componentBase = ComponentConverter.convertFromEntityToBase(componentEntity,dictionaryWithTheSpecificationAndAllValuesForComponent);
            allComponentsBase.add(componentBase);
        }
        return allComponentsBase;
    }

    @Override
    public List<Component> getAllComponentFromComponentType(Long componentTypeId) {

        Optional<ComponentTypeEntity> foundComponentType = componentTypeRepository.findById(componentTypeId);

        if(foundComponentType.isEmpty()){
            throw new ObjectNotFound("Component type does not exist");
        }

        List<ComponentEntity> allComponentsEntities = componentRepository.findByComponentType_Id(componentTypeId);
        List<Component> allComponentsBase = new ArrayList<>();
        for (ComponentEntity componentEntity : allComponentsEntities) {
            List<Component_SpecificationList> allSpecificationsForComponent = componentSpecificationListRepository.findByComponentId(componentEntity);
            Map<SpecificationTypeEntity, List<String>> dictionaryWithTheSpecificationAndAllValuesForComponent = new HashMap<>();

            for(Component_SpecificationList specificationList : allSpecificationsForComponent){
                SpecificationTypeEntity specType = specificationList.getSpecificationType();
                String value = specificationList.getValue();

                List<String> valuesList = dictionaryWithTheSpecificationAndAllValuesForComponent.get(specType);

                if (valuesList == null) {
                    valuesList = new ArrayList<>();
                    valuesList.add(value);
                    dictionaryWithTheSpecificationAndAllValuesForComponent.put(specType, valuesList);
                } else {
                    valuesList.add(value);
                }
            }
            Component componentBase = ComponentConverter.convertFromEntityToBase(componentEntity,dictionaryWithTheSpecificationAndAllValuesForComponent);
            allComponentsBase.add(componentBase);


        }
        return allComponentsBase;
    }

    @Override
    public List<SimpleComponentResponse> getComponentsByComponentTypeAndConfigurationType(Long componentTypeId, String configurationType, int currentPage) {
        if(!componentTypeRepository.existsById(componentTypeId)){
            throw new InvalidInputException("Component type does not exist");
        }

        Pageable pageable = PageRequest.of(currentPage-1, 10, Sort.by("c.componentName").ascending());
        Map<Long,List<String>> idForSpecificationsAndValues = specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(configurationType,componentTypeId);
        Page<ComponentEntity> entities;
        if(idForSpecificationsAndValues.isEmpty())
        {
            entities = componentRepository.findComponentEntityByComponentType(componentTypeId,pageable);
        }
        else {
            Map.Entry<Long, List<String>> firstEntry = idForSpecificationsAndValues.entrySet().iterator().next();
            entities = componentRepository.findComponentEntityByComponentTypeAndConfigurationType(componentTypeId, firstEntry.getValue(), firstEntry.getKey(), pageable);
        }
        List<SimpleComponentResponse> components = new ArrayList<>();
        for (ComponentEntity componentEntity : entities.getContent()) {
            components.add(SimpleComponentResponse.builder()
                    .componentId(componentEntity.getComponentId())
                    .componentImageUrl(componentEntity.getComponentImageUrl())
                    .componentName(componentEntity.getComponentName())
                    .componentPrice(componentEntity.getComponentPrice())
                    .build());
        }
        return components;
    }

    @Override
    public Integer getComponentCountByComponentTypeAndConfigurationType(Long componentTypeId, String configurationTypeId) {
        if(!componentTypeRepository.existsById(componentTypeId)){
            throw new InvalidInputException("Component type does not exist");
        }
        List<Long> allDistinctSpecificationIdsThatHoldConfigurationType =  specificationIdsForComponentPurpose.getAllDistinctSpecificationIdsThatHoldConfigurationType();

        return componentRepository.countByComponentTypeAndConfigurationType(componentTypeId, configurationTypeId,allDistinctSpecificationIdsThatHoldConfigurationType);
    }


}
