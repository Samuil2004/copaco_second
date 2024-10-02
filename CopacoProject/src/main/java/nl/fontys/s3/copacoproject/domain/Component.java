package nl.fontys.s3.copacoproject.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class Component extends ComponentType
{
    private Long componentId;
    private String componentName;
    private String componentUnit;
    private String componentValue;
    private Double componentPrice;
    private Long componentTypeId;
    private String componentImageUrl;
    private Long brandId;
}