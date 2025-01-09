package nl.fontys.s3.copacoproject.business.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nl.fontys.s3.copacoproject.domain.SpecificationType;

@Builder
@Getter
@Setter
public class GetAutomaticCompatibilityByIdResponse {
    private long automaticCompatibilityId;
    private long componentType1Id;
    private long componentType2Id;
    private SpecificationType specificationTypeFromComponentType1;
    private SpecificationType specificationTypeFromComponentType2;
}
