//package nl.fontys.s3.copacoproject.business.impl;
//
//import lombok.RequiredArgsConstructor;
//import nl.fontys.s3.copacoproject.business.CompatibilityBetweenComponents;
//import nl.fontys.s3.copacoproject.business.CompatibilityManager;
//import nl.fontys.s3.copacoproject.business.Exceptions.ComponentTypeNotFound;
//import nl.fontys.s3.copacoproject.business.converters.ComponentConverter;
//import nl.fontys.s3.copacoproject.business.dto.GetAutomaticCompatibilityByIdResponse;
//import nl.fontys.s3.copacoproject.domain.Component;
//import nl.fontys.s3.copacoproject.domain.SpecificationType;
//import nl.fontys.s3.copacoproject.domain.SpecificationType_ComponentType;
//import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
//import nl.fontys.s3.copacoproject.persistence.ComponentSpecificationListRepository;
//import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
//import nl.fontys.s3.copacoproject.persistence.entity.*;
//import org.hibernate.query.sqm.tree.expression.Compatibility;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//@RequiredArgsConstructor
//public class CompatibilityBetweenComponentsImpl implements CompatibilityBetweenComponents {
//    private final AutomaticCompatibilityRepository automaticCompatibilityRepository;
//    private final CompatibilityManager compatibilityManager;
//    private final ComponentRepository componentRepository;
//    private final ComponentTypeRepository componentTypeRepository;
//    private final ComponentSpecificationListRepository componentSpecificationListRepository;
//    @Override
//    public List<Component> automaticCompatibility(Long componentId,Long searchedComponentsType)
//    {
//        List<Component> allComponentsBase = new ArrayList<>();
//        //Identify the component Id and the component type Id that are past trough the api and find their objects
//        Optional<ComponentEntity> foundComponentByIdFromRequest = componentRepository.findByComponentId(componentId);
//        Optional<ComponentTypeEntity> foundComponentTypeByIdFromRequest = componentTypeRepository.findById(searchedComponentsType);
//
//        if(foundComponentByIdFromRequest.isPresent() && foundComponentTypeByIdFromRequest.isPresent()) {
//            //Get all the automatic compatibility records and rules there are between the component type of the first component and the component type that is passed in the request
//            List<AutomaticCompatibilityEntity> allCompatibilityRecordsBetweenTwoComponentTypes = automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypes(foundComponentByIdFromRequest.get().getComponentType(), foundComponentTypeByIdFromRequest.get());
//
//            //This map will store all specifications that should be considered from the first component side and the corresponding values
//            Map<SpecificationTypeEntity, List<String>> allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide = new HashMap<>();
//
//            //This list store all components from the selected component type that are compatible with the first component considering each rule separately (later all of them will be considered)
//            List<ComponentEntity> allCompatibleComponentsBeforeFiltering = new ArrayList<>();
//            List<SpecificationTypeEntity> allSpecificationForTheFirstComponent = new ArrayList<>();
//            List<SpecificationTypeEntity> allSpecificationForTheSearchedComponents = new ArrayList<>();
//
//            //Loop all the automatic compatibilities in order to find the rules and the specifications to consider for each component type
//            for (AutomaticCompatibilityEntity automaticCompatibility : allCompatibilityRecordsBetweenTwoComponentTypes) {
//
//                //Find the specifications to consider for each rule
//                SpecificationTypeEntity specificationForTheMainComponent = automaticCompatibility.getRuleId().getSpecificationToConsider1Id().getSpecificationType();
//                allSpecificationForTheFirstComponent.add(specificationForTheMainComponent);
//
//                SpecificationTypeEntity specificationForTheSearchedComponents = automaticCompatibility.getRuleId().getSpecificationToConsider2Id().getSpecificationType();
//                allSpecificationForTheSearchedComponents.add(specificationForTheSearchedComponents);
//                //Get all specification values for the given specification to consider for the main component
//                List<Component_SpecificationList> specificationsForTheSelectedComponent = componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(foundComponentByIdFromRequest.get(), specificationForTheMainComponent);
//                List<String> values = new ArrayList<>();
//                //Store the values of each specification in an array
//                for (Component_SpecificationList specificationForTheSelectedComponent : specificationsForTheSelectedComponent) {
//                    values.add(specificationForTheSelectedComponent.getValue());
//                }
//                //Add the specification and the values
//                allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.put(specificationForTheSearchedComponents, values);
////            }
////            for (Map.Entry<SpecificationTypeEntity, List<String>> entry : allSpecificationsThatShouldBeConsideredFromTheFirstComponentSide.entrySet()) {
////
////            }
//
//                //The code below gets all components that are part of a provided component type (id) and own a given specification (id) and have a value for this specification that is part of the given list of values
//                //Get all components that are part of the searched component type and as values of the specified specification for the second component type (specificationForTheSearchedComponents) have the same values as the ones of the main component
//                List<ComponentEntity> allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification = componentRepository.findComponentsByTypeAndSpecification(searchedComponentsType, specificationForTheSearchedComponents.getId(), values);
//                allCompatibleComponentsBeforeFiltering.addAll(allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification);
//
//            }
//            //some components are duplicated in the allCompatibleComponentsBeforeFiltering list, so now we map only the unique ones
//            Set<ComponentEntity> uniqueComponentEntities = new HashSet<>(allCompatibleComponentsBeforeFiltering);
//
//            for(ComponentEntity componentEntity : uniqueComponentEntities) {
//                List<Component_SpecificationList> allSpecificationsForComponent = componentSpecificationListRepository.findByComponentId(componentEntity);
//
//
//
//
//
//
//                boolean allSpecificationsMatch = true;
//
//                //Loop trough all the specifications for the component and see if there are any matching the those that will be considered for
//                // the compatibility and if yes to check if the values that are compatible with the first component can be found as values
//                // in the specification of the component values [ex. motherboard and ram are defined as compatible by the clock speed and the DDR values and
//                //the motherboard supports clock speed of 2000 and the ram has a clock speed of 2000, but if the motherboard supports DDR5 and the ram has
//                //DDR4 they are not compatible. this loop below goes trough all the specifications of the component type, checks which are considered in the
//                //rules for the compatibility between the two component types (all the specifications considered are mapped in the
//                // allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide map) and checks if the values that the components have are supported by the first
//                //component.
//                for (Component_SpecificationList spec : allSpecificationsForComponent) {
//                    SpecificationTypeEntity specType = spec.getSpecificationType();
//                    String specValue = spec.getValue();
//
//                    // Check if the specification type is in the map
//                    if (allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.containsKey(specType)) {
//                        // Retrieve the list of expected values for this specification type
//                        List<String> expectedValues =
//                                allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.get(specType);
//
//                        // Check if the specification value is in the list of expected values
//                        if (!expectedValues.contains(specValue)) {
//                            allSpecificationsMatch = false;
//                            //System.out.println("Value not found for specification: " + specType + " with value: " + specValue);
//                            break; // No need to check further as one mismatch was found
//                        }
//                    }
//                }
//                if(allSpecificationsMatch) {
//                    Map<SpecificationTypeEntity, List<String>> dictionaryWithTheSpecificationAndAllValuesForComponent = new HashMap<>();
//
//                    //Loop over all the specification of a component type THIS IS NEEDED ONLY FOR THE CONVERTER
//                    for (Component_SpecificationList specificationList : allSpecificationsForComponent) {
//                        SpecificationTypeEntity specType = specificationList.getSpecificationType();
//                        String value = specificationList.getValue();
//
//                        //Check if there are already any values for the specification type in the dictionary
//                        List<String> valuesList = dictionaryWithTheSpecificationAndAllValuesForComponent.get(specType);
//
//                        //If there aren't, then add the specification type and the list of values (one by one) to the dictionary
//                        if (valuesList == null) {
//                            valuesList = new ArrayList<>();
//                            valuesList.add(value);
//                            dictionaryWithTheSpecificationAndAllValuesForComponent.put(specType, valuesList);
//
//                        }
//                        //otherwise just add the value
//                        else {
//                            valuesList.add(value);
//                        }
//                    }
//                    Component componentBase = ComponentConverter.convertFromEntityToBase(componentEntity, dictionaryWithTheSpecificationAndAllValuesForComponent);
//                    allComponentsBase.add(componentBase);
//                }
//
//            }
//            return  allComponentsBase;
//
////                for (ComponentEntity componentEntity : allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification) {
////
////                    //Get all specifications for the component
////                    List<Component_SpecificationList> allSpecificationsForComponent = componentSpecificationListRepository.findByComponentId(componentEntity);
////
////                    //Each component might have more than one value for one specification type ( a motherboard has many values for the clock speed compatibility)
////                    Map<SpecificationTypeEntity, List<String>> dictionaryWithTheSpecificationAndAllValuesForComponent = new HashMap<>();
////
////                    for (Component_SpecificationList specificationList : allSpecificationsForComponent) {
////                        SpecificationTypeEntity specType = specificationList.getSpecificationType();
////                        String value = specificationList.getValue();
////
////                        //Check if there are already any values for the specification type in the dictionary
////                        List<String> valuesList = dictionaryWithTheSpecificationAndAllValuesForComponent.get(specType);
////
////                        //If there aren't, then add the specification type and the list of values (one by one) to the dictionary
////                        if (valuesList == null) {
////                            valuesList = new ArrayList<>();
////                            valuesList.add(value);
////                            dictionaryWithTheSpecificationAndAllValuesForComponent.put(specType, valuesList);
////
////                        }
////                        //otherwise just add the value
////                        else {
////                            valuesList.add(value);
////                        }
////                    }
//                    //Component componentBase = ComponentConverter.convertFromEntityToBase(componentEntity, dictionaryWithTheSpecificationAndAllValuesForComponent);
//                    //allComponentsBase.add(componentBase);
//                //}
//                //allComponentsBase;
//            //}
//            //return allComponentsBase;
//            //return null;
//        }
//        throw new ComponentTypeNotFound("COMPONENT NOT FOUND");
//    }
//
//}


