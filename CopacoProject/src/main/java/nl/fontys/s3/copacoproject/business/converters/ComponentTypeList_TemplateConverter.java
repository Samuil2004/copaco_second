package nl.fontys.s3.copacoproject.business.converters;

import nl.fontys.s3.copacoproject.domain.ComponentType;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeList_Template;

import java.util.ArrayList;
import java.util.List;

public final class ComponentTypeList_TemplateConverter {

    private ComponentTypeList_TemplateConverter() {}

    public static List<ComponentType> convertFromEntityToBase(List<ComponentTypeList_Template> componentEntities){
        List<ComponentType> components = new ArrayList<>();
        for(ComponentTypeList_Template componentEntity : componentEntities){
            ComponentType component = ComponentTypeConverter.convertFromEntityToBase(componentEntity.getComponentType());
            components.add(component);
        }
        return components;
    }
}
