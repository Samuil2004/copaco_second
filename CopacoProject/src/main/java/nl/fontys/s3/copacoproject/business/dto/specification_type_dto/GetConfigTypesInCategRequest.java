package nl.fontys.s3.copacoproject.business.dto.specification_type_dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetConfigTypesInCategRequest {
@NotNull
    private Long categoryId;
}
