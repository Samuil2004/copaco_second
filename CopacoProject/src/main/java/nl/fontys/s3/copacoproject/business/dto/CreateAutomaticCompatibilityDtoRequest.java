package nl.fontys.s3.copacoproject.business.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CreateAutomaticCompatibilityDtoRequest {
    private Long componentType1Id;
    private Long componentType2Id;
    private Long specificationToConsiderId_from_component1;
    private Long specificationToConsiderId_from_component2;
}
