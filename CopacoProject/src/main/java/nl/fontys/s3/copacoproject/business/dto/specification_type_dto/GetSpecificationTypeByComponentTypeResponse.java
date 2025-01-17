package nl.fontys.s3.copacoproject.business.dto.specification_type_dto;

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
