package nl.fontys.s3.copacoproject.business.converters;

import nl.fontys.s3.copacoproject.domain.Template;
import nl.fontys.s3.copacoproject.persistence.entity.TemplateEntity;

public final class TemplateConverter {
    public static Template convertFromEntityToBase(TemplateEntity entity) {
        return Template.builder()
                .templateId(entity.getId())
                .name(entity.getName())
                .brand(BrandConverter.convertFromEntityToBase(entity.getBrand()))
                .category(CategoryConverter.convertFromEntityToBase(entity.getCategory()))
                .imageUrl(entity.getImageURL())
                .build();
    }

    public static TemplateEntity convertFromBaseToEntity(Template template) {
        return TemplateEntity.builder()
                .id(template.getTemplateId())
                .name(template.getName())
                .brand(BrandConverter.convertFromBaseToEntity(template.getBrand()))
                .category(CategoryConverter.convertFromBaseToEntity(template.getCategory()))
                .imageURL(template.getImageUrl())
                .build();
    }
}
