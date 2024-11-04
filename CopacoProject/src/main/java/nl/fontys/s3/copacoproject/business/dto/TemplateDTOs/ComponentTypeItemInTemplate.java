package nl.fontys.s3.copacoproject.business.dto.TemplateDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComponentTypeItemInTemplate {
    private long componentTypeId;
    private int orderOfImportance;
}
