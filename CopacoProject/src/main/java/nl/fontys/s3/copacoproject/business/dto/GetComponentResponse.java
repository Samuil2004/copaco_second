package nl.fontys.s3.copacoproject.business.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
@Builder
@Getter
@Setter
public class GetComponentResponse {
    private Long componentId;
    private String componentName;
    private Long componentTypeId;
    private String componentTypeName;
    private String categoryName;
    private String componentImageUrl;
    private Double componentPrice;
    private Map<String,List<String>> specifications;
}
