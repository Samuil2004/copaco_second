package nl.fontys.s3.copacoproject.business.impl;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.CompatibilityBetweenComponents;
import nl.fontys.s3.copacoproject.business.CompatibilityManager;
import nl.fontys.s3.copacoproject.business.Exceptions.CompatibilityError;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.converters.ComponentConverter;
import nl.fontys.s3.copacoproject.business.dto.GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest;
import nl.fontys.s3.copacoproject.domain.CompatibilityResult;
import nl.fontys.s3.copacoproject.domain.Component;
import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentSpecificationListRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.entity.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CompatibilityBetweenComponentsImpl implements CompatibilityBetweenComponents {
    private final AutomaticCompatibilityRepository automaticCompatibilityRepository;
    private final ComponentRepository componentRepository;
    private final ComponentTypeRepository componentTypeRepository;
    private final ComponentSpecificationListRepository componentSpecificationListRepository;


    private List<Long> checkAndMapInputtedIds (GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest request)
    {
        List<Long> notNullIds = Stream.of(request.getFirstComponentId(),
                        request.getSecondComponentId(),
                        request.getThirdComponentId(),
                        request.getFourthComponentId(),
                        request.getFifthComponentId(),
                        request.getSixthComponentId(),
                        request.getSeventhComponentId())
                .filter(Objects::nonNull)
                .toList();

        Set<Long> seen = new HashSet<>();
        boolean hasDuplicates = notNullIds.stream().anyMatch(id -> !seen.add(id));
        if(hasDuplicates)
        {
            throw new ObjectNotFound("Duplicate components are not allowed.");
        }
        return notNullIds;
    }

    @Override
    public List<Component>automaticCompatibility(GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest request)
    {
        List<Long> notNullIds = checkAndMapInputtedIds(request);

        List<ComponentEntity> compatibleComponentsEntity = new ArrayList<>();
        Optional<ComponentTypeEntity> foundComponentTypeByIdFromRequest = componentTypeRepository.findById(request.getSearchedComponentTypeId());
        if(foundComponentTypeByIdFromRequest.isEmpty())
        {
            throw new ObjectNotFound("Component type not found");
        }
        for(Long componentId : notNullIds)
        {
            Optional<ComponentEntity> foundComponentById = componentRepository.findByComponentId(componentId);
            if(foundComponentById.isEmpty())
            {
                throw new ObjectNotFound("Component not found");
            }
            List<AutomaticCompatibilityEntity> allCompatibilityRecordsBetweenTwoComponentTypes = automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypes(foundComponentById.get().getComponentType(), foundComponentTypeByIdFromRequest.get());
            if(allCompatibilityRecordsBetweenTwoComponentTypes.isEmpty())
            {
                if(notNullIds.indexOf(componentId) == 0 && notNullIds.size() == 1)
                {
                    List<ComponentEntity> allComponentsFromGivenComponentType = componentRepository.findByComponentType_Id(foundComponentTypeByIdFromRequest.get().getId());
                    return convertComponentEntityToBase(allComponentsFromGivenComponentType);
                }
                if(notNullIds.indexOf(componentId) == notNullIds.size() - 1)
                {
                    return convertComponentEntityToBase(compatibleComponentsEntity);
                }
                continue;
            }
            CompatibilityResult compatibilityResult = GetComponentsFromSearchedComponentTypeThatHaveSpecificationsNeededForCompatibilityWithGivenComponent(allCompatibilityRecordsBetweenTwoComponentTypes,foundComponentById.get(),foundComponentTypeByIdFromRequest.get());
            if(compatibilityResult.getCompatibleComponents().isEmpty())
            {
                return null;
            }
            List<ComponentEntity> compatibilityBetweenSelectedComponentAndListOfComponentsFromDifferentComponentType = new ArrayList<>();
            if(notNullIds.indexOf(componentId) == 0 || compatibleComponentsEntity.isEmpty())
            {
                List<ComponentEntity> compatibilityBetweenFirstComponentAndComponentType = getCompatibleItemsBetweenAComponentAndComponentType(foundComponentById.get(), foundComponentTypeByIdFromRequest.get());
                compatibilityBetweenSelectedComponentAndListOfComponentsFromDifferentComponentType = CheckCompatibilityBetweenSelectedComponentAndAListOfOtherComponentsFromADifferentComponentType(compatibilityBetweenFirstComponentAndComponentType,compatibilityResult.getSpecificationsMap());
            }
            else {
                compatibilityBetweenSelectedComponentAndListOfComponentsFromDifferentComponentType = CheckCompatibilityBetweenSelectedComponentAndAListOfOtherComponentsFromADifferentComponentType(compatibleComponentsEntity, compatibilityResult.getSpecificationsMap());
            }
            compatibleComponentsEntity.clear();
            compatibleComponentsEntity.addAll(compatibilityBetweenSelectedComponentAndListOfComponentsFromDifferentComponentType);
        }
        return convertComponentEntityToBase(compatibleComponentsEntity);
    }
    public List<ComponentEntity> getCompatibleItemsBetweenAComponentAndComponentType(ComponentEntity foundComponentByIdFromRequest, ComponentTypeEntity foundComponentTypeByIdFromRequest)
    {
//        List<Component> allComponentsBase = new ArrayList<>();
        //Identify the component Id and the component type Id that are past trough the api and find their objects
        //Optional<ComponentEntity> foundComponentByIdFromRequest = componentRepository.findByComponentId(componentId);
        //Optional<ComponentTypeEntity> foundComponentTypeByIdFromRequest = componentTypeRepository.findById(componentTypeId);

        //if(foundComponentByIdFromRequest.isPresent() && foundComponentTypeByIdFromRequest.isPresent()) {
            //Get all the automatic compatibility records and rules there are between the component type of the first component and the component type that is passed in the request
            List<AutomaticCompatibilityEntity> allCompatibilityRecordsBetweenTwoComponentTypes = automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypes(foundComponentByIdFromRequest.getComponentType(), foundComponentTypeByIdFromRequest);

            //if there are no rules, all components from the two component types should be considered compatible
            if(allCompatibilityRecordsBetweenTwoComponentTypes.isEmpty()) {
                List<ComponentEntity> allComponentsFromGivenComponentType = componentRepository.findByComponentType_Id(foundComponentTypeByIdFromRequest.getId());
                if(allComponentsFromGivenComponentType.isEmpty())
                {
                    throw new CompatibilityError("COMPONENTS_FROM_CATEGORY_NOT_FOUND");
                }
                return allComponentsFromGivenComponentType;
                //return convertComponentEntityToBase(allComponentsFromGivenComponentType);
            }

            CompatibilityResult compatibilityResult = GetComponentsFromSearchedComponentTypeThatHaveSpecificationsNeededForCompatibilityWithGivenComponent(allCompatibilityRecordsBetweenTwoComponentTypes,foundComponentByIdFromRequest,foundComponentTypeByIdFromRequest);

            //This map will store all specifications that should be considered from the first component side and the corresponding values
            Map<SpecificationTypeEntity, List<String>> allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide = compatibilityResult.getSpecificationsMap();
            //This list store all components from the selected component type that are compatible with the first component considering each rule separately (later all of them will be considered)
            List<ComponentEntity> allCompatibleComponentsBeforeFiltering = compatibilityResult.getCompatibleComponents();

            List<ComponentEntity> allComponentsEntity = CheckCompatibilityBetweenSelectedComponentAndAListOfOtherComponentsFromADifferentComponentType(allCompatibleComponentsBeforeFiltering,allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide);

            return allComponentsEntity;
            //return convertComponentEntityToBase(allComponentsEntity);
        //}
        //throw new ObjectNotFound("COMPONENT NOT FOUND");
    }



    private List<Component> convertComponentEntityToBase(List<ComponentEntity> componentsEntity)
    {
        List<Component> componentsBase = new ArrayList<>();
        for(ComponentEntity componentEntity : componentsEntity) {
            Map<SpecificationTypeEntity, List<String>> dictionaryWithTheSpecificationAndAllValuesForComponent = new HashMap<>();
            List<Component_SpecificationList> allSpecificationsForComponent = componentSpecificationListRepository.findByComponentId(componentEntity);

            //Loop over all the specification of a component type THIS IS NEEDED ONLY FOR THE CONVERTER
            for (Component_SpecificationList specificationList : allSpecificationsForComponent) {
                SpecificationTypeEntity specType = specificationList.getSpecificationType();
                String value = specificationList.getValue();

                //Check if there are already any values for the specification type in the dictionary
                List<String> valuesList = dictionaryWithTheSpecificationAndAllValuesForComponent.get(specType);

                //If there aren't, then add the specification type and the list of values (one by one) to the dictionary
                if (valuesList == null) {
                    valuesList = new ArrayList<>();
                    valuesList.add(value);
                    dictionaryWithTheSpecificationAndAllValuesForComponent.put(specType, valuesList);

                }
                //otherwise just add the value
                else {
                    valuesList.add(value);
                }
            }
            Component componentBase = ComponentConverter.convertFromEntityToBase(componentEntity, dictionaryWithTheSpecificationAndAllValuesForComponent);
            componentsBase.add(componentBase);
        }
        return componentsBase;
    }



    private CompatibilityResult GetComponentsFromSearchedComponentTypeThatHaveSpecificationsNeededForCompatibilityWithGivenComponent(List<AutomaticCompatibilityEntity> allCompatibilityRecordsBetweenTwoComponentTypes,ComponentEntity selectedComponent,ComponentTypeEntity searchedComponentType)
    {
        List<ComponentEntity> allCompatibleComponentsBeforeFiltering = new ArrayList<>();
        List<SpecificationTypeEntity> allSpecificationForTheFirstComponent = new ArrayList<>();
        List<SpecificationTypeEntity> allSpecificationForTheSearchedComponents = new ArrayList<>();
        Map<SpecificationTypeEntity, List<String>> allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide = new HashMap<>();

        //Loop all the automatic compatibilities in order to find the rules and the specifications to consider for each component type
        for (AutomaticCompatibilityEntity automaticCompatibility : allCompatibilityRecordsBetweenTwoComponentTypes) {
            SpecificationTypeEntity specificationForTheMainComponent;
            SpecificationTypeEntity specificationForTheSearchedComponents;
            if(automaticCompatibility.getComponent1Id().getId() == selectedComponent.getComponentType().getId()) {
                //Find the specifications to consider for each rule
                specificationForTheMainComponent = automaticCompatibility.getRuleId().getSpecificationToConsider1Id().getSpecificationType();
                allSpecificationForTheFirstComponent.add(specificationForTheMainComponent);

                specificationForTheSearchedComponents = automaticCompatibility.getRuleId().getSpecificationToConsider2Id().getSpecificationType();
                allSpecificationForTheSearchedComponents.add(specificationForTheSearchedComponents);
            }
            else {
                specificationForTheMainComponent = automaticCompatibility.getRuleId().getSpecificationToConsider2Id().getSpecificationType();
                allSpecificationForTheFirstComponent.add(specificationForTheMainComponent);

                specificationForTheSearchedComponents = automaticCompatibility.getRuleId().getSpecificationToConsider1Id().getSpecificationType();
                allSpecificationForTheSearchedComponents.add(specificationForTheSearchedComponents);
            }
            //Get all specification values for the given specification to consider for the main component
            List<Component_SpecificationList> specificationsForTheSelectedComponent = componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(selectedComponent, specificationForTheMainComponent);
            List<String> values = new ArrayList<>();
            //Store the values of each specification in an array
            for (Component_SpecificationList specificationForTheSelectedComponent : specificationsForTheSelectedComponent) {
                values.add(specificationForTheSelectedComponent.getValue());
            }
            //Add the specification and the values
            allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.put(specificationForTheSearchedComponents, values);

            //The code below gets all components that are part of a provided component type (id) and own a given specification (id) and have a value for this specification that is part of the given list of values
            //Get all components that are part of the searched component type and as values of the specified specification for the second component type (specificationForTheSearchedComponents) have the same values as the ones of the main component
            List<ComponentEntity> allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification = componentRepository.findComponentsByTypeAndSpecification(searchedComponentType.getId(), specificationForTheSearchedComponents.getId(), values);
            allCompatibleComponentsBeforeFiltering.addAll(allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification);
        }
        return CompatibilityResult.builder()
                .specificationsMap(allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide)
                .compatibleComponents(allCompatibleComponentsBeforeFiltering)
                .build();
    }


    private List<ComponentEntity> CheckCompatibilityBetweenSelectedComponentAndAListOfOtherComponentsFromADifferentComponentType(List<ComponentEntity> allCompatibleComponentsBeforeFiltering,Map<SpecificationTypeEntity, List<String>> allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide)
    {
        List<ComponentEntity> allComponentsEntity = new ArrayList<>();
        //some components are duplicated in the allCompatibleComponentsBeforeFiltering list, so now we map only the unique ones
        Set<ComponentEntity> uniqueComponentEntities = new HashSet<>(allCompatibleComponentsBeforeFiltering);

        for(ComponentEntity componentEntity : uniqueComponentEntities) {
            List<Component_SpecificationList> allSpecificationsForComponent = componentSpecificationListRepository.findByComponentId(componentEntity);
            Map<SpecificationTypeEntity, List<String>> mapOfUniqueSpecificationsForItemAndItsValues = new HashMap<>();

            for (Component_SpecificationList specificationForTheSelectedComponent : allSpecificationsForComponent)
            {
                SpecificationTypeEntity specType = specificationForTheSelectedComponent.getSpecificationType();
                String value = specificationForTheSelectedComponent.getValue();

                //Check if there are already any values for the specification type in the dictionary
                List<String> valuesList = mapOfUniqueSpecificationsForItemAndItsValues.get(specType);

                //If there aren't, then add the specification type and the list of values (one by one) to the dictionary
                if (valuesList == null) {
                    valuesList = new ArrayList<>();
                    valuesList.add(value);
                    mapOfUniqueSpecificationsForItemAndItsValues.put(specType, valuesList);

                }
                //otherwise just add the value
                else {
                    valuesList.add(value);
                }
            }

            boolean atLeastOneMatches = false;

            //Loop trough all the specifications for the component and see if there are any matching the those that will be considered for
            // the compatibility and if yes to check if the values that are compatible with the first component can be found as values
            // in the specification of the component values [ex. motherboard and ram are defined as compatible by the clock speed and the DDR values and
            //the motherboard supports clock speed of 2000 and the ram has a clock speed of 2000, but if the motherboard supports DDR5 and the ram has
            //DDR4 they are not compatible. this loop below goes trough all the specifications of the component type, checks which are considered in the
            //rules for the compatibility between the two component types (all the specifications considered are mapped in the
            // allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide map) and checks if the values that the components have are supported by the first
            //component.

            //outerLoop:
            for (Map.Entry<SpecificationTypeEntity, List<String>> entry : mapOfUniqueSpecificationsForItemAndItsValues.entrySet()) {
                SpecificationTypeEntity specification = entry.getKey();
                List<String> values = entry.getValue();
                //atLeastOneMatches = false;
                if (allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.containsKey(specification)) {
                    atLeastOneMatches = false;
                    // Retrieve the list of expected values for this specification type
                    List<String> expectedValues =
                            allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.get(specification);

                    if(!expectedValues.isEmpty()) {
                        for (String value : values) {
                            if (expectedValues.contains(value)) {
                                atLeastOneMatches = true;
                                break; // Found a match, no need to check further
                            }
                        }
                        if (!atLeastOneMatches) {
                            break;

                        }
                    }

                }
            }

            if(atLeastOneMatches) {
                Map<SpecificationTypeEntity, List<String>> dictionaryWithTheSpecificationAndAllValuesForComponent = new HashMap<>();

                //Loop over all the specification of a component type THIS IS NEEDED ONLY FOR THE CONVERTER
                for (Component_SpecificationList specificationList : allSpecificationsForComponent) {
                    SpecificationTypeEntity specType = specificationList.getSpecificationType();
                    String value = specificationList.getValue();

                    //Check if there are already any values for the specification type in the dictionary
                    List<String> valuesList = dictionaryWithTheSpecificationAndAllValuesForComponent.get(specType);

                    //If there aren't, then add the specification type and the list of values (one by one) to the dictionary
                    if (valuesList == null) {
                        valuesList = new ArrayList<>();
                        valuesList.add(value);
                        dictionaryWithTheSpecificationAndAllValuesForComponent.put(specType, valuesList);

                    }
                    else {
                        valuesList.add(value);
                    }
                }
                //Component componentBase = ComponentConverter.convertFromEntityToBase(componentEntity, dictionaryWithTheSpecificationAndAllValuesForComponent);
                allComponentsEntity.add(componentEntity);
            }

        }
        return  allComponentsEntity;
    }

}
