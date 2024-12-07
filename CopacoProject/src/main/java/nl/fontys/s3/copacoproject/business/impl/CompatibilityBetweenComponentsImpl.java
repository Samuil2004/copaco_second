//package nl.fontys.s3.copacoproject.business.impl;
//
//import lombok.RequiredArgsConstructor;
//import nl.fontys.s3.copacoproject.business.CompatibilityBetweenComponents;
//import nl.fontys.s3.copacoproject.business.CompatibilityManager;
//import nl.fontys.s3.copacoproject.business.Exceptions.CompatibilityError;
//import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
//import nl.fontys.s3.copacoproject.business.converters.ComponentConverter;
//import nl.fontys.s3.copacoproject.business.converters.SpecificationTypeConverter;
//import nl.fontys.s3.copacoproject.business.dto.GetAutomaticCompatibilityResponse;
//import nl.fontys.s3.copacoproject.business.dto.GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest;
//import nl.fontys.s3.copacoproject.domain.CompatibilityResult;
//import nl.fontys.s3.copacoproject.domain.Component;
//import nl.fontys.s3.copacoproject.domain.SpecificationType;
//import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
//import nl.fontys.s3.copacoproject.persistence.ComponentSpecificationListRepository;
//import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
//import nl.fontys.s3.copacoproject.persistence.entity.*;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import static java.util.Locale.filter;
//
//@Service
//@RequiredArgsConstructor
//public class CompatibilityBetweenComponentsImpl implements CompatibilityBetweenComponents {
//    private final AutomaticCompatibilityRepository automaticCompatibilityRepository;
//    private final ComponentRepository componentRepository;
//    private final ComponentTypeRepository componentTypeRepository;
//    private final ComponentSpecificationListRepository componentSpecificationListRepository;
//
//
//    private List<Long> checkIfGivenIdsExistInDatabase(GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest request)
//    {
//        List<Long> notNullIds = Stream.of(request.getFirstComponentId(),
//                        request.getSecondComponentId(),
//                        request.getThirdComponentId(),
//                        request.getFourthComponentId(),
//                        request.getFifthComponentId(),
//                        request.getSixthComponentId(),
//                        request.getSeventhComponentId())
//                .filter(Objects::nonNull)
//                .toList();
//
//        List<Long> missingIds = notNullIds.stream()
//                .filter(id -> !componentRepository.existsById(id))
//                .toList();
//        if (!missingIds.isEmpty()) {
//            throw new ObjectNotFound("Components not found: " + missingIds);
//        }
//        return notNullIds;
//    }
//
//
//
//
//    @Override
//    public List<GetAutomaticCompatibilityResponse> automaticCompatibility(GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest request)
//    {
//        List<Long> notNullIds = checkIfGivenIdsExistInDatabase(request);
//
//        List<ComponentEntity> compatibleComponentsEntity = new ArrayList<>();
//        boolean searchedComponentTypeExists = componentTypeRepository.existsById(request.getSearchedComponentTypeId());
//        if(!searchedComponentTypeExists)
//        {
//            throw new ObjectNotFound("Component type not found");
//        }
//
//        //Loop over the given selected component ids (those that are already selected by the user)
//        for(Long componentId : notNullIds)
//        {
//
//            //Get the component type of the current component id from the loop
//            Long componentTypeIdOfProvidedComponent = componentRepository.findComponentTypeIdByComponentId(componentId);
//            if(Objects.equals(componentTypeIdOfProvidedComponent, request.getSearchedComponentTypeId()))
//            {
//                throw new CompatibilityError("Once a component is selected, other components from the same category can not be searched.");
//            }
//            //Find all automatic compatibility records between the component type of current component id from the loop and the searched component type
//            List<AutomaticCompatibilityEntity> allCompatibilityRecordsBetweenTwoComponentTypes = automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypeIds(componentTypeIdOfProvidedComponent, request.getSearchedComponentTypeId());
//
//            if(allCompatibilityRecordsBetweenTwoComponentTypes.isEmpty())
//            {
//                //If there are no automatic compatibility rules and there is only one component id in the request, return all components within the searched component type
//                if(notNullIds.indexOf(componentId) == 0 && notNullIds.size() == 1)
//                {
//                    List<ComponentEntity> allComponentsFromGivenComponentType = componentRepository.findByComponentType_Id(request.getSearchedComponentTypeId());
//                    if(allComponentsFromGivenComponentType.isEmpty())
//                    {
//                        throw new CompatibilityError("COMPONENTS_FROM_CATEGORY_NOT_FOUND");
//                    }
//                    return buildResponse(allComponentsFromGivenComponentType);
//                }
//                //If there are no automatic compatibility rules and it is the last component id, return the compatible components that are in the list so far
//                if(notNullIds.indexOf(componentId) == notNullIds.size() - 1)
//                {
//                    return buildResponse(compatibleComponentsEntity);
//                }
//                continue;
//            }
//            //Get a list of components that are compatible with the selected component and also a map with specifications (those that should be considered from the searched component side)
//            //and all the values for each specification. Both data structures are stored in te Compatibility Result class as fields
//            CompatibilityResult compatibilityResult = GetComponentsFromSearchedComponentTypeThatHaveSpecificationsNeededForCompatibilityWithGivenComponent(allCompatibilityRecordsBetweenTwoComponentTypes,componentId,componentTypeIdOfProvidedComponent,request.getSearchedComponentTypeId());
//            //if there are not any compatible component, there is no point to move downwards, because the flow is stopped, so we return empty list (if we have components A B C D and A is compatible
//            //with B C and D and B is NOT compatible with C, there is no point to check if C is compatible with D
//            if(compatibilityResult.getCompatibleComponents().isEmpty())
//            {
//                return List.of();
//            }
//            List<ComponentEntity> compatibilityBetweenSelectedComponentAndListOfComponentsFromDifferentComponentType = new ArrayList<>();
//
//            //if it is the first component or there are not any compatible component so far (not because the previous component is not compatible with any!)
//            if(notNullIds.indexOf(componentId) == 0 || compatibleComponentsEntity.isEmpty())
//            {
//
//                List<ComponentEntity> compatibilityBetweenFirstComponentAndComponentType = getCompatibleItemsBetweenAComponentAndComponentType(componentId,componentTypeIdOfProvidedComponent, request.getSearchedComponentTypeId());
//                compatibilityBetweenSelectedComponentAndListOfComponentsFromDifferentComponentType = CheckCompatibilityBetweenSelectedComponentAndAListOfOtherComponentsFromADifferentComponentType(compatibilityBetweenFirstComponentAndComponentType,compatibilityResult.getSpecificationsMap());
//
//            }
//            else {
//                compatibilityBetweenSelectedComponentAndListOfComponentsFromDifferentComponentType = CheckCompatibilityBetweenSelectedComponentAndAListOfOtherComponentsFromADifferentComponentType(compatibleComponentsEntity, compatibilityResult.getSpecificationsMap());
//            }
//            compatibleComponentsEntity.clear();
//            compatibleComponentsEntity.addAll(compatibilityBetweenSelectedComponentAndListOfComponentsFromDifferentComponentType);
//        }
//
//        return buildResponse(compatibleComponentsEntity);
//
//    }
//
//    public List<ComponentEntity> getCompatibleItemsBetweenAComponentAndComponentType(Long foundComponentId,Long foundComponentTypeFromComponentFromRequest, Long foundComponentTypeByIdFromRequestId)
//    {
//            //Get all the automatic compatibility records and rules there are between the component type of the first component and the component type that is passed in the request
//            List<AutomaticCompatibilityEntity> allCompatibilityRecordsBetweenTwoComponentTypes = automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypeIds(foundComponentTypeFromComponentFromRequest, foundComponentTypeByIdFromRequestId);
//
//            //if there are no rules, all components from the two component types should be considered compatible
//            if(allCompatibilityRecordsBetweenTwoComponentTypes.isEmpty()) {
//                List<ComponentEntity> allComponentsFromGivenComponentType = componentRepository.findByComponentType_Id(foundComponentTypeByIdFromRequestId);
//                if(allComponentsFromGivenComponentType.isEmpty())
//                {
//                    throw new CompatibilityError("COMPONENTS_FROM_CATEGORY_NOT_FOUND");
//                }
//                return allComponentsFromGivenComponentType;
//            }
//
//            CompatibilityResult compatibilityResult = GetComponentsFromSearchedComponentTypeThatHaveSpecificationsNeededForCompatibilityWithGivenComponent(allCompatibilityRecordsBetweenTwoComponentTypes,foundComponentId,foundComponentTypeFromComponentFromRequest,foundComponentTypeByIdFromRequestId);
//
//            //This map will store all specifications that should be considered from the first component side and the corresponding values
//            Map<SpecificationTypeEntity, List<String>> allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide = compatibilityResult.getSpecificationsMap();
//            //This list store all components from the selected component type that are compatible with the first component considering each rule separately (later all of them will be considered)
//            List<ComponentEntity> allCompatibleComponentsBeforeFiltering = compatibilityResult.getCompatibleComponents();
//
//            List<ComponentEntity> allComponentsEntity = CheckCompatibilityBetweenSelectedComponentAndAListOfOtherComponentsFromADifferentComponentType(allCompatibleComponentsBeforeFiltering,allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide);
//
//            return allComponentsEntity;
//    }
//
//
//
//    private List<GetAutomaticCompatibilityResponse> buildResponse(List<ComponentEntity> componentsEntities) {
//        List<GetAutomaticCompatibilityResponse> allComponentsForResponse = new ArrayList<>();
//        for (ComponentEntity componentEntity : componentsEntities) {
//            allComponentsForResponse.add(GetAutomaticCompatibilityResponse.builder()
//                    .componentId(componentEntity.getComponentId())
//                    .componentName(componentEntity.getComponentName())
//                    .componentTypeId(componentEntity.getComponentType().getId())
//                    .componentTypeName(componentEntity.getComponentType().getComponentTypeName())
//                    .componentImageUrl(componentEntity.getComponentImageUrl())
//                    .brand(componentEntity.getBrand().getName())
//                    .price(componentEntity.getComponentPrice())
//                    .componentSpecifications(getComponentSpecification(componentEntity.getComponentId()))
//                    .build());
//        }
//        return allComponentsForResponse;
//    }
//
//    private  Map<SpecificationType, List<String>>  getComponentSpecification(Long componentId)
//    {
//
//            Map<SpecificationTypeEntity, List<String>> dictionaryWithTheSpecificationAndAllValuesForComponent = new HashMap<>();
//            List<Component_SpecificationList> allSpecificationsForComponent = componentSpecificationListRepository.findByComponentId(componentId);
//
//            //Loop over all the specification of a component type THIS IS NEEDED ONLY FOR THE CONVERTER
//            for (Component_SpecificationList specificationList : allSpecificationsForComponent) {
//                SpecificationTypeEntity specType = specificationList.getSpecificationType();
//                String value = specificationList.getValue();
//
//                //Check if there are already any values for the specification type in the dictionary
//                List<String> valuesList = dictionaryWithTheSpecificationAndAllValuesForComponent.get(specType);
//
//                //If there aren't, then add the specification type and the list of values (one by one) to the dictionary
//                if (valuesList == null) {
//                    valuesList = new ArrayList<>();
//                    valuesList.add(value);
//                    dictionaryWithTheSpecificationAndAllValuesForComponent.put(specType, valuesList);
//
//                }
//                //otherwise just add the value
//                else {
//                    valuesList.add(value);
//                }
//            }
//        Map<SpecificationType, List<String>> baseMap = new HashMap<>();
//
//        for (Map.Entry<SpecificationTypeEntity, List<String>> entry : dictionaryWithTheSpecificationAndAllValuesForComponent.entrySet()) {
//            SpecificationTypeEntity entityKey = entry.getKey();
//            List<String> values = entry.getValue();
//            SpecificationType baseKey = SpecificationTypeConverter.convertFromEntityToBase(entityKey);
//            baseMap.put(baseKey, values);
//        }
//            return baseMap;
//
//    }
//
//
//    //OK
//    private CompatibilityResult GetComponentsFromSearchedComponentTypeThatHaveSpecificationsNeededForCompatibilityWithGivenComponent(List<AutomaticCompatibilityEntity> allCompatibilityRecordsBetweenTwoComponentTypes,Long providedComponentId,Long providedComponentComponentTypeId,Long searchedComponentTypeId)
//    {
//        List<ComponentEntity> allCompatibleComponentsBeforeFiltering = new ArrayList<>();
//        List<SpecificationTypeEntity> allSpecificationForTheFirstComponent = new ArrayList<>();
//        List<SpecificationTypeEntity> allSpecificationForTheSearchedComponents = new ArrayList<>();
//        Map<SpecificationTypeEntity, List<String>> allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide = new HashMap<>();
//
//        //Loop all the automatic compatibilities in order to find the rules and the specifications to consider for each component type
//        for (AutomaticCompatibilityEntity automaticCompatibility : allCompatibilityRecordsBetweenTwoComponentTypes) {
//            SpecificationTypeEntity specificationForTheMainComponent;
//            SpecificationTypeEntity specificationForTheSearchedComponents;
//
//            //The compatibility table works with couples <component_type_1,component_type_2>
//            //This case has two scenarios <component_type_1,component_type_2> and <component_type_2,component_type_1>
//
//            //Scenario <provided_component_type,searched_component_type>
//            if(automaticCompatibility.getComponent1Id().getId() == providedComponentComponentTypeId) {
//                //Find the specifications to consider for each rule
//                specificationForTheMainComponent = automaticCompatibility.getRuleId().getSpecificationToConsider1Id().getSpecificationType();
//                allSpecificationForTheFirstComponent.add(specificationForTheMainComponent);
//
//                specificationForTheSearchedComponents = automaticCompatibility.getRuleId().getSpecificationToConsider2Id().getSpecificationType();
//                allSpecificationForTheSearchedComponents.add(specificationForTheSearchedComponents);
//            }
//            //Scenario <searched_component_type,provided_component_type>
//            else {
//                specificationForTheMainComponent = automaticCompatibility.getRuleId().getSpecificationToConsider2Id().getSpecificationType();
//                allSpecificationForTheFirstComponent.add(specificationForTheMainComponent);
//
//                specificationForTheSearchedComponents = automaticCompatibility.getRuleId().getSpecificationToConsider1Id().getSpecificationType();
//                allSpecificationForTheSearchedComponents.add(specificationForTheSearchedComponents);
//            }
//
//            //Get all specifications records for the selected component and specification_type
//            List<Component_SpecificationList> specificationsForTheSelectedComponent = componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(providedComponentId, specificationForTheMainComponent);
//
//            //A map containing the specification type entity and all the values that the provided component has for this specification
//            // (In this case it will be only 1 specification type and many values (List<String>) -> only the one specification from the rule in the automatic compatibility
//            Map<SpecificationTypeEntity, List<String>> specMap = specificationsForTheSelectedComponent.stream()
//                    .collect(Collectors.groupingBy(
//                            Component_SpecificationList::getSpecificationType,
//                            Collectors.mapping(Component_SpecificationList::getValue, Collectors.toList())));
//
//            //Check if there is a specification type in the map that does not have attached values it's the data provider fault, but we can not establish the compatibility of this item,
//            // so we neglect it and move to the next ones (agreement with client) if A and B have compatibility rule with specification C and D AND E and F, but A has only C and no D,
//            //we should consider only D
//            boolean hasEmptyOrMissingKey = specMap.entrySet().stream()
//                    .anyMatch(entry -> entry.getKey() == null || entry.getValue() == null || entry.getValue().isEmpty());
//
//            if (hasEmptyOrMissingKey || specMap.isEmpty()) {
//                continue;
//            }
//
//            //Add the specification type that should be considered for the searched component type and the values that should relate to this specification type, in order
//            //to be compatible with the selected component
//            allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.put(specificationForTheSearchedComponents, specMap.entrySet().iterator().next().getValue());
//
//
//            //The code below gets all components that are part of a searched component type (id) and own a given specification type (id) and have a value for this specification that is part of the given list of values
//            List<ComponentEntity> allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification = componentRepository.findComponentsByTypeAndSpecification(searchedComponentTypeId, specificationForTheSearchedComponents.getId(), specMap.entrySet().iterator().next().getValue());
//            allCompatibleComponentsBeforeFiltering.addAll(allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification);
//        }
//        return CompatibilityResult.builder()
//                .specificationsMap(allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide)
//                .compatibleComponents(allCompatibleComponentsBeforeFiltering)
//                .build();
//    }
//
//    //Old
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
//}



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
import nl.fontys.s3.copacoproject.domain.FilterComponentsResult;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentSpecificationListRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CompatibilityBetweenComponentsImpl implements CompatibilityBetweenComponents {
    private final AutomaticCompatibilityRepository automaticCompatibilityRepository;
    private final ComponentRepository componentRepository;
    private final ComponentTypeRepository componentTypeRepository;
    private final ComponentSpecificationListRepository componentSpecificationListRepository;
    //private Integer pageNumber = 0;
//    private Boolean thereIsNextPage = true;

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
        Boolean thereIsNextPage = true;
        Pageable pageable = PageRequest.of(request.getPageNumber(), 11);

        long startTime = System.nanoTime();
        List<ComponentEntity> filteredComponentsSoFar = new ArrayList<>();
        List<ComponentEntity> filteredComponentsAfterLooping = new ArrayList<>();
        try {
            //JACKIE -> each component that has been chosen
            //ROCKIE -> searched component type
            List<Long> notNullIds = checkIfGivenIdsExistInDatabase(request);

            //List<ComponentEntity> compatibleComponentsEntity = new ArrayList<>();
            boolean searchedComponentTypeExists = componentTypeRepository.existsById(request.getSearchedComponentTypeId());
            if (!searchedComponentTypeExists) {
                throw new ObjectNotFound("Component type not found");
            }

            //List<ComponentEntity> filteredComponentsSoFar = new ArrayList<>();

            while (filteredComponentsAfterLooping.size() < 10 && thereIsNextPage) {
                //Loop over the given selected component ids (those that are already selected by the user) (all JACKIES)
                for (Long componentId : notNullIds) {

                    //Get the component type of the current component id from the loop
                    Long componentTypeIdOfProvidedComponent = componentRepository.findComponentTypeIdByComponentId(componentId);
                    if (Objects.equals(componentTypeIdOfProvidedComponent, request.getSearchedComponentTypeId())) {
                        throw new CompatibilityError("Once a component is selected, other components from the same category can not be searched.");
                    }
                    //Find all automatic compatibility records between the component type of current component id (Component type of JACKIE) from the loop and the searched component type (ROCKIE)
                    List<AutomaticCompatibilityEntity> allAutomaticCompatibilityRulesBetweenTwoComponentTypes = automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypeIds(componentTypeIdOfProvidedComponent, request.getSearchedComponentTypeId());

                    if (allAutomaticCompatibilityRulesBetweenTwoComponentTypes.isEmpty()) {
                        //If there is no automatic compatibility, and it is the first component from the request, get the first ten components from the searched category and consider them as compatible (since there is no rule)
                        if (notNullIds.indexOf(componentId) == 0) {

                            //Get the specification "meant for"
                            //If there is a map returned - use it
                            //if not get all component without filtering (case: videocard [only for PCs and Workstations] and dvd)

                            //Get 11 components from the searched category based on the pageable (page num and size). We need eleven in order to know if there is at least one more component for the next page
                            List<ComponentEntity> elevenComponentsFromTheSearchedComponentType = componentRepository.findByComponentType_Id(request.getSearchedComponentTypeId(), pageable);
                            if (elevenComponentsFromTheSearchedComponentType.isEmpty()) {
                                throw new CompatibilityError("COMPONENTS_FROM_CATEGORY_NOT_FOUND");
                            }
                            //If there are 11 components, this means that there is at least 1 component for the next page
                            if (elevenComponentsFromTheSearchedComponentType.size() == 11) {
                                thereIsNextPage = true;
                            } else {
                                thereIsNextPage = false;
                            }
                            //If it is the only component in the request, imidiatelly return the first ten components from the searched category
                            if (notNullIds.size() == 1) {
                                //return only the first ten
                                return buildResponse(elevenComponentsFromTheSearchedComponentType.stream().limit(10).collect(Collectors.toList()),thereIsNextPage);
                            }
                            //if it is not the only component in the request, add the ten component from the searched category to the filtered components so far, and move further
                            filteredComponentsSoFar.addAll(elevenComponentsFromTheSearchedComponentType);
                            continue;

                        }
                        //If there are no automatic compatibility rule, and it is the last component id, then go down to the check for number of items in the list and the return statements
                        if (notNullIds.indexOf(componentId) == notNullIds.size() - 1) {
                            break;
                        }
                        continue;
                    }

                    //If it is the first component -> consider the rules and get the compatible component (based on the rule) from the searched component type
                    //If it is NOT the first component -> consider the rules and filter the compatible components that we have so far
                    FilterComponentsResult filteredComponents = filterComponentsBasedOnRule(allAutomaticCompatibilityRulesBetweenTwoComponentTypes, componentId, componentTypeIdOfProvidedComponent, request.getSearchedComponentTypeId(), notNullIds.indexOf(componentId), filteredComponentsSoFar,pageable,thereIsNextPage);
                    if (filteredComponents.getComponents().isEmpty()) {
                        throw new ObjectNotFound("There aren't compatible components");
                    }
                    filteredComponentsSoFar.clear();
                    filteredComponentsSoFar.addAll(filteredComponents.getComponents());
                    thereIsNextPage = filteredComponents.getThereIsNextPage();
                }
                filteredComponentsAfterLooping.addAll(filteredComponentsSoFar);
                filteredComponentsSoFar.clear();

                pageable = PageRequest.of(pageable.getPageNumber() + 1, 11);
            }
        }finally {
            long endTime = System.nanoTime();
            long durationInMillis = (endTime - startTime) / 1_000_000;
            System.out.println("Method execution time: " + durationInMillis + " ms");
        }
        return buildResponse(filteredComponentsAfterLooping.stream().limit(10).collect(Collectors.toList()),thereIsNextPage);
    }


    private FilterComponentsResult filterComponentsBasedOnRule(List<AutomaticCompatibilityEntity> allAutomaticCompatibilityRulesBetweenTwoComponentTypes,Long providedComponentId,Long providedComponentComponentTypeId,Long searchedComponentTypeId,Integer indexOfProvidedComponent,List<ComponentEntity> compatibleComponentsSoFar, Pageable pageable,Boolean thereIsNextPage)
    {
        List<SpecificationTypeEntity> allSpecificationForTheFirstComponent = new ArrayList<>();
        List<SpecificationTypeEntity> allSpecificationForTheSearchedComponents = new ArrayList<>();
        Map<SpecificationTypeEntity, List<String>> allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide = new HashMap<>();

        //Loop all the automatic compatibilities in order to find the rules and the specifications to consider for each component type
        for (AutomaticCompatibilityEntity automaticCompatibility : allAutomaticCompatibilityRulesBetweenTwoComponentTypes) {
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

            //Check if there is a specification type in the map that does not have attached values it's the data provider fault, but we can not establish the compatibility of this item,
            // so we neglect it and move to the next ones (agreement with client) if A and B have compatibility rule with specification C and D AND E and F, but A has only C and no D,
            //we should consider only D
            boolean hasEmptyOrMissingKey = specMap.entrySet().stream()
                    .anyMatch(entry -> entry.getKey() == null || entry.getValue() == null || entry.getValue().isEmpty());

            if (hasEmptyOrMissingKey || specMap.isEmpty()) {
                continue;
            }

            //Add the specification type that should be considered for the searched component type and the values that should relate to this specification type, in order
            //to be compatible with the selected component
            allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.put(specificationForTheSearchedComponents, specMap.entrySet().iterator().next().getValue());

            //if it is the first component from the provided ones in the request and there are no compatible components (because it is the first one,
            //then take the first ten components from the searched component type that satisfy the rule [later, they will be filtered, but now in this if statement]
            if(indexOfProvidedComponent == 0 && compatibleComponentsSoFar.isEmpty()) {
                //The code below gets all components that are part of a searched component type (id) and own a given specification type (id) and have a value for this specification that is part of the given list of values
                Page<ComponentEntity> page = componentRepository.findComponentsByTypeAndSpecification(searchedComponentTypeId, specificationForTheSearchedComponents.getId(), specMap.entrySet().iterator().next().getValue(), pageable);
                List<ComponentEntity> allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification = page.getContent();

                if(allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification.isEmpty())
                {
                    throw new ObjectNotFound("Compatible components from searched component type were not found");
                }
                if(allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification.size() < 11)
                {
                    thereIsNextPage = false;
                }
                //if(allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification.size() < 10)
                compatibleComponentsSoFar.addAll(allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification);
                continue;
            }
            else {

                //We need a copy of the list, because we need it to loop trough it, otherwise if we loop trought the original 'compatibleComponentsSoFar' list and try to remove items
                //while looping, it will give us an error
                List<ComponentEntity> copyOfCompatibleComponents = new ArrayList<>(compatibleComponentsSoFar);
                //Filter the compatible components that we have so far based on the new rules from the loop
                for(ComponentEntity componentEntity : copyOfCompatibleComponents)
                {
                    //Get the values of the componentEntity (from the loop) for the specificationThatShouldBeConsideredFromTheSearchedType
                    // + If there are no value/s -> the combination component_id <-> specification_that_should_be_considered_from_the_searched_component_type ( that is
                    //read by the rule ) this means that the component does not have this specification which automatically means not compatible
                    // + If there are values, then we have to check if these values are the same as the ones from the specification map that is above
                    List<String> allTheValuesTheComponentHasForTheConsideredSpecification = componentSpecificationListRepository.findValuesByComponentAndSpecification(componentEntity.getComponentId(),specificationForTheSearchedComponents.getId());
                    //if there are no values -> this means component is not compatible
                    if(allTheValuesTheComponentHasForTheConsideredSpecification.isEmpty())
                    {
                        compatibleComponentsSoFar.remove(componentEntity);
                        continue;
                    }
                    //Check if any of the specification values of the component (JACKIE) match any of the specification values of the current component(from the loop)
                    // + If yes, this means they are compatible
                    // + If not, this means they are not compatible
                    boolean matchFound = specMap.entrySet().stream()
                            .anyMatch(entry -> {
                                // Get the list of values from the map for the current main component for the specification defined by the rule
                                List<String> valuesForTheSpecificationFromTheRuleForTheMainComponent = entry.getValue();

                                // Check if any value in the list overlaps with the provided list
                                return valuesForTheSpecificationFromTheRuleForTheMainComponent.stream().anyMatch(allTheValuesTheComponentHasForTheConsideredSpecification::contains);
                            });
                    if(!matchFound)
                    {
                        compatibleComponentsSoFar.remove(componentEntity);
                        continue;
                    }

                }
            }

        }
        return new FilterComponentsResult(new ArrayList<>(compatibleComponentsSoFar), thereIsNextPage);
    }

    private List<GetAutomaticCompatibilityResponse> buildResponse(List<ComponentEntity> componentsEntities,Boolean thereIsNextPage) {
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
                    .componentSpecifications(getComponentSpecification(componentEntity.getComponentId()))
                    .thereIsNextPage(thereIsNextPage)
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


    private Map<String,List<String>> defineValuesForComponentsFilteringBasedOnConfigurationType(String configurationType, Long componentTypeId)
    {
        //Component voor - 1070
        //Bedoel voor - 947
        //Soort - 954
        Map<String, List<String>> serverConfig = new HashMap<>();
        if(componentTypeId == 1)
        {
            switch(configurationType)
            {
                case "Server":
                    serverConfig.put("Component voor", List.of("Server"));
                    break;
                case "PC":
                    serverConfig.put("Component voor", List.of("Workstation"));
                    break;
                case "Workstation":
                    serverConfig.put("Component voor", List.of("Workstation"));
                    break;
            }
            return serverConfig;
        }
        else if(componentTypeId == 2)
        {
            switch(configurationType)
            {
                case "Server":
                    serverConfig.put("Component voor", List.of("Server"));
                    break;
                case "PC":
                    serverConfig.put("Component voor", List.of("PC"));
                    break;
                case "Workstation":
                    serverConfig.put("Component voor", List.of("Workstation"));
                    break;
            }
            return serverConfig;
        }
        else if(componentTypeId == 4)
        {
            switch(configurationType)
            {
                case "Server":
                    serverConfig.put("Component voor", List.of("Server"));
                    break;
                case "PC":
                    serverConfig.put("Component voor", List.of("PC"));
                    break;
                case "Workstation":
                    serverConfig.put("Component voor", List.of("Workstation"));
                    break;
                case "Laptop":
                    serverConfig.put("Component voor", List.of("Notebook"));
                    break;
            }
            return serverConfig;
        }
        else if(componentTypeId == 5)
        {
            switch(configurationType)
            {
                case "Server":
                    serverConfig.put("Bedoeld voor", List.of("Server"));
                    break;
                case "PC":
                    serverConfig.put("Bedoeld voor", List.of("PC"));
                    break;
                case "Workstation":
                    serverConfig.put("Bedoeld voor", List.of("PC"));
                    break;
            }
            return serverConfig;
        }
        else if(componentTypeId == 6)
        {
            switch(configurationType)
            {
                case "PC":
                    serverConfig.put("Soort", List.of("PC"));
                    break;
            }
            return serverConfig;
        }
        else if(componentTypeId == 7)
        {
            switch(configurationType)
            {
                case "Server":
                    serverConfig.put("Soort", List.of("Fan","Fan module"));
                    break;
                case "PC":
                    serverConfig.put("Soort", List.of("Liquid cooling kit","Heatsink","Radiatior","Air cooler","Radiator block","Cooler","All-in-one liquid cooler","Cooler"));
                    break;
                case "Laptop":
                    serverConfig.put("Soort", List.of("Thermal paste"));
                    break;
            }
            return serverConfig;
        }
        else if(componentTypeId == 8)
        {
            switch(configurationType)
            {
                case "Server":
                    serverConfig.put("Soort", List.of("Fan","Fan tray","Cooler"));
                    break;
                case "PC":
                    serverConfig.put("Soort", List.of("Liquid cooling kit","Heatsink","Radiatior","Air cooler","Radiator block","Cooler","All-in-one liquid cooler"));
                    break;
                case "Laptop":
                    serverConfig.put("Soort", List.of("Thermal paste"));
                    break;
            }
            return serverConfig;
        }
        else if(componentTypeId == 10)
        {
            switch(configurationType)
            {
                case "Server":
                    serverConfig.put("Component voor", List.of("Server"));
                    break;
                case "Workstation":
                    serverConfig.put("Component voor", List.of("Workstation"));
                    break;
                case "PC":
                    serverConfig.put("Component voor", List.of("PC"));
                    break;
                case "Laptop":
                    serverConfig.put("Component voor", List.of("Notebook"));
                    break;
            }
            return serverConfig;
        }
        else if(componentTypeId == 11)
        {
            switch(configurationType)
            {
                case "Server":
                    serverConfig.put("Component voor", List.of("Server"));
                    break;
                case "Workstation":
                    serverConfig.put("Component voor", List.of("Workstation","workstation"));
                    break;
                case "PC":
                    serverConfig.put("Component voor", List.of("PC"));
                    break;
            }
            return serverConfig;
        }
        else {
            return null;
        }
    }

//
//    public List<ComponentEntity> getCompatibleItemsBetweenAComponentAndComponentType(Long foundComponentId,Long foundComponentTypeFromComponentFromRequest, Long foundComponentTypeByIdFromRequestId)
//    {
//        //Get all the automatic compatibility records and rules there are between the component type of the first component and the component type that is passed in the request
//        List<AutomaticCompatibilityEntity> allCompatibilityRecordsBetweenTwoComponentTypes = automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypeIds(foundComponentTypeFromComponentFromRequest, foundComponentTypeByIdFromRequestId);
//
//        //if there are no rules, all components from the two component types should be considered compatible
//        if(allCompatibilityRecordsBetweenTwoComponentTypes.isEmpty()) {
//            List<ComponentEntity> allComponentsFromGivenComponentType = componentRepository.findFirst10ByComponentType_Id(foundComponentTypeByIdFromRequestId);
//            if(allComponentsFromGivenComponentType.isEmpty())
//            {
//                throw new CompatibilityError("COMPONENTS_FROM_CATEGORY_NOT_FOUND");
//            }
//            return allComponentsFromGivenComponentType;
//        }
//
//        CompatibilityResult compatibilityResult = GetComponentsFromSearchedComponentTypeThatHaveSpecificationsNeededForCompatibilityWithGivenComponent(allCompatibilityRecordsBetweenTwoComponentTypes,foundComponentId,foundComponentTypeFromComponentFromRequest,foundComponentTypeByIdFromRequestId);
//
//        //This map will store all specifications that should be considered from the first component side and the corresponding values
//        Map<SpecificationTypeEntity, List<String>> allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide = compatibilityResult.getSpecificationsMap();
//        //This list store all components from the selected component type that are compatible with the first component considering each rule separately (later all of them will be considered)
//        List<ComponentEntity> allCompatibleComponentsBeforeFiltering = compatibilityResult.getCompatibleComponents();
//
//        List<ComponentEntity> allComponentsEntity = CheckCompatibilityBetweenSelectedComponentAndAListOfOtherComponentsFromADifferentComponentType(allCompatibleComponentsBeforeFiltering,allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide);
//
//        return allComponentsEntity;
//    }
//
//
//
//
//
//    //OK
//    private CompatibilityResult GetComponentsFromSearchedComponentTypeThatHaveSpecificationsNeededForCompatibilityWithGivenComponent(List<AutomaticCompatibilityEntity> allCompatibilityRecordsBetweenTwoComponentTypes,Long providedComponentId,Long providedComponentComponentTypeId,Long searchedComponentTypeId)
//    {
//        List<ComponentEntity> allCompatibleComponentsBeforeFiltering = new ArrayList<>();
//        List<SpecificationTypeEntity> allSpecificationForTheFirstComponent = new ArrayList<>();
//        List<SpecificationTypeEntity> allSpecificationForTheSearchedComponents = new ArrayList<>();
//        Map<SpecificationTypeEntity, List<String>> allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide = new HashMap<>();
//
//        //Loop all the automatic compatibilities in order to find the rules and the specifications to consider for each component type
//        for (AutomaticCompatibilityEntity automaticCompatibility : allCompatibilityRecordsBetweenTwoComponentTypes) {
//            SpecificationTypeEntity specificationForTheMainComponent;
//            SpecificationTypeEntity specificationForTheSearchedComponents;
//
//            //The compatibility table works with couples <component_type_1,component_type_2>
//            //This case has two scenarios <component_type_1,component_type_2> and <component_type_2,component_type_1>
//
//            //Scenario <provided_component_type,searched_component_type>
//            if(automaticCompatibility.getComponent1Id().getId() == providedComponentComponentTypeId) {
//                //Find the specifications to consider for each rule
//                specificationForTheMainComponent = automaticCompatibility.getRuleId().getSpecificationToConsider1Id().getSpecificationType();
//                allSpecificationForTheFirstComponent.add(specificationForTheMainComponent);
//
//                specificationForTheSearchedComponents = automaticCompatibility.getRuleId().getSpecificationToConsider2Id().getSpecificationType();
//                allSpecificationForTheSearchedComponents.add(specificationForTheSearchedComponents);
//            }
//            //Scenario <searched_component_type,provided_component_type>
//            else {
//                specificationForTheMainComponent = automaticCompatibility.getRuleId().getSpecificationToConsider2Id().getSpecificationType();
//                allSpecificationForTheFirstComponent.add(specificationForTheMainComponent);
//
//                specificationForTheSearchedComponents = automaticCompatibility.getRuleId().getSpecificationToConsider1Id().getSpecificationType();
//                allSpecificationForTheSearchedComponents.add(specificationForTheSearchedComponents);
//            }
//
//            //Get all specifications records for the selected component and specification_type
//            List<Component_SpecificationList> specificationsForTheSelectedComponent = componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(providedComponentId, specificationForTheMainComponent);
//
//            //A map containing the specification type entity and all the values that the provided component has for this specification
//            // (In this case it will be only 1 specification type and many values (List<String>) -> only the one specification from the rule in the automatic compatibility
//            Map<SpecificationTypeEntity, List<String>> specMap = specificationsForTheSelectedComponent.stream()
//                    .collect(Collectors.groupingBy(
//                            Component_SpecificationList::getSpecificationType,
//                            Collectors.mapping(Component_SpecificationList::getValue, Collectors.toList())));
//
//            //Check if there is a specification type in the map that does not have attached values it's the data provider fault, but we can not establish the compatibility of this item,
//            // so we neglect it and move to the next ones (agreement with client) if A and B have compatibility rule with specification C and D AND E and F, but A has only C and no D,
//            //we should consider only D
//            boolean hasEmptyOrMissingKey = specMap.entrySet().stream()
//                    .anyMatch(entry -> entry.getKey() == null || entry.getValue() == null || entry.getValue().isEmpty());
//
//            if (hasEmptyOrMissingKey || specMap.isEmpty()) {
//                continue;
//            }
//
//            //Add the specification type that should be considered for the searched component type and the values that should relate to this specification type, in order
//            //to be compatible with the selected component
//            allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.put(specificationForTheSearchedComponents, specMap.entrySet().iterator().next().getValue());
//
//
//            //The code below gets all components that are part of a searched component type (id) and own a given specification type (id) and have a value for this specification that is part of the given list of values
//            //List<ComponentEntity> allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification = componentRepository.findComponentsByTypeAndSpecification(searchedComponentTypeId, specificationForTheSearchedComponents.getId(), specMap.entrySet().iterator().next().getValue());
//            Page<ComponentEntity> page = componentRepository.findComponentsByTypeAndSpecification(searchedComponentTypeId,  specificationForTheSearchedComponents.getId(), specMap.entrySet().iterator().next().getValue(), pageable);
//            List<ComponentEntity> allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification = page.getContent();
//
//            allCompatibleComponentsBeforeFiltering.addAll(allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification);
//        }
//        return CompatibilityResult.builder()
//                .specificationsMap(allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide)
//                .compatibleComponents(allCompatibleComponentsBeforeFiltering)
//                .build();
//    }
//
//    //Old
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

