package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.specification_type_dto.GetConfigTypesInCategRequest;
import nl.fontys.s3.copacoproject.business.dto.specification_type_dto.GetConfTypesInCategResponse;
import nl.fontys.s3.copacoproject.business.dto.specification_type_dto.GetConfigurationTypesResponse;

import java.util.List;

public interface SpecificationsManager {
    //GetConfigurationTypesResponse getDistinctConfigurationTypes();
    GetConfTypesInCategResponse getDistinctConfigurationTypesInCategory(GetConfigTypesInCategRequest request);
    List<String> getSpecificationValuesOfSpecificationTypeByComponentType(Long componentTypeId, Long specificationTypeId, int currentPage, int itemsPerPage);
}
