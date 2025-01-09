package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.specification_type_dto.CreateSpecificationTypeRequest;
import nl.fontys.s3.copacoproject.business.dto.specification_type_dto.CreateSpecificationTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.specification_type_dto.GetAllSpecificationTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.specification_type_dto.GetSpecificationTypeByComponentTypeResponse;
import nl.fontys.s3.copacoproject.domain.SpecificationType;

import java.util.List;

public interface SpecificationTypeManager {
    GetAllSpecificationTypeResponse getAllSpecificationType();
    CreateSpecificationTypeResponse createSpecificationType(CreateSpecificationTypeRequest request);
    void deleteSpecificationType(Long id);
    SpecificationType getSpecificationType(Long id);
    List<SpecificationType> getSpecificationTypesByComponentId(Long componentId);
    GetSpecificationTypeByComponentTypeResponse getSpecificationTypesByComponentTypeId(Long componentTypeId, int currentPage, int itemsPerPage);
}
