package nl.fontys.s3.copacoproject.business.converters;

import nl.fontys.s3.copacoproject.domain.ComponentType;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeList_Template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ComponentTypeList_TemplateConverter {
    public static Map<ComponentType, Integer> convertFromEntityToBase(List<ComponentTypeList_Template> componentEntities){
        Map<ComponentType, Integer> components = new HashMap<>();
        for(ComponentTypeList_Template componentEntity : componentEntities){
            ComponentType component = ComponentTypeConverter.convertFromEntityToBase(componentEntity.getComponentType());
            components.put(component, componentEntity.getOrderOfImportance());
        }
        return components;
    }
}
