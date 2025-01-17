package nl.fontys.s3.copacoproject.business.dto.template_dto;

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
    private String configurationType;
    @NotBlank
    private String name;
    @NotNull
    private List<Long> componentTypes;
}
