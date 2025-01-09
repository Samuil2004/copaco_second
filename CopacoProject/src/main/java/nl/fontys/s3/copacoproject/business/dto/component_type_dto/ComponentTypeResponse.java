package nl.fontys.s3.copacoproject.business.dto.component_type_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ComponentTypeResponse {
    private long id;
    private String componentTypeName;
    private String componentTypeImageUrl;
    private String categoryName;
    private String configurationType;
}
