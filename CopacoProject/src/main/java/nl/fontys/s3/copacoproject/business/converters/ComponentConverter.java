package nl.fontys.s3.copacoproject.business.converters;

import nl.fontys.s3.copacoproject.business.dto.component.ComponentInConfigurationResponse;
import nl.fontys.s3.copacoproject.domain.Component;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;

import java.util.*;

public final class ComponentConverter {
    public static Component convertFromEntityToBase(ComponentEntity entity, Map<SpecificationTypeEntity, List<String>> specificationEntityList)
    {
        Map<SpecificationType, List<String>> baseMap = new HashMap<>();

        for (Map.Entry<SpecificationTypeEntity, List<String>> entry : specificationEntityList.entrySet()) {
            SpecificationTypeEntity entityKey = entry.getKey();
            List<String> values = entry.getValue();
            SpecificationType baseKey = SpecificationTypeConverter.convertFromEntityToBase(entityKey);
            baseMap.put(baseKey, values);
        }

        return Component.builder()
                .componentId(entity.getComponentId())
                .componentName(entity.getComponentName())
                .componentImageUrl(entity.getComponentImageUrl())
                .componentType(ComponentTypeConverter.convertFromEntityToBase(entity.getComponentType()))
                .brand(BrandConverter.convertFromEntityToBase(entity.getBrand()))
                .componentPrice(entity.getComponentPrice())
                .specifications(baseMap)
                .build();
    }
    public static ComponentEntity convertFromBaseToEntity (Component component)
    {
        return ComponentEntity.builder()
                .componentId(component.getComponentId())
                .componentName(component.getComponentName())
                .componentImageUrl(component.getComponentImageUrl())
                .componentType(ComponentTypeConverter.convertFromBaseToEntity(component.getComponentType()))
                .brand(BrandConverter.convertFromBaseToEntity(component.getBrand()))
                .componentPrice(component.getComponentPrice())
                //.componentImageUrl(component.getComponentImageUrl())
                .build();
    }

    public static ComponentInConfigurationResponse convertFromBaseToResponse(Component component){
        return ComponentInConfigurationResponse.builder()
                .componentId(component.getComponentId())
                .componentImageUrl(component.getComponentImageUrl())
                .componentName(component.getComponentName())
                .componentPrice(component.getComponentPrice())
                .componentType(ComponentTypeConverter.convertFromBaseToResponse(component.getComponentType()))
                .build();
    }
}