package nl.fontys.s3.copacoproject.business.impl;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.CompatibilityBetweenComponents;
import nl.fontys.s3.copacoproject.business.CompatibilityManager;
import nl.fontys.s3.copacoproject.business.Exceptions.ComponentTypeNotFound;
import nl.fontys.s3.copacoproject.business.converters.ComponentConverter;
import nl.fontys.s3.copacoproject.business.dto.GetAutomaticCompatibilityByIdResponse;
import nl.fontys.s3.copacoproject.domain.Component;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import nl.fontys.s3.copacoproject.domain.SpecificationType_ComponentType;
import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentSpecificationListRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.entity.*;
import org.hibernate.query.sqm.tree.expression.Compatibility;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CompatibilityBetweenComponentsImpl implements CompatibilityBetweenComponents {
    private final AutomaticCompatibilityRepository automaticCompatibilityRepository;
    private final CompatibilityManager compatibilityManager;
    private final ComponentRepository componentRepository;
    private final ComponentTypeRepository componentTypeRepository;
    private final ComponentSpecificationListRepository componentSpecificationListRepository;
    @Override
    public List<Component> automaticCompatibility(Long componentId,Long searchedComponentsType)
    {
        List<Component> allComponentsBase = new ArrayList<>();
        //Identify the component Id and the component type Id that are past trough the api and find their objects
        Optional<ComponentEntity> foundComponentByIdFromRequest = componentRepository.findByComponentId(componentId);
        Optional<ComponentTypeEntity> foundComponentTypeByIdFromRequest = componentTypeRepository.findById(searchedComponentsType);

        if(foundComponentByIdFromRequest.isPresent() && foundComponentTypeByIdFromRequest.isPresent()) {
            //Get all the automatic compatibility records and rules there are between the component type of the first component and the component type that is passed in the request
            List<AutomaticCompatibilityEntity> allCompatibilityRecordsBetweenTwoComponentTypes = automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypes(foundComponentByIdFromRequest.get().getComponentType(), foundComponentTypeByIdFromRequest.get());

            //This map will store all specifications that should be considered from the first component side and the corresponding values
            Map<SpecificationTypeEntity, List<String>> allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide = new HashMap<>();

            //This list store all components from the selected component type that are compatible with the first component considering each rule separately (later all of them will be considered)
            List<ComponentEntity> allCompatibleComponentsBeforeFiltering = new ArrayList<>();
            List<SpecificationTypeEntity> allSpecificationForTheFirstComponent = new ArrayList<>();
            List<SpecificationTypeEntity> allSpecificationForTheSearchedComponents = new ArrayList<>();

            //Loop all the automatic compatibilities in order to find the rules and the specifications to consider for each component type
            for (AutomaticCompatibilityEntity automaticCompatibility : allCompatibilityRecordsBetweenTwoComponentTypes) {

                //Find the specifications to consider for each rule
                SpecificationTypeEntity specificationForTheMainComponent = automaticCompatibility.getRuleId().getSpecificationToConsider1Id().getSpecificationType();
                allSpecificationForTheFirstComponent.add(specificationForTheMainComponent);

                SpecificationTypeEntity specificationForTheSearchedComponents = automaticCompatibility.getRuleId().getSpecificationToConsider2Id().getSpecificationType();
                allSpecificationForTheSearchedComponents.add(specificationForTheSearchedComponents);
                //Get all specification values for the given specification to consider for the main component
                List<Component_SpecificationList> specificationsForTheSelectedComponent = componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(foundComponentByIdFromRequest.get(), specificationForTheMainComponent);
                List<String> values = new ArrayList<>();
                //Store the values of each specification in an array
                for (Component_SpecificationList specificationForTheSelectedComponent : specificationsForTheSelectedComponent) {
                    values.add(specificationForTheSelectedComponent.getValue());
                }
                //Add the specification and the values
                allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.put(specificationForTheSearchedComponents, values);
//            }
//            for (Map.Entry<SpecificationTypeEntity, List<String>> entry : allSpecificationsThatShouldBeConsideredFromTheFirstComponentSide.entrySet()) {
//
//            }

                //The code below gets all components that are part of a provided component type (id) and own a given specification (id) and have a value for this specification that is part of the given list of values
                //Get all components that are part of the searched component type and as values of the specified specification for the second component type (specificationForTheSearchedComponents) have the same values as the ones of the main component
                List<ComponentEntity> allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification = componentRepository.findComponentsByTypeAndSpecification(searchedComponentsType, specificationForTheSearchedComponents.getId(), values);
                allCompatibleComponentsBeforeFiltering.addAll(allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification);

            }
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
                    atLeastOneMatches = false;
                    if (allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.containsKey(specification)) {
                        // Retrieve the list of expected values for this specification type
                        List<String> expectedValues =
                                allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.get(specification);

                        if(!expectedValues.isEmpty()) {
                            for (String value : values) {
                                if (expectedValues.contains(value)) {
                                    atLeastOneMatches = true;
                                    break; // Found a match, no need to check further
                                }
//                            else {
//                                allSpecificationsMatch = false;
//                                break outerLoop;
//                            }
                            }
                            if (!atLeastOneMatches) {
                                break;

                            }
                        }

                    }
                }

//                if(mapOfUniqueSpecificationsForItemAndItsValues.containsKey())
//                    for (Component_SpecificationList spec : allSpecificationsForComponent) {
//                    SpecificationTypeEntity specType = spec.getSpecificationType();
//                    String specValue = spec.getValue();
//
//                    // Check if the specification type is in the map
//                    if (allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.containsKey(specType)) {
//                        // Retrieve the list of expected values for this specification type
//                        List<String> expectedValues =
//                                allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.get(specType);
//
//                        // Check if the specification value is in the list of expected values
//                        if (!expectedValues.contains(specValue)) {
//                            allSpecificationsMatch = false;
//                            //System.out.println("Value not found for specification: " + specType + " with value: " + specValue);
//                            break; // No need to check further as one mismatch was found
//                        }
//                    }
//                }
//                for (Component_SpecificationList spec : allSpecificationsForComponent) {
//                    SpecificationTypeEntity specType = spec.getSpecificationType();
//                    String specValue = spec.getValue();
//
//                    // Check if the specification type is in the map
//                    if (allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.containsKey(specType)) {
//                        // Retrieve the list of expected values for this specification type
//                        List<String> expectedValues =
//                                allSpecificationsThatShouldBeConsideredFromTheSecondComponentSide.get(specType);
//
//                        // Check if the specification value is in the list of expected values
//                        if (!expectedValues.contains(specValue)) {
//                            allSpecificationsMatch = false;
//                            //System.out.println("Value not found for specification: " + specType + " with value: " + specValue);
//                            break; // No need to check further as one mismatch was found
//                        }
//                    }
//                }
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
                        //otherwise just add the value
                        else {
                            valuesList.add(value);
                        }
                    }
                    Component componentBase = ComponentConverter.convertFromEntityToBase(componentEntity, dictionaryWithTheSpecificationAndAllValuesForComponent);
                    allComponentsBase.add(componentBase);
                }

            }
            return  allComponentsBase;

        }
        throw new ComponentTypeNotFound("COMPONENT NOT FOUND");
    }

}
