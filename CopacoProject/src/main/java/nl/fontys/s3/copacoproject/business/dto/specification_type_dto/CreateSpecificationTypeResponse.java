package nl.fontys.s3.copacoproject.business.dto.specification_type_dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSpecificationTypeResponse {
    @NotNull
    private Long specificationTypeId;
}
