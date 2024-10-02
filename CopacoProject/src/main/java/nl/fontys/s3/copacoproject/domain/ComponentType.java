package nl.fontys.s3.copacoproject.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class ComponentType
{
    private Long componentTypeId;
    private String componentTypeName;
    private Integer orderOfImportance;
    private String componentTypeImageUrl;
    private Long categoryId;
}
