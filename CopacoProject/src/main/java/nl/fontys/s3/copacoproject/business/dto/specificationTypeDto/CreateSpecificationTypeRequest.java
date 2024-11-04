package nl.fontys.s3.copacoproject.business.dto.specificationTypeDto;

import lombok.*;

@RequiredArgsConstructor
@Setter
@Getter
@Builder
@AllArgsConstructor
public class CreateSpecificationTypeRequest {
    private Long SpecificationTypeId;
    private String specificationTypeName;
}
