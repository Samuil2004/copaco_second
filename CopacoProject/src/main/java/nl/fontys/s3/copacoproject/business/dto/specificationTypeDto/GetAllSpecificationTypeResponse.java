package nl.fontys.s3.copacoproject.business.dto.specificationTypeDto;

import lombok.*;
import nl.fontys.s3.copacoproject.domain.SpecificationType;

import java.util.List;
@RequiredArgsConstructor
@Builder
@Setter
@Getter
@AllArgsConstructor
public class GetAllSpecificationTypeResponse {
    private List<SpecificationType> specificationTypes;
}
