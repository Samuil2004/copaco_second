package nl.fontys.s3.copacoproject.business.dto.specification_type_dto;

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
