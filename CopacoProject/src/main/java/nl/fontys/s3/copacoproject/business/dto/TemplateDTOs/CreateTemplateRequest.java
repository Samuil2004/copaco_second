package nl.fontys.s3.copacoproject.business.dto.TemplateDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class CreateTemplateRequest {
    @NotNull
    private long categoryId;
    @NotBlank
    private String name;
    private long brandId;
    private String imageUrl;
    @NotNull
    private List<ComponentTypeItemInTemplate> componentTypes;
}
