package nl.fontys.s3.copacoproject.business.impl;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.CompatibilityBetweenComponents;
import nl.fontys.s3.copacoproject.business.CompatibilityManager;
import nl.fontys.s3.copacoproject.business.Exceptions.CompatibilityError;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.converters.ComponentConverter;
import nl.fontys.s3.copacoproject.business.converters.SpecificationTypeConverter;
import nl.fontys.s3.copacoproject.business.dto.GetAutomaticCompatibilityResponse;
import nl.fontys.s3.copacoproject.business.dto.GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest;
import nl.fontys.s3.copacoproject.domain.CompatibilityResult;
import nl.fontys.s3.copacoproject.domain.Component;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentSpecificationListRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.entity.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Locale.filter;

@Service
@RequiredArgsConstructor
public class CompatibilityBetweenComponentsImpl implements CompatibilityBetweenComponents {
    private final AutomaticCompatibilityRepository automaticCompatibilityRepository;
    private final ComponentRepository componentRepository;
    private final ComponentTypeRepository componentTypeRepository;
    private final ComponentSpecificationListRepository componentSpecificationListRepository;


    private List<Long> checkIfGivenIdsExistInDatabase(GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest request)
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

        List<Long> missingIds = notNullIds.stream()
                .filter(id -> !componentRepository.existsById(id))
                .toList();
        if (!missingIds.isEmpty()) {
            throw new ObjectNotFound("Components not found: " + missingIds);
        }
        return notNullIds;
    }




    @Override
    public List<GetAutomaticCompatibilityResponse> automaticCompatibility(GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest request)
    {
        List<Long> notNullIds = checkIfGivenIdsExistInDatabase(request);

        List<ComponentEntity> compatibleComponentsEntity = new ArrayList<>();
        boolean searchedComponentTypeExists = componentTypeRepository.existsById(request.getSearchedComponentTypeId());
        if(!searchedComponentTypeExists)
        {
            throw new ObjectNotFound("Component type not found");
        }

        //Loop over the given selected component ids (those that are already selected by the user)
        for(Long componentId : notNullIds)
        {

            //Get the component type of the current component id from the loop
            Long componentTypeIdOfProvidedComponent = componentRepository.findComponentTypeIdByComponentId(componentId);

            //Find all automatic compatibility records between the component type of current component id from the loop and the searched component type
            List<AutomaticCompatibilityEntity> allCompatibilityRecordsBetweenTwoComponentTypes = automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypeIds(componentTypeIdOfProvidedComponent, request.getSearchedComponentTypeId());

            if(allCompatibilityRecordsBetweenTwoComponentTypes.isEmpty())
            {
                //If there are no automatic compatibility rules and there is only one component id in the request, return all components within the searched component type
                if(notNullIds.indexOf(componentId) == 0 && notNullIds.size() == 1)
                {
                    List<ComponentEntity> allComponentsFromGivenComponentType = componentRepository.findByComponentType_Id(request.getSearchedComponentTypeId());
                    return buildResponse(allComponentsFromGivenComponentType);
                }
                //If there are no automatic compatibility rules and it is the last component id, return the compatible components that are in the list so far
                if(notNullIds.indexOf(componentId) == notNullIds.size() - 1)
                {
                    return buildResponse(compatibleComponentsEntity);
                }
                continue;
            }
            CompatibilityResult compatibilityResult = GetComponentsFromSearchedComponentTypeThatHaveSpecificationsNeededForCompatibilityWithGivenComponent(allCompatibilityRecordsBetweenTwoComponentTypes,componentId,componentTypeIdOfProvidedComponent,request.getSearchedComponentTypeId());
            if(compatibilityResult.getCompatibleComponents().isEmpty())
            {
                return null;
            }
            List<ComponentEntity> compatibilityBetweenSelectedComponentAndListOfComponentsFromDifferentComponentType = new ArrayList<>();
            if(notNullIds.indexOf(componentId) == 0 || compatibleComponentsEntity.isEmpty())
            {

                //-------------------------------------------
                List<ComponentEntity> compatibilityBetweenFirstComponentAndComponentType = getCompatibleItemsBetweenAComponentAndComponentType(componentId,componentTypeIdOfProvidedComponent, request.getSearchedComponentTypeId());
                compatibilityBetweenSelectedComponentAndListOfComponentsFromDifferentComponentType = CheckCompatibilityBetweenSelectedComponentAndAListOfOtherComponentsFromADifferentComponentType(compatibilityBetweenFirstComponentAndComponentType,compatibilityResult.getSpecificationsMap());
            }
            else {
                compatibilityBetweenSelectedComponentAndListOfComponentsFromDifferentComponentType = CheckCompatibilityBetweenSelectedComponentAndAListOfOtherComponentsFromADifferentComponentType(compatibleComponentsEntity, compatibilityResult.getSpecificationsMap());
            }
            compatibleComponentsEntity.clear();
            compatibleComponentsEntity.addAll(compatibilityBetweenSelectedComponentAndListOfComponentsFromDifferentComponentType);
        }

        return buildResponse(compatibleComponentsEntity);

    }

    public List<ComponentEntity> getCompatibleItemsBetweenAComponentAndComponentType(Long foundComponentId,Long foundComponentTypeFromComponentFromRequest, Long foundComponentTypeByIdFromRequestId)
    {
            //Get all the automatic compatibility records and rules there are between the component type of the first component and the component type that is passed in the request
            List<AutomaticCompatibilityEntity> allCompatibilityRecordsBetweenTwoComponentTypes = automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypeIds(foundComponentTypeFromComponentFromRequest, foundComponentTypeByIdFromRequestId);

            //if there are no rules, all components from the two component types should be considered compatible
            if(allCompatibilityRecordsBetweenTwoComponentTypes.isEmpty()) {
                List<ComponentEntity> allComponentsFromGivenComponentType = componentRepository.findByComponentType_Id(foundComponentTypeByIdFromRequestId);
                if(allComponentsFromGivenComponentType.isEmpty())
                {
                    throw new CompatibilityError("COMPONENTS_FROM_CATEGORY_NOT_FOUND");
                }
                return allComponentsFromGivenComponentType;
            }

            CompatibilityResult compatibilityResult = GetComponentsFromSearchedComponentTypeThatHaveSpecificationsNeededForCompatibilityWithGivenComponent(allCompatibilityRecordsBetweenTwoComponentTypes,foundComponentId,foundComponentTypeFromComponentFromRequest,foundComponentTypeByIdFromRequestId);

            //This map will store all specifications that should be considered from the first component side and the corresponding values
            Map<SpecificationTypeEntity, List<String>> allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide = compatibilityResult.getSpecificationsMap();
            //This list store all components from the selected component type that are compatible with the first component considering each rule separately (later all of them will be considered)
            List<ComponentEntity> allCompatibleComponentsBeforeFiltering = compatibilityResult.getCompatibleComponents();

            List<ComponentEntity> allComponentsEntity = CheckCompatibilityBetweenSelectedComponentAndAListOfOtherComponentsFromADifferentComponentType(allCompatibleComponentsBeforeFiltering,allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide);

            return allComponentsEntity;
    }



    private List<GetAutomaticCompatibilityResponse> buildResponse(List<ComponentEntity> componentsEntities) {
        List<GetAutomaticCompatibilityResponse> allComponentsForResponse = new ArrayList<>();
        for (ComponentEntity componentEntity : componentsEntities) {
            allComponentsForResponse.add(GetAutomaticCompatibilityResponse.builder()
                    .componentId(componentEntity.getComponentId())
                    .componentName(componentEntity.getComponentName())
                    .componentTypeId(componentEntity.getComponentType().getId())
                    .componentTypeName(componentEntity.getComponentType().getComponentTypeName())
                    .componentImageUrl(componentEntity.getComponentImageUrl())
                    .brand(componentEntity.getBrand().getName())
                    .price(componentEntity.getComponentPrice())
                    .specificationsConsideredForCompatibilityAndValues(getComponentSpecification(componentEntity.getComponentId()))
                    .build());
        }
        return allComponentsForResponse;
    }

    private  Map<SpecificationType, List<String>>  getComponentSpecification(Long componentId)
    {

            Map<SpecificationTypeEntity, List<String>> dictionaryWithTheSpecificationAndAllValuesForComponent = new HashMap<>();
            List<Component_SpecificationList> allSpecificationsForComponent = componentSpecificationListRepository.findByComponentId(componentId);

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
        Map<SpecificationType, List<String>> baseMap = new HashMap<>();

        for (Map.Entry<SpecificationTypeEntity, List<String>> entry : dictionaryWithTheSpecificationAndAllValuesForComponent.entrySet()) {
            SpecificationTypeEntity entityKey = entry.getKey();
            List<String> values = entry.getValue();
            SpecificationType baseKey = SpecificationTypeConverter.convertFromEntityToBase(entityKey);
            baseMap.put(baseKey, values);
        }
            return baseMap;

    }


    private CompatibilityResult GetComponentsFromSearchedComponentTypeThatHaveSpecificationsNeededForCompatibilityWithGivenComponent(List<AutomaticCompatibilityEntity> allCompatibilityRecordsBetweenTwoComponentTypes,Long providedComponentId,Long providedComponentComponentTypeId,Long searchedComponentTypeId)
    {
        List<ComponentEntity> allCompatibleComponentsBeforeFiltering = new ArrayList<>();
        List<SpecificationTypeEntity> allSpecificationForTheFirstComponent = new ArrayList<>();
        List<SpecificationTypeEntity> allSpecificationForTheSearchedComponents = new ArrayList<>();
        Map<SpecificationTypeEntity, List<String>> allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide = new HashMap<>();

        //Loop all the automatic compatibilities in order to find the rules and the specifications to consider for each component type
        for (AutomaticCompatibilityEntity automaticCompatibility : allCompatibilityRecordsBetweenTwoComponentTypes) {
            SpecificationTypeEntity specificationForTheMainComponent;
            SpecificationTypeEntity specificationForTheSearchedComponents;

            //The compatibility table works with couples <component_type_1,component_type_2>
            //This case has two scenarios <component_type_1,component_type_2> and <component_type_2,component_type_1>

            //Scenario <provided_component_type,searched_component_type>
            if(automaticCompatibility.getComponent1Id().getId() == providedComponentComponentTypeId) {
                //Find the specifications to consider for each rule
                specificationForTheMainComponent = automaticCompatibility.getRuleId().getSpecificationToConsider1Id().getSpecificationType();
                allSpecificationForTheFirstComponent.add(specificationForTheMainComponent);

                specificationForTheSearchedComponents = automaticCompatibility.getRuleId().getSpecificationToConsider2Id().getSpecificationType();
                allSpecificationForTheSearchedComponents.add(specificationForTheSearchedComponents);
            }
            //Scenario <searched_component_type,provided_component_type>
            else {
                specificationForTheMainComponent = automaticCompatibility.getRuleId().getSpecificationToConsider2Id().getSpecificationType();
                allSpecificationForTheFirstComponent.add(specificationForTheMainComponent);

                specificationForTheSearchedComponents = automaticCompatibility.getRuleId().getSpecificationToConsider1Id().getSpecificationType();
                allSpecificationForTheSearchedComponents.add(specificationForTheSearchedComponents);
            }

            //Get all specifications records for the selected component and specification_type
            List<Component_SpecificationList> specificationsForTheSelectedComponent = componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(providedComponentId, specificationForTheMainComponent);

            //A map containing the specification type entity and all the values that the provided component has for this specification
            // (In this case it will be only 1 specification type and many values (List<String>) -> only the one specification from the rule in the automatic compatibility
            Map<SpecificationTypeEntity, List<String>> specMap = specificationsForTheSelectedComponent.stream()
                    .collect(Collectors.groupingBy(
                            Component_SpecificationList::getSpecificationType,
                            Collectors.mapping(Component_SpecificationList::getValue, Collectors.toList())));


            //Add the specification type that should be considered for the searched component type and the values that should relate to this specification type, in order
            //to be compatible with the selected component
            allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.put(specificationForTheSearchedComponents, specMap.entrySet().iterator().next().getValue());


            //The code below gets all components that are part of a searched component type (id) and own a given specification type (id) and have a value for this specification that is part of the given list of values
            List<ComponentEntity> allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification = componentRepository.findComponentsByTypeAndSpecification(searchedComponentTypeId, specificationForTheSearchedComponents.getId(), specMap.entrySet().iterator().next().getValue());
            allCompatibleComponentsBeforeFiltering.addAll(allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification);
        }
        return CompatibilityResult.builder()
                .specificationsMap(allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide)
                .compatibleComponents(allCompatibleComponentsBeforeFiltering)
                .build();
    }


    //New
    private List<ComponentEntity> CheckCompatibilityBetweenSelectedComponentAndAListOfOtherComponentsFromADifferentComponentType(
            List<ComponentEntity> allCompatibleComponentsBeforeFiltering,
            Map<SpecificationTypeEntity, List<String>> allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide) {

        List<ComponentEntity> allComponentsEntity = new ArrayList<>();

        // Use a HashSet to eliminate duplicates
        Set<ComponentEntity> uniqueComponentEntities = new HashSet<>(allCompatibleComponentsBeforeFiltering);

        // Iterate over unique components
        for (ComponentEntity componentEntity : uniqueComponentEntities) {
            // Retrieve specifications for the component once and map them
            List<Component_SpecificationList> allSpecificationsForComponent = componentSpecificationListRepository.findByComponentId(componentEntity);

            // Use a map to store the unique specifications and their values
            Map<SpecificationTypeEntity, Set<String>> mapOfUniqueSpecificationsForItemAndItsValues = new HashMap<>();

            for (Component_SpecificationList specification : allSpecificationsForComponent) {
                SpecificationTypeEntity specType = specification.getSpecificationType();
                String value = specification.getValue();

                mapOfUniqueSpecificationsForItemAndItsValues
                        .computeIfAbsent(specType, k -> new HashSet<>())  // If the specType is not present, initialize a new HashSet
                        .add(value);  // Add the value to the Set (ensures no duplicates)
            }

            // Check compatibility with the second component
            boolean atLeastOneMatches = false;

            // Iterate over specifications and check for compatibility
            for (Map.Entry<SpecificationTypeEntity, Set<String>> entry : mapOfUniqueSpecificationsForItemAndItsValues.entrySet()) {
                SpecificationTypeEntity specification = entry.getKey();
                Set<String> values = entry.getValue();

                // Only proceed if the specification type exists in the expected specifications
                if (allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.containsKey(specification)) {
                    // Get expected values and convert them to a Set for fast lookup
                    Set<String> expectedValues = new HashSet<>(allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.get(specification));

                    // Check if any value from the component matches the expected values
                    if (!Collections.disjoint(values, expectedValues)) {
                        atLeastOneMatches = true;
                        break;  // Exit early if a match is found
                    }
                }
            }

            // If there's a match, add the component to the result list
            if (atLeastOneMatches) {
                allComponentsEntity.add(componentEntity);
            }
        }

        return allComponentsEntity;
    }

    //Old
