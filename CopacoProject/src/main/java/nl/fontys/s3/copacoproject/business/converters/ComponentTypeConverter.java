package nl.fontys.s3.copacoproject.business.converters;

import nl.fontys.s3.copacoproject.domain.ComponentType;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeEntity;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;

import java.util.ArrayList;
import java.util.List;

final class ComponentTypeConverter {
    public static ComponentType convertFromEntityToBase(ComponentTypeEntity entity, List<SpecificationTypeEntity> specifications) {

        List<SpecificationType> specificationTypes = new ArrayList<>();

        for (SpecificationTypeEntity specificationTypeEntity : specifications) {
            specificationTypes.add(SpecificationTypeConverter.convertFromEntityToBase(specificationTypeEntity));
        }

        return ComponentType.builder()
                .componentTypeId(entity.getId())
                .componentTypeName(entity.getComponentTypeName())
                .componentTypeImageUrl(entity.getComponentTypeImageUrl())
                .specificationTypeList(specificationTypes)
                .build();
    }

    public static ComponentTypeEntity convertFromBaseToEntity(ComponentType base) {

        return ComponentTypeEntity.builder()
                .id(base.getComponentTypeId())
                .componentTypeName(base.getComponentTypeName())
                .componentTypeImageUrl(base.getComponentTypeImageUrl())
                .build();
    }
}
