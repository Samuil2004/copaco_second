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
//import nl.fontys.s3.copacoproject.domain.*;
//import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
//import nl.fontys.s3.copacoproject.persistence.ComponentSpecificationListRepository;
//import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
//import nl.fontys.s3.copacoproject.persistence.entity.*;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@Service
//@RequiredArgsConstructor
//public class CompatibilityBetweenComponentsImpl implements CompatibilityBetweenComponents {
//    private final AutomaticCompatibilityRepository automaticCompatibilityRepository;
//    private final ComponentRepository componentRepository;
//    private final ComponentTypeRepository componentTypeRepository;
//    private final ComponentSpecificationListRepository componentSpecificationListRepository;
//    private final RuleEntityRepository ruleEntityRepository;
//    //private Integer pageNumber = 0;
////    private Boolean thereIsNextPage = true;
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
//    @Override
//    public List<GetAutomaticCompatibilityResponse> automaticCompatibility(GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest request)
//    {
//        Boolean thereIsNextPage = true;
//        Pageable pageable = PageRequest.of(request.getPageNumber(), 11);
//        String typeOfConfiguration = request.getTypeOfConfiguration();
//
//        long startTime = System.nanoTime();
//        List<ComponentEntity> filteredComponentsSoFar = new ArrayList<>();
//        List<ComponentEntity> filteredComponentsAfterLooping = new ArrayList<>();
//        try {
//            //JACKIE -> each component that has been chosen
//            //ROCKIE -> searched component type
//            List<Long> notNullIds = checkIfGivenIdsExistInDatabase(request);
//
//            //List<ComponentEntity> compatibleComponentsEntity = new ArrayList<>();
//            boolean searchedComponentTypeExists = componentTypeRepository.existsById(request.getSearchedComponentTypeId());
//            if (!searchedComponentTypeExists) {
//                throw new ObjectNotFound("Component type not found");
//            }
//
//            //List<ComponentEntity> filteredComponentsSoFar = new ArrayList<>();
//
//            while (filteredComponentsAfterLooping.size() < 10 && thereIsNextPage) {
//                //Loop over the given selected component ids (those that are already selected by the user) (all JACKIES)
//                for (Long componentId : notNullIds) {
//
//                    //Get the component type of the current component id from the loop
//                    Long componentTypeIdOfProvidedComponent = componentRepository.findComponentTypeIdByComponentId(componentId);
//                    if (Objects.equals(componentTypeIdOfProvidedComponent, request.getSearchedComponentTypeId())) {
//                        throw new CompatibilityError("Once a component is selected, other components from the same category can not be searched.");
//                    }
//                    //Find all automatic compatibility records between the component type of current component id (Component type of JACKIE) from the loop and the searched component type (ROCKIE)
//                    List<AutomaticCompatibilityEntity> allAutomaticCompatibilityRulesBetweenTwoComponentTypes = automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypeIds(componentTypeIdOfProvidedComponent, request.getSearchedComponentTypeId(),typeOfConfiguration);
//
//                    if (allAutomaticCompatibilityRulesBetweenTwoComponentTypes.isEmpty()) {
//                        //If there is no automatic compatibility, and it is the first component from the request, get the first ten components from the searched category and consider them as compatible (since there is no rule)
//                        if (notNullIds.indexOf(componentId) == 0) {
//
//
//                            List<ComponentEntity> elevenComponentsFromTheSearchedComponentType = new ArrayList<>();
//
//                            //Get the specification "meant for"[purpose] (most of the components have specification such as PC or Server or Workstation which helps to filter only the components for the selected type of configuration
//                            Map<Long,List<String>> getTheFilteringForTheSearchedComponentType = defineValuesForComponentsFilteringBasedOnConfigurationType(typeOfConfiguration,request.getSearchedComponentTypeId());
//                            //If it is empty (in case fo video card and dvd because they do not have such specifications), just get eleven components from the searched component type based on the page number
//                            if (getTheFilteringForTheSearchedComponentType == null || getTheFilteringForTheSearchedComponentType.isEmpty())
//                            {
//                                //Get 11 components from the searched category based on the pageable (page num and size). We need eleven in order to know if there is at least one more component for the next page
//                                elevenComponentsFromTheSearchedComponentType = componentRepository.findByComponentType_Id(request.getSearchedComponentTypeId(), pageable);
//                            }
//                            //If it is not null, consider the configuration type (ex: if the configuration is for PC, only components within the searched component type that are meant for PC should be retrieved)
//                            else
//                            {
//                                //Get 11 components from the searched category based on the pageable (page num and size) and the configuration type. We need eleven in order to know if there is at least one more component for the next page
//                                Map.Entry<Long, List<String>> firstEntry = getTheFilteringForTheSearchedComponentType.entrySet().iterator().next();
//                                elevenComponentsFromTheSearchedComponentType = componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(request.getSearchedComponentTypeId(),firstEntry.getKey(),firstEntry.getValue(),pageable);
//                            }
//                            //If there is a map returned - use it
//                            //if not get all component without filtering (case: videocard [only for PCs and Workstations] and dvd)
//
//                            //Get 11 components from the searched category based on the pageable (page num and size). We need eleven in order to know if there is at least one more component for the next page
//                            //List<ComponentEntity> elevenComponentsFromTheSearchedComponentType = componentRepository.findByComponentType_Id(request.getSearchedComponentTypeId(), pageable);
//                            if (elevenComponentsFromTheSearchedComponentType.isEmpty()) {
//                                throw new CompatibilityError("COMPONENTS_FROM_CATEGORY_NOT_FOUND");
//                            }
//                            //If there are 11 components, this means that there is at least 1 component for the next page
//                            if (elevenComponentsFromTheSearchedComponentType.size() == 11) {
//                                thereIsNextPage = true;
//                            } else {
//                                thereIsNextPage = false;
//                            }
//                            //If it is the only component in the request, imidiatelly return the first ten components from the searched category
//                            if (notNullIds.size() == 1) {
//                                //return only the first ten
//                                return buildResponse(elevenComponentsFromTheSearchedComponentType.stream().limit(10).collect(Collectors.toList()),thereIsNextPage);
//                            }
//                            //if it is not the only component in the request, add the ten component from the searched category to the filtered components so far, and move further
//                            filteredComponentsSoFar.addAll(elevenComponentsFromTheSearchedComponentType);
//                            continue;
//
//                        }
//                        //If there are no automatic compatibility rule, and it is the last component id, then go down to the check for number of items in the list and the return statements
//                        if (notNullIds.indexOf(componentId) == notNullIds.size() - 1) {
//                            break;
//                        }
//                        continue;
//                    }
//
//                    //If it is the first component -> consider the rules and get the compatible component (based on the rule) from the searched component type
//                    //If it is NOT the first component -> consider the rules and filter the compatible components that we have so far
//                    FilterComponentsResult filteredComponents = filterComponentsBasedOnRule(allAutomaticCompatibilityRulesBetweenTwoComponentTypes, componentId, componentTypeIdOfProvidedComponent, request.getSearchedComponentTypeId(), notNullIds.indexOf(componentId), filteredComponentsSoFar,pageable,thereIsNextPage,typeOfConfiguration);
////                    if (filteredComponents.getComponents().isEmpty()) {
////                        throw new ObjectNotFound("There aren't compatible components");
////                    }
//                    filteredComponentsSoFar.clear();
//                    filteredComponentsSoFar.addAll(filteredComponents.getComponents());
//                    thereIsNextPage = filteredComponents.getThereIsNextPage();
//                }
//                filteredComponentsAfterLooping.addAll(filteredComponentsSoFar);
//                filteredComponentsSoFar.clear();
//
//                pageable = PageRequest.of(pageable.getPageNumber() + 1, 11);
//            }
//        }finally {
//            long endTime = System.nanoTime();
//            long durationInMillis = (endTime - startTime) / 1_000_000;
//            System.out.println("Method execution time: " + durationInMillis + " ms");
//        }
//        return buildResponse(filteredComponentsAfterLooping.stream().limit(10).collect(Collectors.toList()),thereIsNextPage);
//    }
//
//
//
//
//    private FilterComponentsResult handleManualCompatibilityBetweenSpecifications(AutomaticCompatibilityEntity automaticCompatibility,Long providedComponentId,Long searchedComponentTypeId,Integer indexOfProvidedComponent,List<ComponentEntity> compatibleComponentsSoFar, Pageable pageable,Boolean thereIsNextPage,String typeOfConfiguration) {
//        //------1. Get the specification for the main component of the rule
//        SpecificationTypeEntity specificationForTheMainComponent = automaticCompatibility.getRuleId().getSpecificationToConsider1Id().getSpecificationType();
//        SpecificationTypeEntity specificationForTheSearchedComponents = automaticCompatibility.getRuleId().getSpecificationToConsider2Id().getSpecificationType();
//
//
//        //------2. Get the values the component has for the specification
//        List<Component_SpecificationList> specificationsForTheSelectedComponent = componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(providedComponentId, specificationForTheMainComponent);
//
//        //-> A map containing the specification type entity and all the values that the provided component has for this specification
//        // (In this case it will be only 1 specification type and many values (List<String>) -> only the one specification from the rule in the automatic compatibility
//        Map<SpecificationTypeEntity, List<String>> specMap = specificationsForTheSelectedComponent.stream()
//                .collect(Collectors.groupingBy(
//                        Component_SpecificationList::getSpecificationType,
//                        Collectors.mapping(Component_SpecificationList::getValue, Collectors.toList())));
//
//        //-> Check if the specification type in the map does not have attached values -> it's the data provider fault, but we can not establish the compatibility of this item,
//        //If the rule between component type A and B is assigned with specifications 1 and 2, but the component from A does not have specification 1,
//        // then A is not compatible
//        boolean hasEmptyOrMissingKey = specMap.entrySet().stream()
//                .anyMatch(entry -> entry.getKey() == null || entry.getValue() == null || entry.getValue().isEmpty());
//
//        if (hasEmptyOrMissingKey || specMap.isEmpty()) {
//            throw new CompatibilityError("There aren't any components in the current component type that satisfy the compatibility rules.");
//            //return null;
//        }
//
//        //------2. Check if any of the values are mentioned in the rules values
//        //Get the first couple from the specMap
//        Map.Entry<SpecificationTypeEntity, List<String>> firstEntryInMap = specMap.entrySet().iterator().next();
//        //Get all the values that should be considered for the searched component type for the given by the rule specification
//        // ex: in the rule_entity table there is a record specification1,specification2,value1,value2 as following (1,2,"3200","3400,4500,3450") and another one
//        // (1,2,"3400","4562,3456,5436") and for example, if the motherboard has for specification 1 values 3200 and 3400, the list below will store
//        // all values that correspond to these two specifications and that should be considered for manual compatibility (ex: "3400,4500,3450,4562,3456,5436")
//        List<String> valuesThatShouldBeConsideredForTheSearchedComponentTypeForGivenSpecification = ruleEntityRepository.findValuesOfSecondSpecificationForManualCompatibility(automaticCompatibility.getRuleId().getSpecificationToConsider1Id().getId(),automaticCompatibility.getRuleId().getSpecificationToConsider2Id().getId(),firstEntryInMap.getValue());
//        //If for none of the specifications of the current component there aren't values for the searched component, technically there isn't a rule for it,
//        //so it can skip to the next rule
//        if(valuesThatShouldBeConsideredForTheSearchedComponentTypeForGivenSpecification.isEmpty()) {
//            throw new CompatibilityError("There aren't any components in the current component type that satisfy the compatibility rules.");
//        }
//
//        //We need to flat map the values because they are in such format: List.of("value1,value2,value3", "value4,value5", "value6");
//        List<String> separatedValues = valuesThatShouldBeConsideredForTheSearchedComponentTypeForGivenSpecification.stream()
//                .flatMap(value -> List.of(value.split(",")).stream())
//                .collect(Collectors.toList());
//
//        //Make the list with values contain only unique values and no duplicates
//        List<String> uniqueValues = new ArrayList<>(
//                new HashSet<>(separatedValues)
//        );
//
//
//        //if it is the first component from the provided ones in the request and there are no compatible components (because it is the first one,
//        //then take the first ten components from the searched component type that satisfy the rule [later, they will be filtered, but now in this if statement]
//        if (indexOfProvidedComponent == 0 && compatibleComponentsSoFar.isEmpty()) {
//            //Get the specification "meant for" (most of the components have specification such as PC or Server or Workstation which helps to filter only the components for the selected type of configuration
//            Map<Long, List<String>> getTheFilteringForTheSearchedComponentType = defineValuesForComponentsFilteringBasedOnConfigurationType(typeOfConfiguration, searchedComponentTypeId);
//            Page<ComponentEntity> page = null;
//            //If it is empty (in case fo video card and dvd because they do not have such specifications), just get eleven components from the searched component type based on the page number
//            if (getTheFilteringForTheSearchedComponentType == null || getTheFilteringForTheSearchedComponentType.isEmpty()) {
//                //The code below gets all components that are part of a searched component type (id) and own a given specification type (id) and have a value for this specification that is part of the given list of unique values from the rule
//                page = componentRepository.findComponentsByTypeAndSpecification(searchedComponentTypeId, specificationForTheSearchedComponents.getId(), uniqueValues, pageable);
//
//            }
//            //If it is not null, consider the configuration type (ex: if the configuration is for PC, only components within the searched component type that are meant for PC should be retrieved)
//            else {
//                //Get 11 components from the searched category based on the pageable (page num and size) and the configuration type. We need eleven in order to know if there is at least one more component for the next page
//                Map.Entry<Long, List<String>> firstEntry = getTheFilteringForTheSearchedComponentType.entrySet().iterator().next();
//                page = componentRepository.findComponentsByTypeAndSpecificationsAndPurpose(searchedComponentTypeId, specificationForTheSearchedComponents.getId(), uniqueValues, firstEntry.getKey(), firstEntry.getValue(), pageable);
//            }
//            //The code below gets all components that are part of a searched component type (id) and own a given specification type (id) and have a value for this specification that is part of the given list of values
//            //Page<ComponentEntity> page = componentRepository.findComponentsByTypeAndSpecification(searchedComponentTypeId, specificationForTheSearchedComponents.getId(), specMap.entrySet().iterator().next().getValue(), pageable);
//            List<ComponentEntity> allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification = page.getContent();
//
//            if (allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification.isEmpty()) {
//                throw new ObjectNotFound("Compatible components from searched component type were not found");
//            }
//            if (allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification.size() < 11) {
//                thereIsNextPage = false;
//            }
//            //if(allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification.size() < 10)
//            compatibleComponentsSoFar.addAll(allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification);
//        } else {
//
//            //We need a copy of the list, because we need it to loop trough it, otherwise if we loop trought the original 'compatibleComponentsSoFar' list and try to remove items
//            //while looping, it will give us an error
//            List<ComponentEntity> copyOfCompatibleComponents = new ArrayList<>(compatibleComponentsSoFar);
//            //Filter the compatible components that we have so far based on the new rules from the loop
//            for (ComponentEntity componentEntity : copyOfCompatibleComponents) {
//                //Get the values of the componentEntity (from the loop) for the specificationThatShouldBeConsideredFromTheSearchedType
//                // + If there are no value/s -> the combination component_id <-> specification_that_should_be_considered_from_the_searched_component_type ( that is
//                //read by the rule ) this means that the component does not have this specification which automatically means not compatible
//                // + If there are values, then we have to check if these values are the same as the ones from the specification map that is above
//                List<String> allTheValuesTheComponentHasForTheConsideredSpecification = componentSpecificationListRepository.findValuesByComponentAndSpecification(componentEntity.getComponentId(), specificationForTheSearchedComponents.getId());
//                //if there are no values -> this means component is not compatible
//                if (allTheValuesTheComponentHasForTheConsideredSpecification.isEmpty()) {
//                    compatibleComponentsSoFar.remove(componentEntity);
//                    continue;
//                }
//
//                //Check if any of the values for the searched component type for the current specification that are defined by the rule, can be found in the values of the current component for the same specification
//                // + If yes, this means they are compatible
//                // + If not, this means they are not compatible
//                boolean matchFound = allTheValuesTheComponentHasForTheConsideredSpecification
//                        .stream()
//                        .anyMatch(uniqueValues::contains);
//                if (!matchFound) {
//                    compatibleComponentsSoFar.remove(componentEntity);
//                    continue;
//                }
//
//            }
//        }
//        return new FilterComponentsResult(new ArrayList<>(compatibleComponentsSoFar), thereIsNextPage);
//    }
//
//    private FilterComponentsResult handleAutomaticCompatibilityBetweenSpecifications(AutomaticCompatibilityEntity automaticCompatibility,Long providedComponentId,Long providedComponentComponentTypeId,Long searchedComponentTypeId,Integer indexOfProvidedComponent,List<ComponentEntity> compatibleComponentsSoFar, Pageable pageable,Boolean thereIsNextPage,String typeOfConfiguration)
//    {
//        List<SpecificationTypeEntity> allSpecificationForTheFirstComponent = new ArrayList<>();
//        List<SpecificationTypeEntity> allSpecificationForTheSearchedComponents = new ArrayList<>();
//        Map<SpecificationTypeEntity, List<String>> allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide = new HashMap<>();
//        SpecificationTypeEntity specificationForTheMainComponent;
//        SpecificationTypeEntity specificationForTheSearchedComponents;
//
//        //The compatibility table works with couples <component_type_1,component_type_2>
//        //This case has two scenarios <component_type_1,component_type_2> and <component_type_2,component_type_1>
//
//        //Scenario <provided_component_type,searched_component_type>
//        if (automaticCompatibility.getComponent1Id().getId() == providedComponentComponentTypeId) {
//            //Find the specifications to consider for each rule
//            specificationForTheMainComponent = automaticCompatibility.getRuleId().getSpecificationToConsider1Id().getSpecificationType();
//            allSpecificationForTheFirstComponent.add(specificationForTheMainComponent);
//
//            specificationForTheSearchedComponents = automaticCompatibility.getRuleId().getSpecificationToConsider2Id().getSpecificationType();
//            allSpecificationForTheSearchedComponents.add(specificationForTheSearchedComponents);
//        }
//        //Scenario <searched_component_type,provided_component_type>
//        else {
//            specificationForTheMainComponent = automaticCompatibility.getRuleId().getSpecificationToConsider2Id().getSpecificationType();
//            allSpecificationForTheFirstComponent.add(specificationForTheMainComponent);
//
//            specificationForTheSearchedComponents = automaticCompatibility.getRuleId().getSpecificationToConsider1Id().getSpecificationType();
//            allSpecificationForTheSearchedComponents.add(specificationForTheSearchedComponents);
//        }
//
//        //Get all specifications records for the selected component and specification_type
//        List<Component_SpecificationList> specificationsForTheSelectedComponent = componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(providedComponentId, specificationForTheMainComponent);
//
//        //A map containing the specification type entity and all the values that the provided component has for this specification
//        // (In this case it will be only 1 specification type and many values (List<String>) -> only the one specification from the rule in the automatic compatibility
//        Map<SpecificationTypeEntity, List<String>> specMap = specificationsForTheSelectedComponent.stream()
//                .collect(Collectors.groupingBy(
//                        Component_SpecificationList::getSpecificationType,
//                        Collectors.mapping(Component_SpecificationList::getValue, Collectors.toList())));
//
//        //Check if there is a specification type in the map that does not have attached values it's the data provider fault, but we can not establish the compatibility of this item,
//        // so we neglect it and move to the next ones (agreement with client) if A and B have compatibility rule with specification C and D AND E and F, but A has only C and no D,
//        //we should consider only D
//        boolean hasEmptyOrMissingKey = specMap.entrySet().stream()
//                .anyMatch(entry -> entry.getKey() == null || entry.getValue() == null || entry.getValue().isEmpty());
//
//        if (hasEmptyOrMissingKey || specMap.isEmpty()) {
//            throw new CompatibilityError("There aren't any components in the current component type that satisfy the compatibility rules.");
//        }
//
//        //Add the specification type that should be considered for the searched component type and the values that should relate to this specification type, in order
//        //to be compatible with the selected component
//        allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.put(specificationForTheSearchedComponents, specMap.entrySet().iterator().next().getValue());

