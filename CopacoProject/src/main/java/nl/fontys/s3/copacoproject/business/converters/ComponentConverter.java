package nl.fontys.s3.copacoproject.business.converters;

import nl.fontys.s3.copacoproject.domain.Component;
import nl.fontys.s3.copacoproject.domain.ComponentType;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeEntity;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;

import java.security.Key;
import java.util.*;


final class ComponentConverter {
    public static Component convertFromEntityToBase(ComponentEntity entity, Map<SpecificationTypeEntity, String> specificationEntityList)
    {
        ComponentTypeEntity componentTypeEntity= entity.getComponentType();
        List<SpecificationTypeEntity> specificationTypeList = new ArrayList<>();

        specificationTypeList.addAll(specificationEntityList.keySet());

        return Component.builder()
                .componentId(entity.getComponentId())
                .componentName(entity.getComponentName())
                .componentImageUrl(entity.getComponentImageUrl())
                .componentType(ComponentTypeConverter.convertFromEntityToBase(componentTypeEntity, specificationTypeList))
                .componentName(entity.getComponentName())
                .componentPrice(entity.getComponentPrice())
                .componentImageUrl(entity.getComponentImageUrl())
                .build();
    }
    public static ComponentEntity convertFromBaseToEntity (Component component)
    {
        return ComponentEntity.builder()
                .componentId(component.getComponentId())
                .componentName(component.getComponentName())
                .componentImageUrl(component.getComponentImageUrl())
                .componentId(component.getComponentId())
                .componentName(component.getComponentName())
                .componentPrice(component.getComponentPrice())
                .componentImageUrl(component.getComponentImageUrl())
                .build();
    }
}