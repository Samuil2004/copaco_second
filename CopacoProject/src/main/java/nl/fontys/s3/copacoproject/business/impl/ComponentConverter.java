package nl.fontys.s3.copacoproject.business.impl;

import nl.fontys.s3.copacoproject.domain.Component;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;

final class ComponentConverter {
    public static Component convertEntityToNormal(ComponentEntity entity)
    {
        return Component.builder()
                .componentId(entity.getComponentId())
                .componentName(entity.getComponentName())
                .componentImageUrl(entity.getComponentImageUrl())
                .componentId(entity.getComponentId())
                .componentName(entity.getComponentName())
                .componentPrice(entity.getComponentPrice())
                .componentImageUrl(entity.getComponentImageUrl())
                .build();
    }
    public static ComponentEntity convertNormalToEntity (Component component)
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