package nl.fontys.s3.copacoproject.business.converters;

import nl.fontys.s3.copacoproject.domain.SpecificationType;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;

public final class SpecificationTypeConverter {

    private SpecificationTypeConverter() {}
    public static SpecificationType convertFromEntityToBase(SpecificationTypeEntity entity) {
        return SpecificationType.builder()
                .specificationTypeId(entity.getId())
                .specificationTypeName(entity.getSpecificationTypeName())
                .build();
    }
    public static SpecificationTypeEntity convertFromBaseToEntity(SpecificationType specificationType) {
        return SpecificationTypeEntity.builder()
                .id(specificationType.getSpecificationTypeId())
                .specificationTypeName(specificationType.getSpecificationTypeName())
                .build();
    }
}
