package nl.fontys.s3.copacoproject.business.dto.custom_product_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.fontys.s3.copacoproject.business.dto.component.ComponentInConfigurationResponse;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CustomProductResponse {
    private long userId;
    private long customProductId;
    private long templateId;
    private List<ComponentInConfigurationResponse> componentsIncluded;
    private long statusId;

}
