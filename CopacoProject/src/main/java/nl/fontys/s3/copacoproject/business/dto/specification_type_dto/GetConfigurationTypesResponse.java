package nl.fontys.s3.copacoproject.business.dto.specification_type_dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class GetConfigurationTypesResponse {
    private List<String> distinctConfigurationTypes;
}
