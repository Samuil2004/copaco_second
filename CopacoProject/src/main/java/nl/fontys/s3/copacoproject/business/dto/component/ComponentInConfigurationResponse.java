package nl.fontys.s3.copacoproject.business.dto.component;

import lombok.*;
import nl.fontys.s3.copacoproject.business.dto.component_type_dto.ComponentTypeInCustomResponse;


@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class ComponentInConfigurationResponse {
    private Long componentId;
    private String componentName;
    private String componentImageUrl;
    private Double componentPrice;
    private ComponentTypeInCustomResponse componentType;
}
