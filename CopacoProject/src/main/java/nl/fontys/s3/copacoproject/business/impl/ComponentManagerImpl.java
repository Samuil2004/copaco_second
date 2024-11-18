package nl.fontys.s3.copacoproject.business.impl;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.ComponentManager;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.converters.ComponentConverter;
import nl.fontys.s3.copacoproject.business.dto.GetComponentResponse;
import nl.fontys.s3.copacoproject.domain.Component;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import nl.fontys.s3.copacoproject.persistence.*;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeEntity;
import nl.fontys.s3.copacoproject.persistence.entity.Component_SpecificationList;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
public class ComponentManagerImpl implements ComponentManager {

    private final ComponentRepository componentRepository;
    private final ComponentSpecificationListRepository  componentSpecificationListRepository;
    private final SpecificationTypeRepository specificationTypeRepository;
    private final CategoryRepository categoryRepository;
    private final ComponentTypeRepository componentTypeRepository;

    @Override
    public List<GetComponentResponse> getAllComponents() {
        List<ComponentEntity> allComponentsEntities = componentRepository.findAll();
        List<Component> allComponentsBase = new ArrayList<>();
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
            allComponentsBase.add(componentBase);


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
                    .brandName(componentEntity.getBrand().getName())
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
            allComponentsBase.add(componentBase);


            /*Map<String, List<String>> specificationsForResponse = new HashMap<>();
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
                    .brandName(componentEntity.getBrand().getName())
                    .componentPrice(componentEntity.getComponentPrice())
                    .specifications(specificationsForResponse)
                    .build();
            allComponentsResponse.add(getComponentResponse);*/
        }

        //GetAllComponentsResponse response = GetAllComponentsResponse.builder().allComponents(allComponentsBase).build();
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
            allComponentsBase.add(componentBase);


        }
        return allComponentsBase;
    }

}
