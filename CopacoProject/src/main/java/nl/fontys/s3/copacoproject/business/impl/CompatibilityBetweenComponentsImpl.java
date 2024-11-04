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
        Optional<ComponentEntity> foundComponentByIdFromRequest = componentRepository.findById(componentId);
        Optional<ComponentTypeEntity> foundComponentTypeByIdFromRequest = componentTypeRepository.findById(searchedComponentsType);

        if(foundComponentByIdFromRequest.isPresent() && foundComponentTypeByIdFromRequest.isPresent()) {
            //Get all the automatic compatibility records and rules there are between the component type of the first component and the component type that is passed in the request
            List<AutomaticCompatibilityEntity> allCompatibilityRecordsBetweenTwoComponentTypes = automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypes(foundComponentByIdFromRequest.get().getComponentType(), foundComponentTypeByIdFromRequest.get());

            //Loop all the automatic compatibilities in order to find the rules and the specifications to consider for each component type
            for (AutomaticCompatibilityEntity automaticCompatibility : allCompatibilityRecordsBetweenTwoComponentTypes) {

                //Find the specifications to consider for each rule
                SpecificationTypeEntity specificationForTheMainComponent = automaticCompatibility.getRuleId().getSpecificationToConsider1Id().getSpecificationType();
                SpecificationTypeEntity specificationForTheSearchedComponents = automaticCompatibility.getRuleId().getSpecificationToConsider2Id().getSpecificationType();

                //Get all specification values for the given specification to consider for the main component
                List<Component_SpecificationList> specificationsForTheSelectedComponent = componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(foundComponentByIdFromRequest.get(), specificationForTheMainComponent);
                List<String> values = new ArrayList<>();
                //Store the values of each specification in an array
                for (Component_SpecificationList specificationForTheSelectedComponent : specificationsForTheSelectedComponent) {
                    values.add(specificationForTheSelectedComponent.getValue());
                }

                //The code below gets all components that are part of a provided component type (id) and own a given specification (id) and have a value for this specification that is part of the given list of values
                //Get all components that are part of the searched component type and as values of the specified specification for the second component type (specificationForTheSearchedComponents) have the same values as the ones of the main component
                List<ComponentEntity> allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification = componentRepository.findComponentsByTypeAndSpecification(searchedComponentsType,specificationForTheSearchedComponents.getId() , values);
                for (ComponentEntity componentEntity : allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification) {

                    //Get all specifications for the component
                    List<Component_SpecificationList> allSpecificationsForComponent = componentSpecificationListRepository.findByComponentId(componentEntity);

                    //Each component might have more than one value for one specification type ( a motherboard has many values for the clock speed compatibility)
                    Map<SpecificationTypeEntity, List<String>> dictionaryWithTheSpecificationAndAllValuesForComponent = new HashMap<>();

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
                //allComponentsBase;
            }
            return allComponentsBase;
        }
        throw new ComponentTypeNotFound("COMPONENT NOT FOUND");
    }

}

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
//        Optional<ComponentEntity> foundComponentByIdFromRequest = componentRepository.findById(componentId);
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
//
//                //The code below gets all components that are part of a provided component type (id) and own a given specification (id) and have a value for this specification that is part of the given list of values
//                //Get all components that are part of the searched component type and as values of the specified specification for the second component type (specificationForTheSearchedComponents) have the same values as the ones of the main component
//                List<ComponentEntity> allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification = componentRepository.findComponentsByTypeAndSpecification(searchedComponentsType, specificationForTheSearchedComponents.getId(), values);
//                allCompatibleComponentsBeforeFiltering.addAll(allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification);
//
//            }
//            Set<ComponentEntity> uniqueComponentEntities = new HashSet<>(allCompatibleComponentsBeforeFiltering);
//
//            for(ComponentEntity componentEntity : uniqueComponentEntities) {
//                List<Component_SpecificationList> allSpecificationsForComponent = componentSpecificationListRepository.findByComponentId(componentEntity);
//                //List<Component_SpecificationList> specificationsThat
//                for(Component_SpecificationList specificationForTheSelectedComponent : allSpecificationsForComponent) {
//                    if(allSpecificationForTheSearchedComponents.stream()
//                            .anyMatch(componentSpecification -> componentSpecification.getId() == specificationForTheSelectedComponent.getSpecificationType().getId()))
//                    {
//
//                    }
//                    for(SpecificationTypeEntity specificationTypeEntity : allSpecificationForTheSearchedComponents)
//                    {
//                        if(specificationForTheSelectedComponent.getSpecificationType().getId() == specificationTypeEntity.getId())
//                        {
//
//                        }
//
//                    }
//                    //if(specificationForTheSelectedComponent.getSpecificationType())
//                }
//            }
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
//            return null;
//        }
//        throw new ComponentTypeNotFound("COMPONENT NOT FOUND");
//    }
//
//}
