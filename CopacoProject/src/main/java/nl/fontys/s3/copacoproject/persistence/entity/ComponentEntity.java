package nl.fontys.s3.copacoproject.persistence.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class ComponentEntity extends ComponentTypeEntity{
    private Long componentId;
    private String componentName;
    private String componentUnit;
    private String componentValue;
    private Double componentPrice;
    private Long componentTypeId;
    private String componentImageUrl;
    private Long brandId;
}