//        //if it is the first component from the provided ones in the request and there are no compatible components (because it is the first one,
//        //then take the first ten components from the searched component type that satisfy the rule [later, they will be filtered, but now in this if statement]
//        if (indexOfProvidedComponent == 0 && compatibleComponentsSoFar.isEmpty()) {
//            //Get the specification "meant for" (most of the components have specification such as PC or Server or Workstation which helps to filter only the components for the selected type of configuration
//            Map<Long, List<String>> getTheFilteringForTheSearchedComponentType = defineValuesForComponentsFilteringBasedOnConfigurationType(typeOfConfiguration, searchedComponentTypeId);
//            Page<ComponentEntity> page = null;
//            //If it is empty (in case fo video card and dvd because they do not have such specifications), just get eleven components from the searched component type based on the page number
//            if (getTheFilteringForTheSearchedComponentType == null || getTheFilteringForTheSearchedComponentType.isEmpty()) {
//                //The code below gets all components that are part of a searched component type (id) and own a given specification type (id) and have a value for this specification that is part of the given list of values
//                page = componentRepository.findComponentsByTypeAndSpecification(searchedComponentTypeId, specificationForTheSearchedComponents.getId(), specMap.entrySet().iterator().next().getValue(), pageable);
//
//            }
//            //If it is not null, consider the configuration type (ex: if the configuration is for PC, only components within the searched component type that are meant for PC should be retrieved)
//            else {
//                //Get 11 components from the searched category based on the pageable (page num and size) and the configuration type. We need eleven in order to know if there is at least one more component for the next page
//                Map.Entry<Long, List<String>> firstEntry = getTheFilteringForTheSearchedComponentType.entrySet().iterator().next();
//                page = componentRepository.findComponentsByTypeAndSpecificationsAndPurpose(searchedComponentTypeId, specificationForTheSearchedComponents.getId(), specMap.entrySet().iterator().next().getValue(), firstEntry.getKey(), firstEntry.getValue(), pageable);
//            }
//            //The code below gets all components that are part of a searched component type (id) and own a given specification type (id) and have a value for this specification that is part of the given list of values
//            //Page<ComponentEntity> page = componentRepository.findComponentsByTypeAndSpecification(searchedComponentTypeId, specificationForTheSearchedComponents.getId(), specMap.entrySet().iterator().next().getValue(), pageable);
//            List<ComponentEntity> allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification = page.getContent();
//
//            if (allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification.isEmpty()) {
//                throw new ObjectNotFound("Compatible components from searched component type were not found");
//            }
//            if (allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification.size() < 11) {
//                thereIsNextPage = false;
//            }
//            //if(allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification.size() < 10)
//            compatibleComponentsSoFar.addAll(allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification);
//
//        } else {
//
//            //We need a copy of the list, because we need it to loop trough it, otherwise if we loop trought the original 'compatibleComponentsSoFar' list and try to remove items
//            //while looping, it will give us an error
//            List<ComponentEntity> copyOfCompatibleComponents = new ArrayList<>(compatibleComponentsSoFar);
//            //Filter the compatible components that we have so far based on the new rules from the loop
//            for (ComponentEntity componentEntity : copyOfCompatibleComponents) {
//                //Get the values of the componentEntity (from the loop) for the specificationThatShouldBeConsideredFromTheSearchedType
//                // + If there are no value/s -> the combination component_id <-> specification_that_should_be_considered_from_the_searched_component_type ( that is
//                //read by the rule ) this means that the component does not have this specification which automatically means not compatible
//                // + If there are values, then we have to check if these values are the same as the ones from the specification map that is above
//                List<String> allTheValuesTheComponentHasForTheConsideredSpecification = componentSpecificationListRepository.findValuesByComponentAndSpecification(componentEntity.getComponentId(), specificationForTheSearchedComponents.getId());
//                //if there are no values -> this means component is not compatible
//                if (allTheValuesTheComponentHasForTheConsideredSpecification.isEmpty()) {
//                    compatibleComponentsSoFar.remove(componentEntity);
//                    continue;
//                }
//                //Check if any of the specification values of the component (JACKIE) match any of the specification values of the current component(from the loop)
//                // + If yes, this means they are compatible
//                // + If not, this means they are not compatible
//                boolean matchFound = specMap.entrySet().stream()
//                        .anyMatch(entry -> {
//                            // Get the list of values from the map for the current main component for the specification defined by the rule
//                            List<String> valuesForTheSpecificationFromTheRuleForTheMainComponent = entry.getValue();
//
//                            // Check if any value in the list overlaps with the provided list
//                            return valuesForTheSpecificationFromTheRuleForTheMainComponent.stream().anyMatch(allTheValuesTheComponentHasForTheConsideredSpecification::contains);
//                        });
//                if (!matchFound) {
//                    compatibleComponentsSoFar.remove(componentEntity);
//                    continue;
//                }
//
//            }
//        }
//        return new FilterComponentsResult(new ArrayList<>(compatibleComponentsSoFar), thereIsNextPage);
//    }
//
//    public boolean isManualCompatibility(AutomaticCompatibilityEntity automaticCompatibilityEntity) {
//        return  automaticCompatibilityEntity.getRuleId().getValueOfFirstSpecification() != null || automaticCompatibilityEntity.getRuleId().getValueOfSecondSpecification() != null;
//    }
//
//    private FilterComponentsResult filterComponentsBasedOnRule(List<AutomaticCompatibilityEntity> allAutomaticCompatibilityRulesBetweenTwoComponentTypes,Long providedComponentId,Long providedComponentComponentTypeId,Long searchedComponentTypeId,Integer indexOfProvidedComponent,List<ComponentEntity> compatibleComponentsSoFar, Pageable pageable,Boolean thereIsNextPage,String typeOfConfiguration)
//    {
//            FilterComponentsResult filteredResults = null;
//            for (AutomaticCompatibilityEntity automaticCompatibility : allAutomaticCompatibilityRulesBetweenTwoComponentTypes) {
//                boolean isManualCompatibility = isManualCompatibility(automaticCompatibility);
//                if (isManualCompatibility) {
//                    filteredResults = handleManualCompatibilityBetweenSpecifications(automaticCompatibility, providedComponentId, searchedComponentTypeId, indexOfProvidedComponent, compatibleComponentsSoFar, pageable, thereIsNextPage, typeOfConfiguration);
//                    thereIsNextPage = filteredResults.getThereIsNextPage();
//                    compatibleComponentsSoFar = filteredResults.getComponents();
//                } else {
//                    filteredResults = handleAutomaticCompatibilityBetweenSpecifications(automaticCompatibility, providedComponentId, providedComponentComponentTypeId, searchedComponentTypeId, indexOfProvidedComponent, compatibleComponentsSoFar, pageable, thereIsNextPage, typeOfConfiguration);
//                    thereIsNextPage = filteredResults.getThereIsNextPage();
//                    compatibleComponentsSoFar = filteredResults.getComponents();
//                }
//            }
//
//        return new FilterComponentsResult(new ArrayList<>(compatibleComponentsSoFar), thereIsNextPage);
//    }
//
//    private List<GetAutomaticCompatibilityResponse> buildResponse(List<ComponentEntity> componentsEntities,Boolean thereIsNextPage) {
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
//                    .thereIsNextPage(thereIsNextPage)
//                    .build());
//        }
//        return allComponentsForResponse;
//    }
//    private  Map<SpecificationType, List<String>>  getComponentSpecification(Long componentId)
//    {
//
//        Map<SpecificationTypeEntity, List<String>> dictionaryWithTheSpecificationAndAllValuesForComponent = new HashMap<>();
//        List<Component_SpecificationList> allSpecificationsForComponent = componentSpecificationListRepository.findByComponentId(componentId);
//
//        //Loop over all the specification of a component type THIS IS NEEDED ONLY FOR THE CONVERTER
//        for (Component_SpecificationList specificationList : allSpecificationsForComponent) {
//            SpecificationTypeEntity specType = specificationList.getSpecificationType();
//            String value = specificationList.getValue();
//
//            //Check if there are already any values for the specification type in the dictionary
//            List<String> valuesList = dictionaryWithTheSpecificationAndAllValuesForComponent.get(specType);
//
//            //If there aren't, then add the specification type and the list of values (one by one) to the dictionary
//            if (valuesList == null) {
//                valuesList = new ArrayList<>();
//                valuesList.add(value);
//                dictionaryWithTheSpecificationAndAllValuesForComponent.put(specType, valuesList);
//
//            }
//            //otherwise just add the value
//            else {
//                valuesList.add(value);
//            }
//        }
//        Map<SpecificationType, List<String>> baseMap = new HashMap<>();
//
//        for (Map.Entry<SpecificationTypeEntity, List<String>> entry : dictionaryWithTheSpecificationAndAllValuesForComponent.entrySet()) {
//            SpecificationTypeEntity entityKey = entry.getKey();
//            List<String> values = entry.getValue();
//            SpecificationType baseKey = SpecificationTypeConverter.convertFromEntityToBase(entityKey);
//            baseMap.put(baseKey, values);
//        }
//        return baseMap;
//
//    }
//
//
//    private Map<Long,List<String>> defineValuesForComponentsFilteringBasedOnConfigurationType(String configurationType, Long componentTypeId)
//    {
//        //Component voor - 1070
//        //Bedoel voor - 947
//        //Soort - 954
//        Map<Long, List<String>> serverConfig = new HashMap<>();
//        if(componentTypeId == 1)
//        {
//            switch(configurationType)
//            {
//                case "Server":
//                    serverConfig.put(1070L, List.of("Server"));
//                    break;
//                case "PC":
//                    serverConfig.put(1070L, List.of("Workstation"));
//                    break;
//                case "Workstation":
//                    serverConfig.put(1070L, List.of("Workstation"));
//                    break;
//            }
//            return serverConfig;
//        }
//        else if(componentTypeId == 2)
//        {
//            switch(configurationType)
//            {
//                case "Server":
//                    serverConfig.put(1070L, List.of("Server"));
//                    break;
//                case "PC":
//                    serverConfig.put(1070L, List.of("PC"));
//                    break;
//                case "Workstation":
//                    serverConfig.put(1070L, List.of("Workstation"));
//                    break;
//            }
//            return serverConfig;
//        }
//        else if(componentTypeId == 4)
//        {
//            switch(configurationType)
//            {
//                case "Server":
//                    serverConfig.put(1070L, List.of("Server"));
//                    break;
//                case "PC":
//                    serverConfig.put(1070L, List.of("PC"));
//                    break;
//                case "Workstation":
//                    serverConfig.put(1070L, List.of("Workstation"));
//                    break;
//                case "Laptop":
//                    serverConfig.put(1070L, List.of("Notebook"));
//                    break;
//            }
//            return serverConfig;
//        }
//        else if(componentTypeId == 5)
//        {
//            switch(configurationType)
//            {
//                case "Server":
//                    serverConfig.put(947L, List.of("Server","server"));
//                    break;
//                case "PC":
//                    serverConfig.put(947L, List.of("PC"));
//                    break;
//                case "Workstation":
//                    serverConfig.put(947L, List.of("PC"));
//                    break;
//            }
//            return serverConfig;
//        }
//        else if(componentTypeId == 6)
//        {
//            switch(configurationType)
//            {
//                case "PC":
//                    serverConfig.put(954L, List.of("PC"));
//                    break;
//            }
//            return serverConfig;
//        }
//        else if(componentTypeId == 7)
//        {
//            switch(configurationType)
//            {
//                case "Server":
//                    serverConfig.put(954L, List.of("Fan","Fan module"));
//                    break;
//                case "PC":
//                    serverConfig.put(954L, List.of("Liquid cooling kit","Heatsink","Radiatior","Air cooler","Radiator block","Cooler","All-in-one liquid cooler","Cooler"));
//                    break;
//                case "Laptop":
//                    serverConfig.put(954L, List.of("Thermal paste"));
//                    break;
//            }
//            return serverConfig;
//        }
//        else if(componentTypeId == 8)
//        {
//            switch(configurationType)
//            {
//                case "Server":
//                    serverConfig.put(954L, List.of("Fan","Fan tray","Cooler"));
//                    break;
//                case "PC":
//                    serverConfig.put(954L, List.of("Liquid cooling kit","Heatsink","Radiatior","Air cooler","Radiator block","Cooler","All-in-one liquid cooler"));
//                    break;
//                case "Laptop":
//                    serverConfig.put(954L, List.of("Thermal paste"));
//                    break;
//            }
//            return serverConfig;
//        }
//        else if(componentTypeId == 10)
//        {
//            switch(configurationType)
//            {
//                case "Server":
//                    serverConfig.put(1070L, List.of("Server"));
//                    break;
//                case "Workstation":
//                    serverConfig.put(1070L, List.of("Workstation"));
//                    break;
//                case "PC":
//                    serverConfig.put(1070L, List.of("PC"));
//                    break;
//                case "Laptop":
//                    serverConfig.put(1070L, List.of("Notebook"));
//                    break;
//            }
//            return serverConfig;
//        }
//        else if(componentTypeId == 11)
//        {
//            switch(configurationType)
//            {
//                case "Server":
//                    serverConfig.put(1070L, List.of("Server"));
//                    break;
//                case "Workstation":
//                    serverConfig.put(1070L, List.of("Workstation","workstation"));
//                    break;
//                case "PC":
//                    serverConfig.put(1070L, List.of("PC"));
//                    break;
//            }
//            return serverConfig;
//        }
//        else {
//            return null;
//        }
//    }
//}
//

