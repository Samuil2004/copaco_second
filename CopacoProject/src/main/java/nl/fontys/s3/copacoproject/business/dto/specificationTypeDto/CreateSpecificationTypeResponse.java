package nl.fontys.s3.copacoproject.business.dto.specificationTypeDto;

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
