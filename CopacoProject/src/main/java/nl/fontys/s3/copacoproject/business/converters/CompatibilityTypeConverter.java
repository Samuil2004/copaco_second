package nl.fontys.s3.copacoproject.business.converters;

import nl.fontys.s3.copacoproject.domain.CompatibilityType;
import nl.fontys.s3.copacoproject.persistence.entity.CompatibilityTypeEntity;

public final class CompatibilityTypeConverter {

    public static CompatibilityType convertFromEntityToBase(CompatibilityTypeEntity compatibilityTypeEntity) {
        return CompatibilityType.builder()
                .id(compatibilityTypeEntity.getId())
                .typeOfCompatibility(compatibilityTypeEntity.getTypeOfCompatibility())
                .build();
    }
}
