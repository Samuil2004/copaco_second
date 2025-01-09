package nl.fontys.s3.copacoproject.business.impl;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.CompatibilityBetweenComponents;
import nl.fontys.s3.copacoproject.business.exception.CompatibilityError;
import nl.fontys.s3.copacoproject.business.exception.ObjectNotFound;
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
        if(elevenComponentsFromTheSearchedComponentType.size() >= 10)
        {
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