//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------

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
//import nl.fontys.s3.copacoproject.domain.*;
//import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
//import nl.fontys.s3.copacoproject.persistence.ComponentSpecificationListRepository;
//import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
//import nl.fontys.s3.copacoproject.persistence.SpecificationTypeComponentTypeRepository;
//import nl.fontys.s3.copacoproject.persistence.entity.*;
//import nl.fontys.s3.copacoproject.persistence.entity.supportingEntities.SpecificationTypeAndValuesForIt;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@Service
//@RequiredArgsConstructor
//public class CompatibilityBetweenComponentsImpl implements CompatibilityBetweenComponents {
//    private final AutomaticCompatibilityRepository automaticCompatibilityRepository;
//    private final ComponentRepository componentRepository;
//    private final ComponentTypeRepository componentTypeRepository;
//    private final ComponentSpecificationListRepository componentSpecificationListRepository;
//    private final RuleEntityRepository ruleEntityRepository;
//    private final SpecificationTypeComponentTypeRepository specificationTypeComponentTypeRepository;
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
//    @Override
//    public List<GetAutomaticCompatibilityResponse> automaticCompatibility(GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest request)
//    {
//        Boolean thereIsNextPage = true;
//        Pageable pageable = PageRequest.of(request.getPageNumber(), 11);
//        String typeOfConfiguration = request.getTypeOfConfiguration();
//
//        long startTime = System.nanoTime();
//        List<ComponentEntity> filteredComponentsSoFar = new ArrayList<>();
//        List<ComponentEntity> filteredComponentsAfterLooping = new ArrayList<>();
//        try {
//            //JACKIE -> each component that has been chosen
//            //ROCKIE -> searched component type
//            List<Long> notNullIds = checkIfGivenIdsExistInDatabase(request);
//            //List<ComponentEntity> compatibleComponentsEntity = new ArrayList<>();
//            boolean searchedComponentTypeExists = componentTypeRepository.existsById(request.getSearchedComponentTypeId());
//            if (!searchedComponentTypeExists) {
//                throw new ObjectNotFound("Component type not found");
//            }
//            int loopCounter = 0;
//            while (filteredComponentsAfterLooping.size() < 10 && thereIsNextPage) {
//                loopCounter = 0;
//                //Loop over the given selected component ids (those that are already selected by the user) (all JACKIES)
//                for (Long componentId : notNullIds) {
//
//                    //Get the component type of the current component id from the loop
//                    Long componentTypeIdOfProvidedComponent = componentRepository.findComponentTypeIdByComponentId(componentId);
//                    if (Objects.equals(componentTypeIdOfProvidedComponent, request.getSearchedComponentTypeId())) {
//                        throw new CompatibilityError("Once a component is selected, other components from the same category can not be searched.");
//                    }
//                    //Get all distinct specification ids(from the rules table) that should be considered between the current component type and the searched component type
//                    List<Long> allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes = automaticCompatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeIdOfProvidedComponent, request.getSearchedComponentTypeId(),typeOfConfiguration);
//
//
//                    if (allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes.isEmpty()) {
//                        //If there are no compatibility rules, and it is the first component from the request, get the first ten components from the searched category and consider them as compatible (since there is no rule)
//                        if (notNullIds.indexOf(componentId) == 0) {
//
//
//                            List<ComponentEntity> elevenComponentsFromTheSearchedComponentType = new ArrayList<>();
//
//                            //Get the specification "meant for"[purpose] (most of the components have specification such as PC or Server or Workstation which helps to filter only the components for the selected type of configuration
//                            Map<Long,List<String>> getTheFilteringForTheSearchedComponentType = defineValuesForComponentsFilteringBasedOnConfigurationType(typeOfConfiguration,request.getSearchedComponentTypeId());
//                            //If it is empty (in case fo video card and dvd because they do not have such specifications), just get eleven components from the searched component type based on the page number
//                            if (getTheFilteringForTheSearchedComponentType == null || getTheFilteringForTheSearchedComponentType.isEmpty())
//                            {
//                                //Get 11 components from the searched category based on the pageable (page num and size). We need eleven in order to know if there is at least one more component for the next page
//                                elevenComponentsFromTheSearchedComponentType = componentRepository.findByComponentType_Id(request.getSearchedComponentTypeId(), pageable);
//                            }
//                            //If it is not null, consider the configuration type (ex: if the configuration is for PC, only components within the searched component type that are meant for PC should be retrieved)
//                            else
//                            {
//                                //Get 11 components from the searched category based on the pageable (page num and size) and the configuration type. We need eleven in order to know if there is at least one more component for the next page
//                                Map.Entry<Long, List<String>> firstEntry = getTheFilteringForTheSearchedComponentType.entrySet().iterator().next();
//                                elevenComponentsFromTheSearchedComponentType = componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(request.getSearchedComponentTypeId(),firstEntry.getKey(),firstEntry.getValue(),pageable);
//                            }
//                            //If there is a map returned - use it
//                            //if not get all component without filtering (case: videocard [only for PCs and Workstations] and dvd)
//
//                            //Get 11 components from the searched category based on the pageable (page num and size). We need eleven in order to know if there is at least one more component for the next page
//                            //List<ComponentEntity> elevenComponentsFromTheSearchedComponentType = componentRepository.findByComponentType_Id(request.getSearchedComponentTypeId(), pageable);
//                            if (elevenComponentsFromTheSearchedComponentType.isEmpty()) {
//                                throw new CompatibilityError("COMPONENTS_FROM_CATEGORY_NOT_FOUND");
//                            }
//                            //If there are 11 components, this means that there is at least 1 component for the next page
//                            if (elevenComponentsFromTheSearchedComponentType.size() == 11) {
//                                thereIsNextPage = true;
//                            } else {
//                                thereIsNextPage = false;
//                            }
//                            //If it is the only component in the request, imidiatelly return the first ten components from the searched category
//                            if (notNullIds.size() == 1) {
//                                //return only the first ten
//                                return buildResponse(elevenComponentsFromTheSearchedComponentType.stream().limit(10).collect(Collectors.toList()),thereIsNextPage);
//                            }
//                            //if it is not the only component in the request, add the ten component from the searched category to the filtered components so far, and move further
//                            filteredComponentsSoFar.addAll(elevenComponentsFromTheSearchedComponentType);
//                            continue;
//
//                        }
//                        //If there are no automatic compatibility rule, and it is the last component id, then go down to the check for number of items in the list and the return statements
//                        if (notNullIds.indexOf(componentId) == notNullIds.size() - 1) {
//                            break;
//                        }
//                        continue;
//                    }
//
//                    //If it is the first component -> consider the rules and get the compatible component (based on the rule) from the searched component type
//                    //If it is NOT the first component -> consider the rules and filter the compatible components that we have so far
//                    FilterComponentsResult filteredComponents = filterComponentsBasedOnRule(allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes, componentId, componentTypeIdOfProvidedComponent, request.getSearchedComponentTypeId(), notNullIds.indexOf(componentId), filteredComponentsSoFar,pageable,thereIsNextPage,typeOfConfiguration);
//
//                    filteredComponentsSoFar.clear();
//                    filteredComponentsSoFar.addAll(filteredComponents.getComponents());
//                    thereIsNextPage = filteredComponents.getThereIsNextPage();
//
//                    if (filteredComponents.getComponents().isEmpty()) {
//                        break;
//                    }
//                    //loopCounter = loopCounter + 1;
//                    loopCounter++;
//
//                }
//                if(loopCounter == notNullIds.size()) {
//                    filteredComponentsAfterLooping.addAll(filteredComponentsSoFar);
//                    filteredComponentsSoFar.clear();
//                }
//
//                pageable = PageRequest.of(pageable.getPageNumber() + 1, 11);
//            }
//        }finally {
//            long endTime = System.nanoTime();
//            long durationInMillis = (endTime - startTime) / 1_000_000;
//            System.out.println("Method execution time: " + durationInMillis + " ms");
//        }
//        return buildResponse(filteredComponentsAfterLooping.stream().limit(10).collect(Collectors.toList()),thereIsNextPage);
//    }
//
//
//
//
//    private FilterComponentsResult handleManualCompatibilityBetweenSpecifications(Long searchedComponentTypeId,List<ComponentEntity> compatibleComponentsSoFar,SpecificationTypeAndValuesForIt specificationIdAndValuesToBeConsideredForSearchedComponentType) {
//
//        //We need a copy of the list, because we need it to loop trough it, otherwise if we loop trought the original 'compatibleComponentsSoFar' list and try to remove items
//        //while looping, it will give us an error
//        List<ComponentEntity> copyOfCompatibleComponents = new ArrayList<>(compatibleComponentsSoFar);
//        //Filter the compatible components that we have so far based on the new rules from the loop
//        for (ComponentEntity componentEntity : copyOfCompatibleComponents) {
//
//            //Loop all the specifications that should be satisfied by the component and check if it satisfies them
//                //The specificationTypeValuesForIt object contains the id of the relation between a component type and a specification type, so we need to get the specification type
//                Long specificationTypeId = specificationTypeComponentTypeRepository.findSpecificationTypeIdByComponentTypeIdAndComponentTypeSpecificationRelationId(searchedComponentTypeId,specificationIdAndValuesToBeConsideredForSearchedComponentType.getSpecification2Id());
//                List<String> valuesList = Arrays.asList(specificationIdAndValuesToBeConsideredForSearchedComponentType.getValueOfSecondSpecification().split("\\s*,\\s*"));
//
//                boolean componentSatisfiesRule = componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(componentEntity.getComponentId(),specificationTypeId,valuesList);
//                if(!componentSatisfiesRule)
//                {
//                    compatibleComponentsSoFar.remove(componentEntity);
//                    //continue;
//                }
//            //}
//        }
//
//        return new FilterComponentsResult(new ArrayList<>(compatibleComponentsSoFar), null);
//    }
//
//    private FilterComponentsResult handleAutomaticCompatibilityBetweenSpecifications(Long searchedComponentTypeId,List<ComponentEntity> compatibleComponentsSoFar,List<String> valuesToBeConsideredFromTheProvidedComponent,SpecificationTypeAndValuesForIt specificationToBeConsidered)
//    {
//        //We need a copy of the list, because we need it to loop trough it, otherwise if we loop trought the original 'compatibleComponentsSoFar' list and try to remove items
//        //while looping, it will give us an error
//        List<ComponentEntity> copyOfCompatibleComponents = new ArrayList<>(compatibleComponentsSoFar);
//        //Filter the compatible components that we have so far based on the new rules from the loop
//        for (ComponentEntity componentEntity : copyOfCompatibleComponents) {
//
//            Long specificationTypeId = specificationTypeComponentTypeRepository.findSpecificationTypeIdByComponentTypeIdAndComponentTypeSpecificationRelationId(searchedComponentTypeId, specificationToBeConsidered.getSpecification2Id());
//            boolean componentSatisfiesRule = componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(componentEntity.getComponentId(), specificationTypeId, valuesToBeConsideredFromTheProvidedComponent);
//            if (!componentSatisfiesRule) {
//                compatibleComponentsSoFar.remove(componentEntity);
//                continue;
//            }
//        }
//
//        return new FilterComponentsResult(new ArrayList<>(compatibleComponentsSoFar), null);
//    }
//
//    private FilterComponentsResult handleFirstOrEmptyCompatibleComponents(Long searchedComponentTypeId,Long specificationToConsider,List<String> valuesToConsider,String typeOfConfiguration,Pageable pageable,Boolean thereIsNextPage)
//    {
//            //Get the specification "meant for" (most of the components have specification such as PC or Server or Workstation which helps to filter only the components for the selected type of configuration
//            Map<Long, List<String>> getTheFilteringForTheSearchedComponentType = defineValuesForComponentsFilteringBasedOnConfigurationType(typeOfConfiguration, searchedComponentTypeId);
//            Page<ComponentEntity> page = null;
//            //If it is empty (in case fo video card and dvd because they do not have such specifications), just get eleven components from the searched component type based on the page number
//            if (getTheFilteringForTheSearchedComponentType == null || getTheFilteringForTheSearchedComponentType.isEmpty()) {
//                //The code below gets all components that are part of a searched component type (id) and own a given specification type (id) and have a value for this specification that is part of the given list of values
//                page = componentRepository.findComponentsByTypeAndSpecification(searchedComponentTypeId, specificationToConsider,valuesToConsider, pageable);
//
//            }
//            //If it is not null, consider the configuration type (ex: if the configuration is for PC, only components within the searched component type that are meant for PC should be retrieved)
//            else {
//                //Get 11 components from the searched category based on the pageable (page num and size) and the configuration type. We need eleven in order to know if there is at least one more component for the next page
//                Map.Entry<Long, List<String>> firstEntry = getTheFilteringForTheSearchedComponentType.entrySet().iterator().next(); //This is always only one entry
//                page = componentRepository.findComponentsByTypeAndSpecificationsAndPurpose(searchedComponentTypeId, specificationToConsider, valuesToConsider, firstEntry.getKey(), firstEntry.getValue(), pageable);
//            }
//            //The code below gets all components that are part of a searched component type (id) and own a given specification type (id) and have a value for this specification that is part of the given list of values
//            //Page<ComponentEntity> page = componentRepository.findComponentsByTypeAndSpecification(searchedComponentTypeId, specificationForTheSearchedComponents.getId(), specMap.entrySet().iterator().next().getValue(), pageable);
//            List<ComponentEntity> allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification = page.getContent();
//
//            //!!!! DO NOT DELETE
////            if (allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification.isEmpty()) {
////                throw new ObjectNotFound("Compatible components from searched component type were not found");
////            }
//            if (allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification.size() < 11)
//            {
//                thereIsNextPage = false;
//            }
//        return new FilterComponentsResult(new ArrayList<>(allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification), thereIsNextPage);
//    }
//
//    private FilterComponentsResult filterComponentsBasedOnRule(List<Long> allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes,Long providedComponentId,Long providedComponentComponentTypeId,Long searchedComponentTypeId,Integer indexOfProvidedComponent,List<ComponentEntity> compatibleComponentsSoFar, Pageable pageable,Boolean thereIsNextPage,String typeOfConfiguration)
//    {
//
//        boolean shouldBreak = false; //Flag so that nested loop can break also the parent loop
//        FilterComponentsResult filteredResults = null;
//        Long specificationTypeId = null;
//        for(Long specificationId : allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes){
//            //In the specificationId we are given the relation id between a component type and specification type, with this query we get the specificationType id out of the relationId
//            specificationTypeId = specificationTypeComponentTypeRepository.findSpecificationTypeIdByComponentTypeIdAndComponentTypeSpecificationRelationId(providedComponentComponentTypeId,specificationId);
//            //Get all values the provided component has for the specification
//            List<String> allSpecificationsTheProvidedComponentHasForTheSpecification = componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(providedComponentId,specificationTypeId);
//            //If there aren't any, it means that it is not compatible
//            if(allSpecificationsTheProvidedComponentHasForTheSpecification.isEmpty()){
//                throw new ObjectNotFound("Compatible components from searched component type were not found");
//            }
//            //Get a list of objects each containing a specification2_id and value_of_second_specification for the searched component type, by provided component type 1 and 2 and specificationId (relation between component type and specification) for the first component and the values (Those objects can be for both manual and automatic compatibility)
//            //* Lets assume that for specification type 1, the first component has values (DDR4-SDRAM and DDR5-SDRAM)
//            //** If we have (1,10,”DDR4-SDRAM”,”small”) and (1,10,”DDR5-SDRAM”,”big”) in the rule table in the db if we select them as two separate items of type
//            //SpecificationTypeAndValuesForIt and store them in a list, when the loop happens even if the searched components type has a specification value of
//            //“small” for specification type id 10, it will be firstly selected, but then removed, because of the next record which will be checking for “big”
//            //and since “small” is not in “big” it will say that it’s not compatible, that is why the list of SpecificationTypeAndValuesForIt detects the
//            // same ids for the second component specification (10) and get its values at once like (10-> “small, big”)
//            List<Object[]> response = automaticCompatibilityRepository.findSpecification2IdsAndValuesOfSecondSpecification(providedComponentComponentTypeId,searchedComponentTypeId,typeOfConfiguration,specificationId,allSpecificationsTheProvidedComponentHasForTheSpecification);
//            List<SpecificationTypeAndValuesForIt> specificationTypeIdsAndValuesToBeConsideredForTheSearchedComponentType = response.stream()
//                    .map(result -> new SpecificationTypeAndValuesForIt(
//                            ((Number) result[0]).longValue(),  // Cast to appropriate type
//                            (String) result[1]
//                    ))
//                    .collect(Collectors.toList());
//
//            //If the list is empty, this means that eventhough the component has the specification, the values it has for this specification is not compatible with any other specification types, which means not compatible
//            if(specificationTypeIdsAndValuesToBeConsideredForTheSearchedComponentType.isEmpty()){
//                throw new ObjectNotFound("Compatible components from searched component type were not found");
//            }
//
//            //Loop trough the list of specifications (it might be the case that one specification for component type 1 appears in an automatic and manual compatibility (ex: rule entity table: 1,2,null,null  | 1,7,"sdf","sdf,sdf,sdf")
//            for(SpecificationTypeAndValuesForIt specificationTypeAndValuesForIt : specificationTypeIdsAndValuesToBeConsideredForTheSearchedComponentType) {
//                if(compatibleComponentsSoFar.isEmpty())
//                {
//                    Long specificationTypeToConsider = specificationTypeComponentTypeRepository.findSpecificationTypeIdByComponentTypeIdAndComponentTypeSpecificationRelationId(searchedComponentTypeId, specificationTypeAndValuesForIt.getSpecification2Id());
//                    List<String> valuesToConsider = new ArrayList<>();
//                    if(specificationTypeAndValuesForIt.getValueOfSecondSpecification() != null) {
//                        valuesToConsider = Arrays.asList(specificationTypeAndValuesForIt.getValueOfSecondSpecification().split("\\s*,\\s*"));
//                    }
//                    else {
//                        valuesToConsider = allSpecificationsTheProvidedComponentHasForTheSpecification;
//                    }
//                    filteredResults = handleFirstOrEmptyCompatibleComponents(searchedComponentTypeId,specificationTypeToConsider,valuesToConsider,typeOfConfiguration,pageable,thereIsNextPage);                    compatibleComponentsSoFar = filteredResults.getComponents();
//                    compatibleComponentsSoFar = filteredResults.getComponents();
//                    thereIsNextPage = filteredResults.getThereIsNextPage();
//                }
//                else if (specificationTypeAndValuesForIt.getValueOfSecondSpecification() == null) {
//                    //Automatic Compatibility
//                    filteredResults = handleAutomaticCompatibilityBetweenSpecifications(searchedComponentTypeId, compatibleComponentsSoFar, allSpecificationsTheProvidedComponentHasForTheSpecification, specificationTypeAndValuesForIt);
//                    compatibleComponentsSoFar = filteredResults.getComponents();
//                } else {
//                    //Manual Compatibility
//                    filteredResults = handleManualCompatibilityBetweenSpecifications(searchedComponentTypeId, compatibleComponentsSoFar, specificationTypeAndValuesForIt);
//                    compatibleComponentsSoFar = filteredResults.getComponents();
//                }
//
//                if(compatibleComponentsSoFar.isEmpty()) {
//                    shouldBreak = true;
//                    return new FilterComponentsResult(new ArrayList<>(), thereIsNextPage);
//
//                    //break;
//                }
//            }
//            if (shouldBreak) {
//                break;
//            }
//        }
//        return new FilterComponentsResult(new ArrayList<>(compatibleComponentsSoFar), thereIsNextPage);
//    }
//
//    private List<GetAutomaticCompatibilityResponse> buildResponse(List<ComponentEntity> componentsEntities,Boolean thereIsNextPage) {
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
//                    .thereIsNextPage(thereIsNextPage)
//                    .build());
//        }
//        return allComponentsForResponse;
//    }
//    private  Map<SpecificationType, List<String>>  getComponentSpecification(Long componentId)
//    {
//
//        Map<SpecificationTypeEntity, List<String>> dictionaryWithTheSpecificationAndAllValuesForComponent = new HashMap<>();
//        List<Component_SpecificationList> allSpecificationsForComponent = componentSpecificationListRepository.findByComponentId(componentId);
//
//        //Loop over all the specification of a component type THIS IS NEEDED ONLY FOR THE CONVERTER
//        for (Component_SpecificationList specificationList : allSpecificationsForComponent) {
//            SpecificationTypeEntity specType = specificationList.getSpecificationType();
//            String value = specificationList.getValue();
//
//            //Check if there are already any values for the specification type in the dictionary
//            List<String> valuesList = dictionaryWithTheSpecificationAndAllValuesForComponent.get(specType);
//
//            //If there aren't, then add the specification type and the list of values (one by one) to the dictionary
//            if (valuesList == null) {
//                valuesList = new ArrayList<>();
//                valuesList.add(value);
//                dictionaryWithTheSpecificationAndAllValuesForComponent.put(specType, valuesList);
//
//            }
//            //otherwise just add the value
//            else {
//                valuesList.add(value);
//            }
//        }
//        Map<SpecificationType, List<String>> baseMap = new HashMap<>();
//
//        for (Map.Entry<SpecificationTypeEntity, List<String>> entry : dictionaryWithTheSpecificationAndAllValuesForComponent.entrySet()) {
//            SpecificationTypeEntity entityKey = entry.getKey();
//            List<String> values = entry.getValue();
//            SpecificationType baseKey = SpecificationTypeConverter.convertFromEntityToBase(entityKey);
//            baseMap.put(baseKey, values);
//        }
//        return baseMap;
//
//    }
//
//
//    private Map<Long,List<String>> defineValuesForComponentsFilteringBasedOnConfigurationType(String configurationType, Long componentTypeId)
//    {
//        //Component voor - 1070
//        //Bedoel voor - 947
//        //Soort - 954
//        Map<Long, List<String>> serverConfig = new HashMap<>();
//        if(componentTypeId == 1)
//        {
//            switch(configurationType)
//            {
//                case "Server":
//                    serverConfig.put(1070L, List.of("Server"));
//                    break;
//                case "PC":
//                    serverConfig.put(1070L, List.of("Workstation"));
//                    break;
//                case "Workstation":
//                    serverConfig.put(1070L, List.of("Workstation"));
//                    break;
//            }
//            return serverConfig;
//        }
//        else if(componentTypeId == 2)
//        {
//            switch(configurationType)
//            {
//                case "Server":
//                    serverConfig.put(1070L, List.of("Server"));
//                    break;
//                case "PC":
//                    serverConfig.put(1070L, List.of("PC"));
//                    break;
//                case "Workstation":
//                    serverConfig.put(1070L, List.of("Workstation"));
//                    break;
//            }
//            return serverConfig;
//        }
//        else if(componentTypeId == 4)
//        {
//            switch(configurationType)
//            {
//                case "Server":
//                    serverConfig.put(1070L, List.of("Server"));
//                    break;
//                case "PC":
//                    serverConfig.put(1070L, List.of("PC"));
//                    break;
//                case "Workstation":
//                    serverConfig.put(1070L, List.of("Workstation"));
//                    break;
//                case "Laptop":
//                    serverConfig.put(1070L, List.of("Notebook"));
//                    break;
//            }
//            return serverConfig;
//        }
//        else if(componentTypeId == 5)
//        {
//            switch(configurationType)
//            {
//                case "Server":
//                    serverConfig.put(947L, List.of("Server","server"));
//                    break;
//                case "PC":
//                    serverConfig.put(947L, List.of("PC"));
//                    break;
//                case "Workstation":
//                    serverConfig.put(947L, List.of("PC"));
//                    break;
//            }
//            return serverConfig;
//        }
//        else if(componentTypeId == 6)
//        {
//            switch(configurationType)
//            {
//                case "PC":
//                    serverConfig.put(954L, List.of("PC"));
//                    break;
//            }
//            return serverConfig;
//        }
//        else if(componentTypeId == 7)
//        {
//            switch(configurationType)
//            {
//                case "Server":
//                    serverConfig.put(954L, List.of("Fan","Fan module"));
//                    break;
//                case "PC":
//                    serverConfig.put(954L, List.of("Liquid cooling kit","Heatsink","Radiatior","Air cooler","Radiator block","Cooler","All-in-one liquid cooler","Cooler"));
//                    break;
//                case "Laptop":
//                    serverConfig.put(954L, List.of("Thermal paste"));
//                    break;
//            }
//            return serverConfig;
//        }
//        else if(componentTypeId == 8)
//        {
//            switch(configurationType)
//            {
//                case "Server":
//                    serverConfig.put(954L, List.of("Fan","Fan tray","Cooler"));
//                    break;
//                case "PC":
//                    serverConfig.put(954L, List.of("Liquid cooling kit","Heatsink","Radiatior","Air cooler","Radiator block","Cooler","All-in-one liquid cooler"));
//                    break;
//                case "Laptop":
//                    serverConfig.put(954L, List.of("Thermal paste"));
//                    break;
//            }
//            return serverConfig;
//        }
//        else if(componentTypeId == 10)
//        {
//            switch(configurationType)
//            {
//                case "Server":
//                    serverConfig.put(1070L, List.of("Server"));
//                    break;
//                case "Workstation":
//                    serverConfig.put(1070L, List.of("Workstation"));
//                    break;
//                case "PC":
//                    serverConfig.put(1070L, List.of("PC"));
//                    break;
//                case "Laptop":
//                    serverConfig.put(1070L, List.of("Notebook"));
//                    break;
//            }
//            return serverConfig;
//        }
//        else if(componentTypeId == 11)
//        {
//            switch(configurationType)
//            {
//                case "Server":
//                    serverConfig.put(1070L, List.of("Server"));
//                    break;
//                case "Workstation":
//                    serverConfig.put(1070L, List.of("Workstation","workstation"));
//                    break;
//                case "PC":
//                    serverConfig.put(1070L, List.of("PC"));
//                    break;
//            }
//            return serverConfig;
//        }
//        else {
//            return null;
//        }
//    }
//}




