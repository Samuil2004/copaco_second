package nl.fontys.s3.copacoproject.business.dto.component_type_dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class ComponentTypeInCustomResponse {
    private Long id;
    private String name;
}
