package nl.fontys.s3.copacoproject.business.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import nl.fontys.s3.copacoproject.domain.SpecificationType;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@EqualsAndHashCode
public class GetAutomaticCompatibilityResponse {
    private Long componentId;
    private String componentName;
    private Long componentTypeId;
    private String componentTypeName;
    private String componentImageUrl;
    private String brand;
    private Double price;
    private Map<SpecificationType, List<String>> componentSpecifications;
    private Boolean thereIsNextPage;
}
