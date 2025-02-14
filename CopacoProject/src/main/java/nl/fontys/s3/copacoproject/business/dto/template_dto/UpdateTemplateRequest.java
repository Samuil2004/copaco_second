package nl.fontys.s3.copacoproject.business.dto.template_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateTemplateRequest {
    @NotNull
    private long categoryId;
    @NotBlank
    private String name;
    @NotNull
    private List<Long> componentTypes;
}
