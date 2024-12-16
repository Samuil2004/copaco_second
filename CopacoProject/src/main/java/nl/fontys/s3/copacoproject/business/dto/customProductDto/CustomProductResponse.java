package nl.fontys.s3.copacoproject.business.dto.customProductDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.fontys.s3.copacoproject.domain.Component;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CustomProductResponse {
    private long userId;
    private long customProductId;
    private long templateId;
    private List<Component> componentsIncluded;
    private long statusId;

}
