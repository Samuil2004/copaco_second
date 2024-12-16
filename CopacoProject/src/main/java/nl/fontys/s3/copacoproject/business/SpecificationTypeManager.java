package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.CreateSpecificationTypeRequest;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.CreateSpecificationTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.GetAllSpecificationTypeResponse;
import nl.fontys.s3.copacoproject.domain.SpecificationType;

import java.util.List;

public interface SpecificationTypeManager {
    GetAllSpecificationTypeResponse getAllSpecificationType();
    CreateSpecificationTypeResponse createSpecificationType(CreateSpecificationTypeRequest request);
    void deleteSpecificationType(Long id);
    SpecificationType getSpecificationType(Long id);
    List<SpecificationType> getSpecificationTypesByComponentId(Long componentId);
    List<SpecificationType> getSpecificationTypesByComponentTypeId(Long componentTypeId, int currentPage, int itemsPerPage);
}
