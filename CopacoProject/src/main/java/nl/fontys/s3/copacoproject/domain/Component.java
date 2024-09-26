package nl.fontys.s3.copacoproject.domain;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class Component extends ComponentType
{
    private Integer componentId;
    private String componentName;
    private String componentUnit;
    private Double value;
    private Double price;
    private String componentImageUrl;
    private String brand;
}
