package nl.fontys.s3.copacoproject.business.impl;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.CompatibilityBetweenComponents;
import nl.fontys.s3.copacoproject.business.SpecificationIdsForComponentPurpose;
import nl.fontys.s3.copacoproject.business.converters.SpecificationTypeConverter;
import nl.fontys.s3.copacoproject.business.dto.ConfiguratorRequest;
import nl.fontys.s3.copacoproject.business.dto.GetAutomaticCompatibilityResponse;
import nl.fontys.s3.copacoproject.business.exception.CompatibilityError;
import nl.fontys.s3.copacoproject.business.exception.ObjectNotFound;
import nl.fontys.s3.copacoproject.domain.FilterComponentsResult;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentSpecificationListRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.entity.CompatibilityRepository;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import nl.fontys.s3.copacoproject.persistence.entity.Component_SpecificationList;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;
import nl.fontys.s3.copacoproject.persistence.entity.supportingEntities.SpecificationTypeAndValuesForIt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompatibilityBetweenComponentsImpl implements CompatibilityBetweenComponents {
    private final CompatibilityRepository compatibilityRepository;
    private final ComponentRepository componentRepository;
    private final ComponentTypeRepository componentTypeRepository;
    private final ComponentSpecificationListRepository componentSpecificationListRepository;
    private final SpecificationIdsForComponentPurpose specificationIdsForComponentPurpose;
    private static final String VALUE_KEY = "value";
    private static final String ID_KEY = "id";
    private static final String ID2_KEY = "id2";
    private static final String COMPATIBLE_COMPONENTS_NOT_FOUND = "Compatible components from searched component type were not found;";


    private Long ID_Component_Voor=1070L;
    private Long ID_Bedoel_Voor=947L;

    private Long ID_THERMAL_DESIGN_POWER=1120L;
    private Long ID_Minimum_system_power_voorraad=937L;
    private Long ID_Stroomverbruik_lezeN=1144L;
    private Long ID_Stroomverbruik_schrijven=1145L;
    private Long ID_Stroomverbruik_typisch=922L;
    private Long ID_Totaal_vermogen=1036L;
    private Long ID_Gecombineerd_vermogen=1293L;



    private List<Long> checkIfGivenIdsExistInDatabase(ConfiguratorRequest request)
    {
        List<Long> nonExistingIds = request.getComponentIds().stream()
                .filter(id -> !componentRepository.existsById(id))
                .toList();
        if (!nonExistingIds.isEmpty()) {
            throw new ObjectNotFound("Components not found: " + nonExistingIds);
        }
        return request.getComponentIds();
    }

    private void checkIfCongfigurationTypeChanged(String configurationTypeFromRequest,Long componentTypeId,Long componentId)
    {
        Map<Long,List<String>> componentPurposeAndSpecificationId = specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(configurationTypeFromRequest,componentTypeId);
        //This is for the unique cases like graphics card, where the graphics card do not have specification for their purpose, which means that they are compatible with
        // all configuration types that are in the switch statements (PC,WORKSTATION,NOTEBOOK)but not (City Bike and Downhill)
        if(componentPurposeAndSpecificationId.isEmpty())
        {
            return;
        }
        Map.Entry<Long, List<String>> firstEntry = componentPurposeAndSpecificationId.entrySet().iterator().next();
        //this query checks if the configuration type the current component is meant for is the same with the one provided in the
        //request (ex: if the first selected component is for PC and the endpoint request is corrupted and instead of PC, Server is passed as configuration type, it should throw an error)
        boolean sameConfigurationType = componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(componentId,firstEntry.getKey(),firstEntry.getValue());
        if(!sameConfigurationType)
        {
            throw new CompatibilityError("One of the selected components is meant to be used in a different configuration");
        }
    }



    @Override
    public List<GetAutomaticCompatibilityResponse> automaticCompatibility(ConfiguratorRequest request)
    {
        boolean thereIsNextPage = true;
        Pageable pageable = PageRequest.of(request.getPageNumber(), 10);
        Pageable checkNextPageSinceComponent = PageRequest.of((request.getPageNumber()+1)*10, 1);

        String typeOfConfiguration = request.getTypeOfConfiguration();
        List<ComponentEntity> foundComponentsThatSatisfyAllFilters;
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
                //This method checks if the configuration type in the request is the same as the configuration type of the current component
                checkIfCongfigurationTypeChanged(typeOfConfiguration,componentTypeIdOfProvidedComponent,componentId);
                //Get all distinct specification ids(from the rules table) that should be considered between the current component type and the searched component type
                List<Long> allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes = compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeIdOfProvidedComponent, request.getSearchedComponentTypeId(),typeOfConfiguration);

                if (allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes.isEmpty()) {
                    if (notNullIds.indexOf(componentId) == 0 && notNullIds.size() == 1) {
                        //If there are no compatibility rules and there is only one componentId provided, return first ten components from the searched component type
                        FilterComponentsResult foundComponents = fetchComponentsWithoutFiltering(typeOfConfiguration,request,pageable,checkNextPageSinceComponent);
                        return buildResponse(foundComponents.getComponents(),foundComponents.getThereIsNextPage());
                    }
                    if (notNullIds.indexOf(componentId) == notNullIds.size() - 1) {
                        //If there are no compatibility rules, there are not foundComponents until now and it is the last provided component id, return first ten components from the searched component type


                        //If there are no rules to be considered and it is the last component from the list of provided components, then get without filtering,otherwise,if there are rules
                        //and it is the last components (in this case for this last component, there aren't rules), then we should break the loop for the ids, and go to the custom query to consider the rules,
                        //and fetch the searched components that satisfy the rules
                        if(specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues.isEmpty())
                        {
                            FilterComponentsResult foundComponents = fetchComponentsWithoutFiltering(typeOfConfiguration,request,pageable,checkNextPageSinceComponent);
                            return buildResponse(foundComponents.getComponents(),foundComponents.getThereIsNextPage());
                        }
                        break;
                    }
                    continue;
                }
                //Get an updated map of rules that should be considered for the searched component type
                Map<Long,List<String>> updatedIdsAndValues = addFilteringCriteriaForSearchedComponentBasedOnRuleBetweenProvidedComponentTypeAdnSearchedComponentType(allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes,componentId,componentTypeIdOfProvidedComponent,request.getSearchedComponentTypeId(),specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues,typeOfConfiguration);
                specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues = updatedIdsAndValues;
            }
            //Add the configuration type and the corresponding specification id to the map as well as it is a specification that should be considered
            Map<Long,List<String>> componentPurposeAndSpecificationId = specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(typeOfConfiguration,request.getSearchedComponentTypeId());
            if(!componentPurposeAndSpecificationId.isEmpty()) {
                Map.Entry<Long, List<String>> firstEntry = componentPurposeAndSpecificationId.entrySet().iterator().next();
                specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues.put(firstEntry.getKey(), firstEntry.getValue());
            }
            //Build a dynamic query including all filters for searching for components within the searched component type that satisfy all rules
            Specification<ComponentEntity> spec = ComponentRepository.dynamicSpecification(
                    request.getSearchedComponentTypeId(), specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues);

            //Using the dynamic query, get the first ten components that satisfy the filters in the query
            Page<ComponentEntity> page = componentRepository.findAll(spec, pageable);
            foundComponentsThatSatisfyAllFilters = page.getContent();
            if(foundComponentsThatSatisfyAllFilters.isEmpty()){
                throw new ObjectNotFound(COMPATIBLE_COMPONENTS_NOT_FOUND);
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
        Map<Long,List<String>> getTheFilteringForTheSearchedComponentType = specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(typeOfConfiguration,request.getSearchedComponentTypeId());
        //If it is empty (in case fo video card and dvd because they do not have such specifications), just get eleven components from the searched component type based on the page number
        if (getTheFilteringForTheSearchedComponentType.isEmpty())
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
        if (elevenComponentsFromTheSearchedComponentType.isEmpty()) {
            throw new CompatibilityError("COMPONENTS_FROM_CATEGORY_NOT_FOUND");
        }
        //If there are 11 components, this means that there is at least 1 component for the next page
        if(elevenComponentsFromTheSearchedComponentType.size() >= 10)
        {
            List<ComponentEntity> nextPageCheck;
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
        //this checks if the power supply supports the selected configuration type
        specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(configurationType,5L);

        for (Long componentId : notNullIds) {
            Long componentTypeIdOfProvidedComponent = componentRepository.findComponentTypeIdByComponentId(componentId);
            //This method checks if the configuration type in the request is the same as the configuration type of the current component
            checkIfCongfigurationTypeChanged(configurationType,componentTypeIdOfProvidedComponent,componentId);

            Double valueForPowerConsumption = calculatePowerConsumption(componentId, componentTypeIdOfProvidedComponent);

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
        List<ComponentEntity> foundPSUs = componentRepository.findComponentsBySpecificationsNative(ID_Totaal_vermogen,totalPowerConsumption,ID_Bedoel_Voor,configurationType,ID_Gecombineerd_vermogen,gpuCpuConsumptionFor12thRail,pageable);
        if(foundPSUs.isEmpty())
        {
            throw new ObjectNotFound("PSUs that can handle the power consumption were not found");
        }
        if(foundPSUs.size() < 10)
        {
            thereIsNextPage = false;
        }
        else {
            List<ComponentEntity> nextPageCheck = componentRepository.findComponentsBySpecificationsNative(ID_Totaal_vermogen,totalPowerConsumption,ID_Bedoel_Voor,configurationType,ID_Gecombineerd_vermogen,gpuCpuConsumptionFor12thRail,checkNextPageSinceComponent);
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

    private Double calculatePowerConsumption(Long componentId, Long componentTypeId) {
        Map<String, Long> idOrValueToBeConsidered = defineValuesForPowerConsumptionSpecifications(componentTypeId);
        Iterator<Map.Entry<String, Long>> iterator = idOrValueToBeConsidered.entrySet().iterator();

        Map.Entry<String, Long> firstEntry = iterator.next();
        Double valueForPowerConsumption;

        if (Objects.equals(firstEntry.getKey(), ID_KEY)) {
            valueForPowerConsumption = componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(componentId, firstEntry.getValue());

            // Try second entry if the first value is null
            if (valueForPowerConsumption == null && iterator.hasNext()) {
                Map.Entry<String, Long> secondEntry = iterator.next();
                valueForPowerConsumption = componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(componentId, secondEntry.getValue());
            }
        } else {
            valueForPowerConsumption = (double) idOrValueToBeConsidered.get(VALUE_KEY);
        }

        return valueForPowerConsumption;
    }



    private Map<Long,List<String>> addFilteringCriteriaForSearchedComponentBasedOnRuleBetweenProvidedComponentTypeAdnSearchedComponentType(List<Long> allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes,Long providedComponentId,Long providedComponentComponentTypeId,Long searchedComponentTypeId, Map<Long,List<String>> specificationIdToBeConsideredForTheSearchedComponentAndCorrespondingValues,String typeOfConfiguration)
    {
        for(Long specificationId : allDistinctSpecificationsThatShouldBeConsideredBetweenTheTwoComponentTypes) {

            //Get all values the provided component has for the specification
            List<String> allSpecificationsTheProvidedComponentHasForTheSpecification = componentSpecificationListRepository.findValuesForAComponentSpecificationTypeBySpecificationTypeComponentRelationAndComponentId(specificationId,providedComponentId);

            //If there aren't any, it means that it is not compatible
            if(allSpecificationsTheProvidedComponentHasForTheSpecification.isEmpty()){
                throw new ObjectNotFound("One of the selected components does not respect any of the rules between it and the searched one;");
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
                throw new ObjectNotFound(COMPATIBLE_COMPONENTS_NOT_FOUND);
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

    private Map<String,Long> defineValuesForPowerConsumptionSpecifications(Long componentTypeId)
    {
        //The map should store a string (the string should be either id if the long is an id of a specification,
        // or value, if we know concrete value (ex.motherboards power consumption that does not have a specification for the power consumption, but in general it should be around 50W))
        Map<String,Long> specificationTypeId = new LinkedHashMap<>();
        if (componentTypeId == 1L) {
            specificationTypeId.put(ID_KEY, ID_THERMAL_DESIGN_POWER);
        } else if (componentTypeId == 2L) {
            specificationTypeId.put(VALUE_KEY, 50L);
        } else if (componentTypeId == 3L) {
            specificationTypeId.put(ID_KEY,ID_Minimum_system_power_voorraad);
        } else if (componentTypeId == 4L) {
            specificationTypeId.put(VALUE_KEY, 10L);
        } else if (componentTypeId == 7L) {
            specificationTypeId.put(VALUE_KEY, 10L);
        } else if (componentTypeId == 8L) {
            specificationTypeId.put(VALUE_KEY, 10L);
        } else if (componentTypeId == 9L) {
            specificationTypeId.put(VALUE_KEY, 30L);
        } else if (componentTypeId == 10L) {
            specificationTypeId.put(ID_KEY, ID_Stroomverbruik_lezeN);
            specificationTypeId.put(ID2_KEY, ID_Stroomverbruik_schrijven);
        } else if (componentTypeId == 11L) {
            specificationTypeId.put(ID_KEY, ID_Stroomverbruik_lezeN);
            specificationTypeId.put(ID2_KEY,ID_Stroomverbruik_typisch);
        } else {
            specificationTypeId.put(ID_KEY, ID_THERMAL_DESIGN_POWER);
        }
        return specificationTypeId;
    }
}
