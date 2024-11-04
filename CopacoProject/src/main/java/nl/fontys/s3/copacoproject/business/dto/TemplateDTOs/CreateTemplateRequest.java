package nl.fontys.s3.copacoproject.business.dto.TemplateDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Builder
@Data
public class CreateTemplateRequest {
    @NotNull
    private long categoryId;
    @NotBlank
    private String name;
    private long brandId;
    private String imageUrl;
    private List<ComponentTypeItemInTemplate> componentTypes;
}
