package nl.fontys.s3.copacoproject.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentType
{
    private Long componentTypeId;
    private String componentTypeName;
    private String componentTypeImageUrl;
    private Category  category;
}
