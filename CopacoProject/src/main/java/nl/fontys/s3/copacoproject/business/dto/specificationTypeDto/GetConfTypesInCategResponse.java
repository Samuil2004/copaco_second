package nl.fontys.s3.copacoproject.business.dto.specificationTypeDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Builder
@Getter
@Setter
public class GetConfTypesInCategResponse {
    private List<String> distinctConfigurationTypesInCategory;
}
