package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.GetDistinctConfigurationTypesInCategoryRequest;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.GetDistinctConfigurationTypesInCategoryResponse;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.GetDistinctConfigurationTypesResponse;

import java.util.List;

public interface SpecificationsManager {
    GetDistinctConfigurationTypesResponse getDistinctConfigurationTypes();
    GetDistinctConfigurationTypesInCategoryResponse getDistinctConfigurationTypesInCategory(GetDistinctConfigurationTypesInCategoryRequest request);
    List<String> getSpecificationValuesOfSpecificationTypeByComponentType(Long componentTypeId, Long specificationTypeId, int currentPage, int itemsPerPage);
}
