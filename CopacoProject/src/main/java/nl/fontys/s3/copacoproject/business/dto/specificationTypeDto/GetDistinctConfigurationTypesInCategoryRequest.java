package nl.fontys.s3.copacoproject.business.dto.specificationTypeDto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetDistinctConfigurationTypesInCategoryRequest {
@NotNull
    private Long categoryId;
}
