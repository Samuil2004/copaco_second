package nl.fontys.s3.copacoproject.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import nl.fontys.s3.copacoproject.domain.enums.CompatibilityType;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentType
{
    private Long componentTypeId;
    private String componentTypeName;
    private String componentTypeImageUrl;
    private Category category;
    private List<SpecificationType> specificationTypeList;
    private List<CompatibilityType> compatibilityTypeList;
}
