package nl.fontys.s3.copacoproject.business.converters;

import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.TemplateObjectResponse;
import nl.fontys.s3.copacoproject.domain.Template;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeList_Template;
import nl.fontys.s3.copacoproject.persistence.entity.TemplateEntity;

import java.util.List;

public final class TemplateConverter {
    public static Template convertFromEntityToBase(TemplateEntity entity, List<ComponentTypeList_Template> componentTypeEntities) {
        byte[] image = null;
        if(entity.getImage() != null) {
            image = entity.getImage();
        }
        return Template.builder()
                .templateId(entity.getId())
                .name(entity.getName())
                .category(CategoryConverter.convertFromEntityToBase(entity.getCategory()))
                .configurationType(entity.getConfigurationType())
                .image(entity.getImage())
                .components(ComponentTypeList_TemplateConverter.convertFromEntityToBase(componentTypeEntities))
                .build();
    }

    public static TemplateEntity convertFromBaseToEntity(Template template) {
        byte[] image = null;
        if(template.getImage() != null) {
            image = template.getImage();
        }
        return TemplateEntity.builder()
                .id(template.getTemplateId())
                .name(template.getName())
                .category(CategoryConverter.convertFromBaseToEntity(template.getCategory()))
                .configurationType(template.getConfigurationType())
                .image(template.getImage())
                .build();
    }

    public static TemplateObjectResponse convertFromEntityToResponse(TemplateEntity templateEntity, List<String> componentTypes) {
        byte[] image = null;
        if(templateEntity.getImage() != null) {
            image = templateEntity.getImage();
        }
        return TemplateObjectResponse.builder()
                .templateId(templateEntity.getId())
                .category(CategoryConverter.convertFromEntityToBase(templateEntity.getCategory()))
                .image(templateEntity.getImage())
                .name(templateEntity.getName())
                .components(componentTypes)
                .configurationType(templateEntity.getConfigurationType())
                .build();
    }
}
