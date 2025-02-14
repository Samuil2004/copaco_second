package nl.fontys.s3.copacoproject.domain;

import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Component
{
    private long componentId;
    private String componentName;
    private ComponentType componentType;
    private String componentImageUrl;
    private Double componentPrice;
    //private Map<SpecificationType,String> specifications; //string = the value of the specification
    private Map<SpecificationType, List<String>> specifications;
}