package nl.fontys.s3.copacoproject.business.dto.TemplateDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComponentTypeItemInTemplate {
    @NotNull
    private long componentTypeId;
    @NotNull
    private int orderOfImportance;
}
