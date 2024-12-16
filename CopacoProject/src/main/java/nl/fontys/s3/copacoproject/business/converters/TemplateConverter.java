package nl.fontys.s3.copacoproject.business.converters;

import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.TemplateObjectResponse;
import nl.fontys.s3.copacoproject.domain.Template;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeList_Template;
import nl.fontys.s3.copacoproject.persistence.entity.TemplateEntity;

import java.util.List;

public final class TemplateConverter {
    public static Template convertFromEntityToBase(TemplateEntity entity, List<ComponentTypeList_Template> componentTypeEntities) {
        return Template.builder()
                .templateId(entity.getId())
                .name(entity.getName())
                .category(CategoryConverter.convertFromEntityToBase(entity.getCategory()))
                .configurationType(entity.getConfigurationType())
                .imageUrl(entity.getImageURL())
                .components(ComponentTypeList_TemplateConverter.convertFromEntityToBase(componentTypeEntities))
                .build();
    }

    public static TemplateEntity convertFromBaseToEntity(Template template) {
        return TemplateEntity.builder()
                .id(template.getTemplateId())
                .name(template.getName())
                .category(CategoryConverter.convertFromBaseToEntity(template.getCategory()))
                .configurationType(template.getConfigurationType())
                .imageURL(template.getImageUrl())
                .build();
    }

    public static TemplateObjectResponse convertFromEntityToResponse(TemplateEntity templateEntity, List<String> componentTypes) {
        return TemplateObjectResponse.builder()
                .templateId(templateEntity.getId())
                .category(CategoryConverter.convertFromEntityToBase(templateEntity.getCategory()))
                .imageUrl(templateEntity.getImageURL())
                .name(templateEntity.getName())
                .components(componentTypes)
                .configurationType(templateEntity.getConfigurationType())
                .build();
    }
}
