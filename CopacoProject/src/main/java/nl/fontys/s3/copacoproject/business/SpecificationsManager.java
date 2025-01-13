package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.specification_type_dto.GetConfigTypesInCategRequest;
import nl.fontys.s3.copacoproject.business.dto.specification_type_dto.GetConfTypesInCategResponse;

import java.util.List;

public interface SpecificationsManager {
    GetConfTypesInCategResponse getDistinctConfigurationTypesInCategory(GetConfigTypesInCategRequest request);
    List<String> getSpecificationValuesOfSpecificationTypeByComponentType(Long componentTypeId, Long specificationTypeId, int currentPage, int itemsPerPage);
}
