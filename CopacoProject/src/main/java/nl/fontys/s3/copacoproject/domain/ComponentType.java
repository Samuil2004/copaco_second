package nl.fontys.s3.copacoproject.domain;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class ComponentType
{
    private Integer componentTypeId;
    private String componentTypeName;
    private Integer orderOfImportance;
    private String componentimageUrl;
}
