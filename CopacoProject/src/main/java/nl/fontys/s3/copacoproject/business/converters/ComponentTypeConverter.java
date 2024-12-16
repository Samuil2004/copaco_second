package nl.fontys.s3.copacoproject.business.converters;

import nl.fontys.s3.copacoproject.domain.ComponentType;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeEntity;
import nl.fontys.s3.copacoproject.persistence.entity.SpecficationTypeList_ComponentTypeEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public final class ComponentTypeConverter {

    public static ComponentType convertFromEntityToBase(ComponentTypeEntity entity) {

        List<SpecificationType> specificationTypes = new ArrayList<>();

        if (entity.getSpecifications() != null) {
            for (SpecficationTypeList_ComponentTypeEntity specificationTypeList : entity.getSpecifications()) {
                specificationTypes.add(SpecificationTypeConverter.convertFromEntityToBase(specificationTypeList.getSpecificationType()));
            }
        }

        List<String> configurationTypes = new ArrayList<>();
        if (entity.getConfigurationType() != null && !entity.getConfigurationType().isEmpty()) {
            configurationTypes = Arrays.asList(entity.getConfigurationType().split(","));
        }

        return ComponentType.builder()
                .componentTypeId(entity.getId())
                .componentTypeName(entity.getComponentTypeName())
                .componentTypeImageUrl(entity.getComponentTypeImageUrl())
                .category(CategoryConverter.convertFromEntityToBase(entity.getCategory())) // Ensure to convert Category if needed
                .configurationTypes(configurationTypes)
                .specificationTypeList(specificationTypes)
                .build();
    }

    public static ComponentTypeEntity convertFromBaseToEntity(ComponentType base) {

        String configurationTypeString = null;
        if (base.getConfigurationTypes() != null && !base.getConfigurationTypes().isEmpty()) {
            configurationTypeString = String.join(",", base.getConfigurationTypes());
        }

        return ComponentTypeEntity.builder()
                .id(base.getComponentTypeId())
                .componentTypeName(base.getComponentTypeName())
                .componentTypeImageUrl(base.getComponentTypeImageUrl())
                .configurationType(configurationTypeString)
                .category(CategoryConverter.convertFromBaseToEntity(base.getCategory()))
                .build();
    }
}
