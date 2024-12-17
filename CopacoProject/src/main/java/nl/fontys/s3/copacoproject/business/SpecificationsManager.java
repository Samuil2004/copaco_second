package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.GetConfigTypesInCategRequest;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.GetConfTypesInCategResponse;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.GetConfigurationTypesResponse;

import java.util.List;

public interface SpecificationsManager {
    GetConfigurationTypesResponse getDistinctConfigurationTypes();
    GetConfTypesInCategResponse getDistinctConfigurationTypesInCategory(GetConfigTypesInCategRequest request);
    List<String> getSpecificationValuesOfSpecificationTypeByComponentType(Long componentTypeId, Long specificationTypeId, int currentPage, int itemsPerPage);
}
