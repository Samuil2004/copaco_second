package nl.fontys.s3.copacoproject.domain;

import lombok.*;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;

import java.util.List;
import java.util.Map;

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
    //private Map<SpecificationType,String> specifications; //string = the value of the specification
    private Map<SpecificationType, List<String>> specifications;
}