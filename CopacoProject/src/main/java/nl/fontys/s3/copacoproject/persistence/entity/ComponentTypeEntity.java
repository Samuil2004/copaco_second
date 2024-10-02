package nl.fontys.s3.copacoproject.persistence.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class ComponentTypeEntity {
    private Long componentTypeId;
    private String componentTypeName;
    private Integer orderOfImportance;
    private String componentTypeImageUrl;
    private Long categoryId;
}
