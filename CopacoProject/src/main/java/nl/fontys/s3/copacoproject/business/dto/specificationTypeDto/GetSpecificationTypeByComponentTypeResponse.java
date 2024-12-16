package nl.fontys.s3.copacoproject.business.dto.specificationTypeDto;

import lombok.*;
import nl.fontys.s3.copacoproject.domain.SpecificationType;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class GetSpecificationTypeByComponentTypeResponse {
    private int totalNumberOfItems;
    private List<SpecificationType> specificationTypes;
}
