package nl.fontys.s3.copacoproject.business.impl;

import lombok.AllArgsConstructor;
import lombok.Setter;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.SpecificationsManager;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.GetDistinctConfigurationTypesInCategoryRequest;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.GetDistinctConfigurationTypesInCategoryResponse;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.GetDistinctConfigurationTypesResponse;
import nl.fontys.s3.copacoproject.persistence.ComponentSpecificationListRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class SpecificationsManagerImpl implements SpecificationsManager {
    private final ComponentSpecificationListRepository componentSpecificationListRepository;
    @Override
    public GetDistinctConfigurationTypesResponse getDistinctConfigurationTypes() {
        List<String> distinctConfigurationTypes = componentSpecificationListRepository.getDistinctConfigurationTypes();
        if(distinctConfigurationTypes.isEmpty())
        {
            throw new ObjectNotFound("No configuration types were found");
        }
        return GetDistinctConfigurationTypesResponse.builder().distinctConfigurationTypes(distinctConfigurationTypes).build();
    }

    @Override
    public GetDistinctConfigurationTypesInCategoryResponse getDistinctConfigurationTypesInCategory(GetDistinctConfigurationTypesInCategoryRequest request) {
        List<String> distinctConfigurationTypesFromCategory = componentSpecificationListRepository.getDistinctConfigurationTypesInCategory(request.getCategoryId());
        if(distinctConfigurationTypesFromCategory.isEmpty())
        {
            throw new ObjectNotFound("No configuration types were found within the selected category");
        }
        return GetDistinctConfigurationTypesInCategoryResponse.builder().distinctConfigurationTypesInCategory(distinctConfigurationTypesFromCategory).build();
    }
}
