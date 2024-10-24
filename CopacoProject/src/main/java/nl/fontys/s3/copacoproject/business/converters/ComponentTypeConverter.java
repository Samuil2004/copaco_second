package nl.fontys.s3.copacoproject.business.converters;

import nl.fontys.s3.copacoproject.domain.ComponentType;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeEntity;
import nl.fontys.s3.copacoproject.persistence.entity.SpecficationTypeList_ComponentType;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;

import java.util.ArrayList;
import java.util.List;
public final class ComponentTypeConverter {

    public static ComponentType convertFromEntityToBase(ComponentTypeEntity entity) {

        List<SpecificationType> specificationTypes = new ArrayList<>();

        if (entity.getSpecifications() != null) {
            for (SpecficationTypeList_ComponentType specificationTypeList : entity.getSpecifications()) {
                specificationTypes.add(SpecificationTypeConverter.convertFromEntityToBase(specificationTypeList.getSpecificationType()));
            }
        }

        return ComponentType.builder()
                .componentTypeId(entity.getId())
                .componentTypeName(entity.getComponentTypeName())
                .componentTypeImageUrl(entity.getComponentTypeImageUrl())
                .category(CategoryConverter.convertFromBaseToEntity(entity.getCategory())) // Ensure to convert Category if needed
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
