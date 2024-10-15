package nl.fontys.s3.copacoproject.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Dictionary;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Component
{
    private Long componentId;
    private String componentName;
    private ComponentType componentType;
    private String componentImageUrl;
    private Brand brand;
    private Double componentPrice;
    private Dictionary<SpecificationType,String> specifictaions; //string = the value of the specification
}