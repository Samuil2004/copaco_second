package nl.fontys.s3.copacoproject.business.impl;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.CompatibilityBetweenComponents;
import nl.fontys.s3.copacoproject.business.CompatibilityManager;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.converters.ComponentConverter;
import nl.fontys.s3.copacoproject.domain.Component;
import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentSpecificationListRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.entity.*;
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
        //Long componentId = 2L;

        //Long theComponentTypeWeAreLookingForCompatibilityId = 6L;
        List<Component> allComponentsBase = new ArrayList<>();
        Optional<ComponentEntity> foundComponent = componentRepository.findById(componentId);
        Optional<ComponentTypeEntity> foundComponentType = componentTypeRepository.findById(searchedComponentsType);
        if(foundComponent.isPresent() && foundComponentType.isPresent()) {
            List<AutomaticCompatibilityEntity> allCompatibilityRecordsBetweenTwoComponentTypes = automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypes(foundComponent.get().getComponentType(), foundComponentType.get());
            for (AutomaticCompatibilityEntity automaticCompatibility : allCompatibilityRecordsBetweenTwoComponentTypes) {
                SpecificationTypeEntity specificationForTheMainComponent = automaticCompatibility.getRuleId().getSpecificationToConsider1Id().getSpecificationType();
                SpecificationTypeEntity specificationForTheSearchedComponents = automaticCompatibility.getRuleId().getSpecificationToConsider2Id().getSpecificationType();
                //SpecificationTypeEntity specificationTypeFortTheSelectedComponentForMatching = specificationForTheMainComponent;
                List<Component_SpecificationList> specificationsForTheSelectedComponent = componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(foundComponent.get(), specificationForTheMainComponent);
                List<String> values = new ArrayList<>();
                for (Component_SpecificationList specificationForTheSelectedComponent : specificationsForTheSelectedComponent) {
                    values.add(specificationForTheSelectedComponent.getValue());
                }

                //The code below gets all components that are part of a provided component type (id) and own a given specification (id) and have a value for this specification that is part of the given list of values
//            List<ComponentEntity> allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification = componentRepository.findComponentsByTypeAndSpecification(5L, 96L, List.of("6800", "6600", "5600"));
                List<ComponentEntity> allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification = componentRepository.findComponentsByTypeAndSpecification(searchedComponentsType,specificationForTheSearchedComponents.getId() , values);

                //List<Component> allComponentsBase = new ArrayList<>();

                for (ComponentEntity componentEntity : allComponentsThatArePartOfTheFirstComponentTypeAndHaveTheChosenSpecification) {

                    List<Component_SpecificationList> allSpecificationsForComponent = componentSpecificationListRepository.findByComponentId(componentEntity);
                    Map<SpecificationTypeEntity, List<String>> dictionaryWithTheSpecificationAndAllValuesForComponent = new HashMap<>();

                    for (Component_SpecificationList specificationList : allSpecificationsForComponent) {
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
                    Component componentBase = ComponentConverter.convertFromEntityToBase(componentEntity, dictionaryWithTheSpecificationAndAllValuesForComponent);
                    allComponentsBase.add(componentBase);
                }
                //allComponentsBase;
            }
            return allComponentsBase;
        }
        throw new ObjectNotFound("COMPONENT NOT FOUND");
    }

}
