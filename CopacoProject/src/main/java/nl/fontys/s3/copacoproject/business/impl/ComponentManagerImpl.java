package nl.fontys.s3.copacoproject.business.impl;

import ch.qos.logback.core.joran.sanity.Pair;
import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.ComponentManager;
import nl.fontys.s3.copacoproject.business.converters.ComponentConverter;
import nl.fontys.s3.copacoproject.business.dto.GetAllComponentsResponse;
import nl.fontys.s3.copacoproject.business.dto.GetComponentResponse;
import nl.fontys.s3.copacoproject.business.dto.GetComponentsByCategoryResponse;
import nl.fontys.s3.copacoproject.domain.Component;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentSpecificationListRepository;
import nl.fontys.s3.copacoproject.persistence.SpecificationTypeRepository;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import nl.fontys.s3.copacoproject.persistence.entity.Component_SpecificationList;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ComponentManagerImpl implements ComponentManager {

    private final ComponentRepository componentRepository;
    private final ComponentSpecificationListRepository  componentSpecificationListRepository;
    private final SpecificationTypeRepository specificationTypeRepository;

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

        //GetAllComponentsResponse response = GetAllComponentsResponse.builder().allComponents(allComponentsBase).build();
        return allComponentsResponse;
    }



    //@Override
//    public Component GetComponentById(Long id){
////        ComponentEntity entity = componentRepository.getComponentEntitiesByComponentId(id);
////        Map<SpecificationTypeEntity, String> specifications = componentSpecificationListRepository.getComponent_SpecificationListByComponentId(entity)
////                .stream()
////                .collect(Collectors.toMap(Component_SpecificationList::getSpecificationType, Component_SpecificationList::getValue));
////        return ComponentConverter.convertFromEntityToBase(entity, specifications);
//    }

//    @Override
//    public GetComponentsByCategoryResponse getComponentsByCategory(String category) {
//        List<Component> componentsInCategory = componentRepository.getComponentsByType(category)
//                .stream()
//                .map(entity -> {
//                    List<Component_SpecificationList> specificationLists = componentSpecificationListRepository.getComponent_SpecificationListByComponentId(entity);
//                    Map<SpecificationTypeEntity, String> specificationMap = specificationLists.stream()
//                            .collect(Collectors.toMap(Component_SpecificationList::getSpecificationType, Component_SpecificationList::getValue));
//                    return ComponentConverter.convertFromEntityToBase(entity, specificationMap);
//                })
//                .toList();
//
//        return GetComponentsByCategoryResponse.builder()
//                .allComponentsInCategory(componentsInCategory)
//                .build();
//
//
///*        return GetComponentsByCategoryResponse
//                .builder()
//                .allComponentsInCategory(componentRepository.getComponentsByType(category)
//                        .stream()
//                        .map(ComponentConverter::convertEntityToNormal)
//                        .toList())
//                .build();*/
//        /*return null;*/
//    }
}