//    private List<ComponentEntity> CheckCompatibilityBetweenSelectedComponentAndAListOfOtherComponentsFromADifferentComponentType(List<ComponentEntity> allCompatibleComponentsBeforeFiltering,Map<SpecificationTypeEntity, List<String>> allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide)
//    {
//        List<ComponentEntity> allComponentsEntity = new ArrayList<>();
//        //some components are duplicated in the allCompatibleComponentsBeforeFiltering list, so now we map only the unique ones
//        Set<ComponentEntity> uniqueComponentEntities = new HashSet<>(allCompatibleComponentsBeforeFiltering);
//
//        for(ComponentEntity componentEntity : uniqueComponentEntities) {
//            List<Component_SpecificationList> allSpecificationsForComponent = componentSpecificationListRepository.findByComponentId(componentEntity);
//            Map<SpecificationTypeEntity, List<String>> mapOfUniqueSpecificationsForItemAndItsValues = new HashMap<>();
//
//            for (Component_SpecificationList specificationForTheSelectedComponent : allSpecificationsForComponent)
//            {
//                SpecificationTypeEntity specType = specificationForTheSelectedComponent.getSpecificationType();
//                String value = specificationForTheSelectedComponent.getValue();
//
//                //Check if there are already any values for the specification type in the dictionary
//                List<String> valuesList = mapOfUniqueSpecificationsForItemAndItsValues.get(specType);
//
//                //If there aren't, then add the specification type and the list of values (one by one) to the dictionary
//                if (valuesList == null) {
//                    valuesList = new ArrayList<>();
//                    valuesList.add(value);
//                    mapOfUniqueSpecificationsForItemAndItsValues.put(specType, valuesList);
//
//                }
//                //otherwise just add the value
//                else {
//                    valuesList.add(value);
//                }
//            }
//
//            boolean atLeastOneMatches = false;
//
//            //Loop trough all the specifications for the component and see if there are any matching the those that will be considered for
//            // the compatibility and if yes to check if the values that are compatible with the first component can be found as values
//            // in the specification of the component values [ex. motherboard and ram are defined as compatible by the clock speed and the DDR values and
//            //the motherboard supports clock speed of 2000 and the ram has a clock speed of 2000, but if the motherboard supports DDR5 and the ram has
//            //DDR4 they are not compatible. this loop below goes trough all the specifications of the component type, checks which are considered in the
//            //rules for the compatibility between the two component types (all the specifications considered are mapped in the
//            // allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide map) and checks if the values that the components have are supported by the first
//            //component.
//
//            //outerLoop:
//            for (Map.Entry<SpecificationTypeEntity, List<String>> entry : mapOfUniqueSpecificationsForItemAndItsValues.entrySet()) {
//                SpecificationTypeEntity specification = entry.getKey();
//                List<String> values = entry.getValue();
//                if (allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.containsKey(specification)) {
//                    atLeastOneMatches = false;
//
//                    // Retrieve the list of expected values for this specification type
//                    List<String> expectedValues =
//                            allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.get(specification);
//
//                    if(!expectedValues.isEmpty()) {
//                        for (String value : values) {
//                            if (expectedValues.contains(value)) {
//                                atLeastOneMatches = true;
//                                break; // Found a match, no need to check further
//                            }
//                        }
//                        if (!atLeastOneMatches) {
//                            break;
//
//                        }
//                    }
//
//                }
//            }
//
//            if(atLeastOneMatches) {
//                Map<SpecificationTypeEntity, List<String>> dictionaryWithTheSpecificationAndAllValuesForComponent = new HashMap<>();
//
//                //Loop over all the specification of a component type THIS IS NEEDED ONLY FOR THE CONVERTER
//                for (Component_SpecificationList specificationList : allSpecificationsForComponent) {
//                    SpecificationTypeEntity specType = specificationList.getSpecificationType();
//                    String value = specificationList.getValue();
//
//                    //Check if there are already any values for the specification type in the dictionary
//                    List<String> valuesList = dictionaryWithTheSpecificationAndAllValuesForComponent.get(specType);
//
//                    //If there aren't, then add the specification type and the list of values (one by one) to the dictionary
//                    if (valuesList == null) {
//                        valuesList = new ArrayList<>();
//                        valuesList.add(value);
//                        dictionaryWithTheSpecificationAndAllValuesForComponent.put(specType, valuesList);
//
//                    }
//                    else {
//                        valuesList.add(value);
//                    }
//                }
//                allComponentsEntity.add(componentEntity);
//            }
//
//        }
//        return  allComponentsEntity;
//    }

}
