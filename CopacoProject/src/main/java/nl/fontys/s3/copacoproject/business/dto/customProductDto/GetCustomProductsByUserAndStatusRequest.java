package nl.fontys.s3.copacoproject.business.dto.customProductDto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetCustomProductsByUserAndStatusRequest {
    @NotNull
    private int statusId;
}
