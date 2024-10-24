package nl.fontys.s3.copacoproject.business.impl;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.ComponentManager;
import nl.fontys.s3.copacoproject.business.converters.ComponentConverter;
import nl.fontys.s3.copacoproject.business.dto.GetAllComponentsResponse;
import nl.fontys.s3.copacoproject.business.dto.GetComponentsByCategoryResponse;
import nl.fontys.s3.copacoproject.domain.Component;
import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentSpecificationListRepository;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import nl.fontys.s3.copacoproject.persistence.entity.Component_SpecificationList;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ComponentManagerImpl implements ComponentManager {

    private final ComponentRepository componentRepository;
    private final ComponentSpecificationListRepository  componentSpecificationListRepository;

    @Override
    public GetAllComponentsResponse getAllComponents() {
        List<Component> components = componentRepository.findAll()
                .stream()
                .map(entity -> {
                    List<Component_SpecificationList> specificationLists = componentSpecificationListRepository.getComponent_SpecificationListByComponentId(entity);

                    Map<SpecificationTypeEntity, String> specificationMap = specificationLists.stream()
                            .collect(Collectors.toMap(Component_SpecificationList::getSpecificationType, Component_SpecificationList::getValue));

                    return ComponentConverter.convertFromEntityToBase(entity, specificationMap);
                })
                .toList();
        return GetAllComponentsResponse.builder()
                .allComponents(components)
                .build();

       /* return GetAllComponentsResponse.builder()
                .allComponents(componentRepository.getAllComponents()
                        .stream()
                        .map(ComponentConverter::convertEntityToNormal)
                        .toList()).build();*/
    }
    @Override
    public Component GetComponentById(Long id){
        ComponentEntity entity = componentRepository.getComponentEntitiesByComponentId(id);
        Map<SpecificationTypeEntity, String> specifications = componentSpecificationListRepository.getComponent_SpecificationListByComponentId(entity)
                .stream()
                .collect(Collectors.toMap(Component_SpecificationList::getSpecificationType, Component_SpecificationList::getValue));
        return ComponentConverter.convertFromEntityToBase(entity, specifications);
    }

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
