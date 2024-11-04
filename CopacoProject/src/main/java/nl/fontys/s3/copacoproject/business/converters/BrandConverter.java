package nl.fontys.s3.copacoproject.business.converters;

import nl.fontys.s3.copacoproject.domain.Brand;
import nl.fontys.s3.copacoproject.persistence.entity.BrandEntity;

public final class BrandConverter {
    public static Brand convertFromEntityToBase(BrandEntity brandEntity) {
        return Brand.builder()
                .id(brandEntity.getId())
                .name(brandEntity.getName())
                .build();
    }
    public static BrandEntity convertFromBaseToEntity(Brand brand) {
        return BrandEntity.builder()
                .id(brand.getId())
                .name(brand.getName())
                .build();
    }
}
