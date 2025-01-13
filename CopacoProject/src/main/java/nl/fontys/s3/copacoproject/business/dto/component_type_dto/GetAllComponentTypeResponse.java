package nl.fontys.s3.copacoproject.business.dto.component_type_dto;

import lombok.*;
import nl.fontys.s3.copacoproject.domain.ComponentType;

import java.util.List;
@Getter
@Builder
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class GetAllComponentTypeResponse {
    private List<ComponentType> componentTypes;
}