//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------------------
//Dynamic query


package nl.fontys.s3.copacoproject.business.impl;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.CompatibilityBetweenComponents;
import nl.fontys.s3.copacoproject.business.Exceptions.CompatibilityError;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.converters.SpecificationTypeConverter;
import nl.fontys.s3.copacoproject.business.dto.GetAutomaticCompatibilityResponse;
import nl.fontys.s3.copacoproject.business.dto.ConfiguratorRequest;
import nl.fontys.s3.copacoproject.domain.*;
import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentSpecificationListRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.SpecificationTypeComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.entity.*;
import nl.fontys.s3.copacoproject.persistence.entity.supportingEntities.SpecificationTypeAndValuesForIt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CompatibilityBetweenComponentsImpl implements CompatibilityBetweenComponents {
    private final CompatibilityRepository compatibilityRepository;
    private final ComponentRepository componentRepository;
    private final ComponentTypeRepository componentTypeRepository;
    private final ComponentSpecificationListRepository componentSpecificationListRepository;
    private final SpecificationTypeComponentTypeRepository specificationTypeComponentTypeRepository;

    private List<Long> checkIfGivenIdsExistInDatabase(ConfiguratorRequest request)
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
    public List<GetAutomaticCompatibilityResponse> automaticCompatibility(ConfiguratorRequest request)
    {
        boolean thereIsNextPage = true;
        Pageable pageable = PageRequest.of(request.getPageNumber(), 10);
        Pageable checkNextPageSinceComponent = PageRequest.of((request.getPageNumber()+1)*10, 1);

        String typeOfConfiguration = request.getTypeOfConfiguration();
        List<ComponentEntity> foundComponentsThatSatisfyAllFilters = new ArrayList<>();
        long startTime = System.nanoTime();
        Map<Long,List<String>> specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues = new HashMap<>();
        try {
            List<Long> notNullIds = checkIfGivenIdsExistInDatabase(request);
            boolean searchedComponentTypeExists = componentTypeRepository.existsById(request.getSearchedComponentTypeId());
            if (!searchedComponentTypeExists) {
                throw new ObjectNotFound("Component type not found");
            }

            //Handle PSU
            if(request.getSearchedComponentTypeId() == 5)
            {
                FilterComponentsResult foundPowerSupplies = handlePowerSupply(notNullIds,typeOfConfiguration,pageable,checkNextPageSinceComponent);
                List<ComponentEntity> foundPSUs = foundPowerSupplies.getComponents();

//                if(foundPowerSupplies.size() < 11)
//                {
//                    thereIsNextPage = false;
//                }
                return buildResponse(foundPSUs,foundPowerSupplies.getThereIsNextPage());
            }

                for (Long componentId : notNullIds) {

                    //Get the component type of the current component id from the loop
                    Long componentTypeIdOfProvidedComponent = componentRepository.findComponentTypeIdByComponentId(componentId);
                    if (Objects.equals(componentTypeIdOfProvidedComponent, request.getSearchedComponentTypeId())) {
                        throw new CompatibilityError("Once a component is selected, other components from the same category can not be searched.");
                    }

                    //Get all distinct specification ids(from the rules table) that should be considered between the current component type and the searched component type
                    List<Long> allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes = compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeIdOfProvidedComponent, request.getSearchedComponentTypeId(),typeOfConfiguration);

                    if (allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes.isEmpty()) {
                        if (notNullIds.indexOf(componentId) == 0) {
                            //If there are no compatibility rules and there is only one componentId provided, return first ten components from the searched component type
                            if (notNullIds.size() == 1) {
                                FilterComponentsResult foundComponents = fetchComponentsWithoutFiltering(typeOfConfiguration,request,pageable,checkNextPageSinceComponent);
                                return buildResponse(foundComponents.getComponents(),foundComponents.getThereIsNextPage());

                            }
                            continue;
                        }
                        if (notNullIds.indexOf(componentId) == notNullIds.size() - 1) {
                            //If there are no compatibility rules, there are not foundComponents until now and it is the last provided component id, return first ten components from the searched component type
                            if(foundComponentsThatSatisfyAllFilters.isEmpty())
                            {
                                FilterComponentsResult foundComponents = fetchComponentsWithoutFiltering(typeOfConfiguration,request,pageable,checkNextPageSinceComponent);
                                return buildResponse(foundComponents.getComponents(),foundComponents.getThereIsNextPage());

                                //return fetchComponentsWithoutFiltering(typeOfConfiguration,request,pageable,checkNextPageSinceComponent);
                            }
                            break;



                        }
                        continue;
                    }
                    //Get an updated map of rules that should be considered for the searched component type
                    Map<Long,List<String>> updatedIdsAndValues = addFilteringCriteriaForSearchedComponentBasedOnRuleBetweenProvidedComponentTypeAdnSearchedComponentType(allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes,componentId,componentTypeIdOfProvidedComponent,request.getSearchedComponentTypeId(),specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues,typeOfConfiguration);
                    specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues = updatedIdsAndValues;
                }
                //Build a dynamic query including all filters for searching for components within the searched component type that satisfy all rules
            Specification<ComponentEntity> spec = ComponentRepository.dynamicSpecification(
                    request.getSearchedComponentTypeId(), specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues);

                //Using the dynamic query, get the first ten components that satisfy the filters in the query
            Page<ComponentEntity> page = componentRepository.findAll(spec, pageable);
            foundComponentsThatSatisfyAllFilters = page.getContent();
            if(foundComponentsThatSatisfyAllFilters.isEmpty()){
                throw new ObjectNotFound("Compatible components from searched component type were not found");
            }
            if(foundComponentsThatSatisfyAllFilters.size() < 10)
            {
                thereIsNextPage = false;
            }
            else {
                Page<ComponentEntity> checkForNextPage = componentRepository.findAll(spec,checkNextPageSinceComponent);
                if(checkForNextPage.getContent().size() == 1)
                {
                    thereIsNextPage = true;
                }
                else {
                    thereIsNextPage = false;
                }
            }

        }finally {
            long endTime = System.nanoTime();
            long durationInMillis = (endTime - startTime) / 1_000_000;
            System.out.println("Method execution time: " + durationInMillis + " ms");
        }
        return buildResponse(foundComponentsThatSatisfyAllFilters.stream().limit(10).collect(Collectors.toList()),thereIsNextPage);
    }

    private FilterComponentsResult fetchComponentsWithoutFiltering(String typeOfConfiguration,ConfiguratorRequest request,Pageable pageable,Pageable checkNextPageSinceComponent)
    {
        boolean thereIsNextPage = false;
        List<ComponentEntity> elevenComponentsFromTheSearchedComponentType = new ArrayList<>();

        //Get the specification "meant for"[purpose] (most of the components have specification such as PC or Server or Workstation which helps to filter only the components for the selected type of configuration
        Map<Long,List<String>> getTheFilteringForTheSearchedComponentType = defineValuesForComponentsFilteringBasedOnConfigurationType(typeOfConfiguration,request.getSearchedComponentTypeId());
        //If it is empty (in case fo video card and dvd because they do not have such specifications), just get eleven components from the searched component type based on the page number
        if (getTheFilteringForTheSearchedComponentType == null || getTheFilteringForTheSearchedComponentType.isEmpty())
        {
            //Get 11 components from the searched category based on the pageable (page num and size). We need eleven in order to know if there is at least one more component for the next page
            elevenComponentsFromTheSearchedComponentType = componentRepository.findByComponentType_Id(request.getSearchedComponentTypeId(), pageable);
        }
        //If it is not null, consider the configuration type (ex: if the configuration is for PC, only components within the searched component type that are meant for PC should be retrieved)
        else
        {
            //Get 11 components from the searched category based on the pageable (page num and size) and the configuration type. We need eleven in order to know if there is at least one more component for the next page
            Map.Entry<Long, List<String>> firstEntry = getTheFilteringForTheSearchedComponentType.entrySet().iterator().next();
            elevenComponentsFromTheSearchedComponentType = componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(request.getSearchedComponentTypeId(),firstEntry.getKey(),firstEntry.getValue(),pageable);
        }
        //If there is a map returned - use it
        //if not get all component without filtering (case: videocard [only for PCs and Workstations] and dvd)

        //Get 11 components from the searched category based on the pageable (page num and size). We need eleven in order to know if there is at least one more component for the next page
        //List<ComponentEntity> elevenComponentsFromTheSearchedComponentType = componentRepository.findByComponentType_Id(request.getSearchedComponentTypeId(), pageable);
        if (elevenComponentsFromTheSearchedComponentType.isEmpty()) {
            throw new CompatibilityError("COMPONENTS_FROM_CATEGORY_NOT_FOUND");
        }
        //If there are 11 components, this means that there is at least 1 component for the next page
//        if (elevenComponentsFromTheSearchedComponentType.size() == 11) {
//            thereIsNextPage = true;
//        } else {
//            thereIsNextPage = false;
//        }
        if(elevenComponentsFromTheSearchedComponentType.size() < 10)
        {
            thereIsNextPage = false;
        }
        else {
            List<ComponentEntity> nextPageCheck = new ArrayList<>();
            if (getTheFilteringForTheSearchedComponentType == null || getTheFilteringForTheSearchedComponentType.isEmpty())
            {
                //Get 11 components from the searched category based on the pageable (page num and size). We need eleven in order to know if there is at least one more component for the next page
                nextPageCheck = componentRepository.findByComponentType_Id(request.getSearchedComponentTypeId(), checkNextPageSinceComponent);
            }
            //If it is not null, consider the configuration type (ex: if the configuration is for PC, only components within the searched component type that are meant for PC should be retrieved)
            else
            {
                //Get 11 components from the searched category based on the pageable (page num and size) and the configuration type. We need eleven in order to know if there is at least one more component for the next page
                Map.Entry<Long, List<String>> firstEntry = getTheFilteringForTheSearchedComponentType.entrySet().iterator().next();
                nextPageCheck = componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(request.getSearchedComponentTypeId(),firstEntry.getKey(),firstEntry.getValue(),checkNextPageSinceComponent);
            }
            if(nextPageCheck.size() == 1)
            {
                thereIsNextPage = true;
            }
            else {
                thereIsNextPage = false;
            }
        }

        return FilterComponentsResult.builder().components(elevenComponentsFromTheSearchedComponentType).thereIsNextPage(thereIsNextPage).build();
    }

    private FilterComponentsResult handlePowerSupply(List<Long> notNullIds,String configurationType,Pageable pageable,Pageable checkNextPageSinceComponent) {
        double totalPowerConsumption = 0;
        double gpuCpuConsumptionFor12thRail = 0;
        boolean thereIsNextPage;
        for (Long componentId : notNullIds) {
            Long componentTypeIdOfProvidedComponent = componentRepository.findComponentTypeIdByComponentId(componentId);
            Map<String,Long> idOrValueToBeConsidered = defineValuesForPowerConsumptionSpecifications(componentTypeIdOfProvidedComponent);
            Iterator<Map.Entry<String, Long>> iterator = idOrValueToBeConsidered.entrySet().iterator();
            Map.Entry<String, Long> firstEntry = iterator.next();
            //Double valueForPowerConsumption;
            Double valueForPowerConsumption;
            if(Objects.equals(firstEntry.getKey(), "id"))
            {
                valueForPowerConsumption = componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(componentId,firstEntry.getValue());
                if(valueForPowerConsumption == null)
                {
                    if(iterator.hasNext()) {
                        Map.Entry<String, Long> secondEntry = iterator.next();
                        //if (secondEntry != null) {
                            valueForPowerConsumption = componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(componentId, secondEntry.getValue());
                       // }
                    }
                }
            }
            else {
                valueForPowerConsumption = (double)idOrValueToBeConsidered.get("value");
            }
            if(valueForPowerConsumption == null)
            {
                continue;
            }
            totalPowerConsumption += valueForPowerConsumption;
            if(componentTypeIdOfProvidedComponent == 1 || componentTypeIdOfProvidedComponent == 3)
            {
                gpuCpuConsumptionFor12thRail += valueForPowerConsumption;
            }
        }
        List<ComponentEntity> foundPSUs = componentRepository.findComponentsBySpecificationsNative(totalPowerConsumption,configurationType,gpuCpuConsumptionFor12thRail,pageable);
        if(foundPSUs.isEmpty())
        {
            throw new ObjectNotFound("PSUs that can handle the power consumption were not found");
        }
        if(foundPSUs.size() < 10)
        {
            thereIsNextPage = false;
        }
        else {
            List<ComponentEntity> nextPageCheck = componentRepository.findComponentsBySpecificationsNative(totalPowerConsumption,configurationType,gpuCpuConsumptionFor12thRail,checkNextPageSinceComponent);
            if(nextPageCheck.size() == 1)
            {
                thereIsNextPage = true;
            }
            else {
                thereIsNextPage = false;
            }
        }
        return FilterComponentsResult.builder().components(foundPSUs).thereIsNextPage(thereIsNextPage).build();
    }




    private Map<Long,List<String>> addFilteringCriteriaForSearchedComponentBasedOnRuleBetweenProvidedComponentTypeAdnSearchedComponentType(List<Long> allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes,Long providedComponentId,Long providedComponentComponentTypeId,Long searchedComponentTypeId, Map<Long,List<String>> specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues,String typeOfConfiguration)
    {
        Long specificationTypeId = null;
        for(Long specificationId : allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes) {

            //Get all values the provided component has for the specification
            List<String> allSpecificationsTheProvidedComponentHasForTheSpecification = componentSpecificationListRepository.findValuesForAComponentSpecificationTypeBySpecificationTypeComponentRelationAndComponentId(specificationId,providedComponentId);

            //If there aren't any, it means that it is not compatible
            if(allSpecificationsTheProvidedComponentHasForTheSpecification.isEmpty()){
                throw new ObjectNotFound("Compatible components from searched component type were not found");
            }
            //Get a list of objects each containing a specification2_id and value_of_second_specification for the searched component type, by provided component type 1 and 2 and specificationId (relation between component type and specification) for the first component and the values (Those objects can be for both manual and automatic compatibility)
            //* Lets assume that for specification type 1, the first component has values (DDR4-SDRAM and DDR5-SDRAM)
            //** If we have (1,10,”DDR4-SDRAM”,”small”) and (1,10,”DDR5-SDRAM”,”big”) in the rule table in the db if we select them as two separate items of type
            //SpecificationTypeAndValuesForIt and store them in a list, when the loop happens even if the searched components type has a specification value of
            //“small” for specification type id 10, it will be firstly selected, but then removed, because of the next record which will be checking for “big”
            //and since “small” is not in “big” it will say that it’s not compatible, that is why the list of SpecificationTypeAndValuesForIt detects the
            // same ids for the second component specification (10) and get its values at once like (10-> “small, big”)
//            List<Object[]> response = automaticCompatibilityRepository.findSpecification2IdsAndValuesOfSecondSpecification(providedComponentComponentTypeId,searchedComponentTypeId,typeOfConfiguration,specificationId,allSpecificationsTheProvidedComponentHasForTheSpecification);
            List<Object[]> response = compatibilityRepository.findSpecification2IdsAndValuesOfSecondSpecification2(providedComponentComponentTypeId,searchedComponentTypeId,typeOfConfiguration,specificationId,allSpecificationsTheProvidedComponentHasForTheSpecification);

            List<SpecificationTypeAndValuesForIt> specificationTypeIdsAndValuesToBeConsideredForTheSearchedComponentType = response.stream()
                    .map(result -> new SpecificationTypeAndValuesForIt(
                            ((Number) result[0]).longValue(),  // Cast to appropriate type
                            (String) result[1]
                    ))
                    .collect(Collectors.toList());



            //If the list is empty, this means that eventhough the component has the specification, the values it has for this specification is not compatible with any other specification types, which means not compatible
            if(specificationTypeIdsAndValuesToBeConsideredForTheSearchedComponentType.isEmpty()){
                throw new ObjectNotFound("Compatible components from searched component type were not found");
            }
            for (SpecificationTypeAndValuesForIt specificationTypeAndValuesForIt : specificationTypeIdsAndValuesToBeConsideredForTheSearchedComponentType) {
                Long specificationIdToBeConsideredForSecondSpecification = specificationTypeAndValuesForIt.getSpecification2Id();
                String valuesToBeConsideredForTheSearchedComponent = specificationTypeAndValuesForIt.getValueOfSecondSpecification();

                List<String> newValuesToConsider;
                if (valuesToBeConsideredForTheSearchedComponent == null) {
                    // Automatic Compatibility
                    newValuesToConsider = allSpecificationsTheProvidedComponentHasForTheSpecification;
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

    private List<GetAutomaticCompatibilityResponse> buildResponse(List<ComponentEntity> componentsEntities,Boolean thereIsNextPage) {
        List<GetAutomaticCompatibilityResponse> allComponentsForResponse = new ArrayList<>();
        for (ComponentEntity componentEntity : componentsEntities) {
            allComponentsForResponse.add(GetAutomaticCompatibilityResponse.builder()
                    .componentId(componentEntity.getComponentId())
                    .componentName(componentEntity.getComponentName())
                    //.componentTypeId(componentEntity.getComponentType().getId())
                    //.componentTypeName(componentEntity.getComponentType().getComponentTypeName())
                    .componentImageUrl(componentEntity.getComponentImageUrl())
                    .price(componentEntity.getComponentPrice())
                    //.componentSpecifications(getComponentSpecification(componentEntity.getComponentId()))
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


    private Map<Long,List<String>> defineValuesForComponentsFilteringBasedOnConfigurationType(String configurationType, Long componentTypeId)
    {
        //Component voor - 1070 - Hardware
        //Bedoel voor - 947 - Hardware
        //Soort - 954 - Hardware
        //Bike type - 1792 - Hardware
        Map<Long, List<String>> serverConfig = new HashMap<>();
        if(componentTypeId == 1)
        {
            switch(configurationType)
            {
                case "Server":
                    serverConfig.put(1070L, List.of("Server"));
                    break;
                case "PC":
                    serverConfig.put(1070L, List.of("Workstation"));
                    break;
                case "Workstation":
                    serverConfig.put(1070L, List.of("Workstation"));
                    break;
            }
            return serverConfig;
        }
        else if(componentTypeId == 2)
        {
            switch(configurationType)
            {
                case "Server":
                    serverConfig.put(1070L, List.of("Server"));
                    break;
                case "PC":
                    serverConfig.put(1070L, List.of("PC"));
                    break;
                case "Workstation":
                    serverConfig.put(1070L, List.of("Workstation"));
                    break;
            }
            return serverConfig;
        }
        else if(componentTypeId == 4)
        {
            switch(configurationType)
            {
                case "Server":
                    serverConfig.put(1070L, List.of("Server"));
                    break;
                case "PC":
                    serverConfig.put(1070L, List.of("PC"));
                    break;
                case "Workstation":
                    serverConfig.put(1070L, List.of("Workstation"));
                    break;
                case "Laptop":
                    serverConfig.put(1070L, List.of("Notebook"));
                    break;
            }
            return serverConfig;
        }
        else if(componentTypeId == 5)
        {
            switch(configurationType)
            {
                case "Server":
                    serverConfig.put(947L, List.of("Server","server"));
                    break;
                case "PC":
                    serverConfig.put(947L, List.of("PC"));
                    break;
                case "Workstation":
                    serverConfig.put(947L, List.of("PC"));
                    break;
            }
            return serverConfig;
        }
        else if(componentTypeId == 6)
        {
            switch(configurationType)
            {
                case "PC":
                    serverConfig.put(954L, List.of("PC"));
                    break;
            }
            return serverConfig;
        }
        else if(componentTypeId == 7)
        {
            switch(configurationType)
            {
                case "Server":
                    serverConfig.put(954L, List.of("Fan","Fan module"));
                    break;
                case "PC":
                    serverConfig.put(954L, List.of("Liquid cooling kit","Heatsink","Radiatior","Air cooler","Radiator block","Cooler","All-in-one liquid cooler","Cooler"));
                    break;
                case "Laptop":
                    serverConfig.put(954L, List.of("Thermal paste"));
                    break;
            }
            return serverConfig;
        }
        else if(componentTypeId == 8)
        {
            switch(configurationType)
            {
                case "Server":
                    serverConfig.put(954L, List.of("Fan","Fan tray","Cooler"));
                    break;
                case "PC":
                    serverConfig.put(954L, List.of("Liquid cooling kit","Heatsink","Radiatior","Air cooler","Radiator block","Cooler","All-in-one liquid cooler"));
                    break;
                case "Laptop":
                    serverConfig.put(954L, List.of("Thermal paste"));
                    break;
            }
            return serverConfig;
        }
        else if(componentTypeId == 10)
        {
            switch(configurationType)
            {
                case "Server":
                    serverConfig.put(1070L, List.of("Server"));
                    break;
                case "Workstation":
                    serverConfig.put(1070L, List.of("Workstation"));
                    break;
                case "PC":
                    serverConfig.put(1070L, List.of("PC"));
                    break;
                case "Laptop":
                    serverConfig.put(1070L, List.of("Notebook"));
                    break;
            }
            return serverConfig;
        }
        else if(componentTypeId == 11)
        {
            switch(configurationType)
            {
                case "Server":
                    serverConfig.put(1070L, List.of("Server"));
                    break;
                case "Workstation":
                    serverConfig.put(1070L, List.of("Workstation","workstation"));
                    break;
                case "PC":
                    serverConfig.put(1070L, List.of("PC"));
                    break;
            }
            return serverConfig;
        }
        if(componentTypeId == 12)
        {
            switch(configurationType)
            {
                case "CITY BIKE":
                    serverConfig.put(1792L, List.of("CITY BIKE"));
                    break;
                case "DOWNHILL":
                    serverConfig.put(1792L, List.of("DOWNHILL"));
                    break;
            }
            return serverConfig;
        }
        if(componentTypeId == 13)
        {
            switch(configurationType)
            {
                case "CITY BIKE":
                    serverConfig.put(1792L, List.of("CITY BIKE"));
                    break;
                case "DOWNHILL":
                    serverConfig.put(1792L, List.of("DOWNHILL"));
                    break;
            }
            return serverConfig;
        }
        else {
            return null;
        }
    }


    private Map<String,Long> defineValuesForPowerConsumptionSpecifications(Long componentTypeId)
    {
        //The map should store a string (the string should be either id if the long is an id of a specification,
        // or value, if we know concrete value (ex.motherboards power consumption that does not have a specification for the power consumption, but in general it should be around 50W))
        Map<String,Long> specificationTypeId = new LinkedHashMap<>();
        if (componentTypeId == 1L) {
            specificationTypeId.put("id", 1120L);
        } else if (componentTypeId == 2L) {
            specificationTypeId.put("value", 50L);
        } else if (componentTypeId == 3L) {
            specificationTypeId.put("id", 937L);
        } else if (componentTypeId == 4L) {
            specificationTypeId.put("value", 10L);
        } else if (componentTypeId == 7L) {
            specificationTypeId.put("value", 10L);
        } else if (componentTypeId == 8L) {
            specificationTypeId.put("value", 10L);
        } else if (componentTypeId == 9L) {
            specificationTypeId.put("value", 30L);
        } else if (componentTypeId == 10L) {
            specificationTypeId.put("id", 1144L);
            specificationTypeId.put("id2", 1145L);
        } else if (componentTypeId == 11L) {
            specificationTypeId.put("id", 1144L);
            specificationTypeId.put("id2", 922L);
        } else {
            specificationTypeId.put("id", 1120L);
        }
        return specificationTypeId;
    }
}


