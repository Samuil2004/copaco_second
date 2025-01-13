package nl.fontys.s3.copacoproject.business.dto.template_dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateTemplateStatusRequest {
    @NotNull
    private boolean active;
}
