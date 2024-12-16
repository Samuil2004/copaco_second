package nl.fontys.s3.copacoproject.business.impl;

import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.Exceptions.InvalidInputException;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.SpecificationsManager;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.GetDistinctConfigurationTypesInCategoryRequest;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.GetDistinctConfigurationTypesInCategoryResponse;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.GetDistinctConfigurationTypesResponse;
import nl.fontys.s3.copacoproject.persistence.ComponentSpecificationListRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.entity.SpecficationTypeList_ComponentTypeEntity;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeList_ComponentTypeRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class SpecificationsManagerImpl implements SpecificationsManager {
    private final ComponentSpecificationListRepository componentSpecificationListRepository;
    private final ComponentTypeRepository componentTypeRepository;
    private final SpecificationTypeList_ComponentTypeRepository specificationTypeList_ComponentTypeRepository;

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

    @Override
    public List<String> getSpecificationValuesOfSpecificationTypeByComponentType(Long componentTypeId, Long specificationTypeId, int currentPage, int itemsPerPage) {
        if(!componentTypeRepository.existsById(componentTypeId)){
            throw new InvalidInputException("Requested component type does not exist");
        }
        SpecficationTypeList_ComponentTypeEntity item = specificationTypeList_ComponentTypeRepository.findByComponentTypeAndSpecificationType(componentTypeId, specificationTypeId);
        if(item == null){
            throw new InvalidInputException("Specification type does not exist");
        }

        Pageable pageable = PageRequest.of(currentPage-1, itemsPerPage);

        return specificationTypeList_ComponentTypeRepository.findSpecificationValuesBySpecificationTypeAndComponentType(item, pageable).getContent();
    }
}
