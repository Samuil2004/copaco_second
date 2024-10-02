package nl.fontys.s3.copacoproject.business.impl;

import nl.fontys.s3.copacoproject.domain.Component;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeEntity;

final class ComponentConverter {
    public static Component convertEntityToNormal(ComponentEntity entity)
    {
        return Component.builder()
                .componentTypeId(entity.getComponentTypeId())
                .componentTypeName(entity.getComponentTypeName())
                .orderOfImportance(entity.getOrderOfImportance())
                .componentTypeImageUrl(entity.getComponentTypeImageUrl())
                .categoryId(entity.getCategoryId())
                .componentId(entity.getComponentId())
                .componentName(entity.getComponentName())
                .componentUnit(entity.getComponentUnit())
                .componentValue(entity.getComponentValue())
                .componentPrice(entity.getComponentPrice())
                .componentImageUrl(entity.getComponentImageUrl())
                .brandId(entity.getBrandId())
                .build();
    }
    public static ComponentEntity convertNormalToEntity (Component component)
    {
        return ComponentEntity.builder()
                .componentTypeId(component.getComponentTypeId())
                .componentTypeName(component.getComponentTypeName())
                .orderOfImportance(component.getOrderOfImportance())
                .componentTypeImageUrl(component.getComponentTypeImageUrl())
                .categoryId(component.getCategoryId())
                .componentId(component.getComponentId())
                .componentName(component.getComponentName())
                .componentUnit(component.getComponentUnit())
                .componentValue(component.getComponentValue())
                .componentPrice(component.getComponentPrice())
                .componentImageUrl(component.getComponentImageUrl())
                .brandId(component.getBrandId())
                .build();
    }
}