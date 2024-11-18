package nl.fontys.s3.copacoproject.business.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CreateAutomaticCompatibilityDtoRequest {
    private Long componentType1Id;
    private Long componentType2Id;
    private Long specificationToConsiderId_from_component1;
    private Long specificationToConsiderId_from_component2;
}
