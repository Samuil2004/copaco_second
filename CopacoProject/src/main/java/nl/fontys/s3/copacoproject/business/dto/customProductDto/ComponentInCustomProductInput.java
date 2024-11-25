package nl.fontys.s3.copacoproject.business.dto.customProductDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ComponentInCustomProductInput {
    private long componentId;
}
