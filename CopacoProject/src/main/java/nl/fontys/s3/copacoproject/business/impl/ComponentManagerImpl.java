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
import nl.fontys.s3.copacoproject.persistence.entity.*;
import nl.fontys.s3.copacoproject.persistence.entity.supportingEntities.SpecificationTypeAndValuesForIt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ComponentManagerImpl implements ComponentManager {

    private final ComponentRepository componentRepository;
    private final ComponentSpecificationListRepository  componentSpecificationListRepository;
    private final CategoryRepository categoryRepository;
    private final ComponentTypeRepository componentTypeRepository;
    private final SpecificationIdsForComponentPurpose specificationIdsForComponentPurpose;
    private final CompatibilityRepository compatibilityRepository;

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
    public List<SimpleComponentResponse> getComponentsForFirstComponentTypeConfigurator(Long firstSelectedComponentTypeId, String configurationType, int currentPage,List<Long> componentTypesInTheTemplate) {
        if(!componentTypeRepository.existsById(firstSelectedComponentTypeId)){
            throw new InvalidInputException("Component type does not exist");
        }
        if (componentTypesInTheTemplate != null && componentTypesInTheTemplate.contains(firstSelectedComponentTypeId)) {
            componentTypesInTheTemplate.remove(firstSelectedComponentTypeId);
        }
        List<ComponentEntity> foundComponentsThatSatisfyAllFilters;
        Pageable pageable = PageRequest.of(currentPage-1, 10);
        Map<Long,List<String>> specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues = new HashMap<>();
        for(Long componentTypeId : componentTypesInTheTemplate) {
            List<Long> allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes = compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(firstSelectedComponentTypeId, componentTypeId, configurationType);
            if(allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes.isEmpty()){
                if(componentTypesInTheTemplate.indexOf(componentTypeId) == componentTypesInTheTemplate.size() - 1)
                {
                    if(specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues.isEmpty()){
                        return getComponentsByComponentTypeAndConfigurationType(firstSelectedComponentTypeId,configurationType,currentPage);
                    }
                    break;
                }
                continue;
            }
            Map<Long,List<String>> updatedIdsAndValues = getSpecificationsToBeConsideredForSearchedComponentType(allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes,firstSelectedComponentTypeId,componentTypeId,specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues,configurationType);
            specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues = updatedIdsAndValues;
            //process the rules
        }
        Map<Long,List<String>> componentPurposeAndSpecificationId = specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(configurationType,firstSelectedComponentTypeId);
        if(!componentPurposeAndSpecificationId.isEmpty()) {
            Map.Entry<Long, List<String>> firstEntry = componentPurposeAndSpecificationId.entrySet().iterator().next();
            specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues.put(firstEntry.getKey(), firstEntry.getValue());
        }
        Specification<ComponentEntity> spec = ComponentRepository.dynamicSpecification(
                firstSelectedComponentTypeId, specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues);

        //Using the dynamic query, get the first ten components that satisfy the filters in the query
        Page<ComponentEntity> page = componentRepository.findAll(spec, pageable);
        foundComponentsThatSatisfyAllFilters = page.getContent();
        List<SimpleComponentResponse> components = new ArrayList<>();
        for (ComponentEntity componentEntity : foundComponentsThatSatisfyAllFilters) {
            components.add(SimpleComponentResponse.builder()
                    .componentId(componentEntity.getComponentId())
                    .componentImageUrl(componentEntity.getComponentImageUrl())
                    .componentName(componentEntity.getComponentName())
                    .componentPrice(componentEntity.getComponentPrice())
                    .build());
        }
        return components;
    }

    private Map<Long,List<String>> getSpecificationsToBeConsideredForSearchedComponentType(List<Long> allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes,Long providedComponentComponentTypeId,Long searchedComponentTypeId, Map<Long,List<String>> specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues,String typeOfConfiguration)
    {
        for(Long specificationId : allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes) {
      List<Object[]> response = compatibilityRepository.findSpecification1IdsAndValuesOfFirstSpecification1ForFirstComponentType(providedComponentComponentTypeId,searchedComponentTypeId,specificationId,typeOfConfiguration);

            List<SpecificationTypeAndValuesForIt> specificationTypeIdsAndValuesToBeConsideredForTheSearchedComponentType = response.stream()
                    .map(result -> new SpecificationTypeAndValuesForIt(
                            ((Number) result[0]).longValue(),  // Cast to appropriate type
                            (String) result[1]
                    ))
                    .collect(Collectors.toList());



            //If the list is empty, this means that eventhough the component has the specification, the values it has for this specification is not compatible with any other specification types, which means not compatible
            if(specificationTypeIdsAndValuesToBeConsideredForTheSearchedComponentType.isEmpty()){
                throw new ObjectNotFound("One of the selected components does not respect any of the rules between it and the searched one;");
            }
            for (SpecificationTypeAndValuesForIt specificationTypeAndValuesForIt : specificationTypeIdsAndValuesToBeConsideredForTheSearchedComponentType) {
                Long specificationIdToBeConsideredForSecondSpecification = specificationTypeAndValuesForIt.getSpecification2Id();
                String valuesToBeConsideredForTheSearchedComponent = specificationTypeAndValuesForIt.getValueOfSecondSpecification();

                List<String> newValuesToConsider;
                if (valuesToBeConsideredForTheSearchedComponent == null) {
                    // Automatic Compatibility
                    Long specificationTypeIdOfTheSecondComponentType = compatibilityRepository.getSecondComponentSpecificationId(providedComponentComponentTypeId,searchedComponentTypeId,specificationId,typeOfConfiguration);
                    List<String> valuesTheSecondComponentHasForThisSpecification = compatibilityRepository.getDistinctValuesForASpecification(specificationTypeIdOfTheSecondComponentType,searchedComponentTypeId);
                    if(valuesTheSecondComponentHasForThisSpecification.isEmpty()){
                        throw new ObjectNotFound("One of the selected components does not respect any of the rules between it and the searched one;");
                    }
                    newValuesToConsider = valuesTheSecondComponentHasForThisSpecification;
                } else {
                    // Manual Compatibility
                    newValuesToConsider = Arrays.asList(valuesToBeConsideredForTheSearchedComponent.split("\\s*,\\s*"));
                }
                //This code does the following -> if we have component from component type 1 that has a rule for compatibility with component 4 (1,5,"2300","1,2,3,4") and this is already in the map
                //and now we have another component from component type 2 that has a rule for compatibility with component type 4 (7,5,"2300","4,5,6") and we are looking for component from component
                //type 4, for specification 5 we should consider only value "4" because all other values will make either the first or the second component incompatible with the searched one
                if (specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues.containsKey(specificationIdToBeConsideredForSecondSpecification)) {

                    List<String> existingValues = specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues.get(specificationIdToBeConsideredForSecondSpecification);

                    // Find common values between existing and new values
                    List<String> commonValues = existingValues.stream()
                            .filter(newValuesToConsider::contains)
                            .collect(Collectors.toList());

                    // Update the map with only the common values
                    specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues.put(specificationIdToBeConsideredForSecondSpecification, commonValues);
                } else {
                    // Insert new values into the map
                    specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues.put(specificationIdToBeConsideredForSecondSpecification, newValuesToConsider);
                }
            }
        }
        return specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues;
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
