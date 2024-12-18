package nl.fontys.s3.copacoproject.business.impl;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.Exceptions.InvalidInputException;
import nl.fontys.s3.copacoproject.business.SpecificationTypeManager;
import nl.fontys.s3.copacoproject.business.converters.SpecificationTypeConverter;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.CreateSpecificationTypeRequest;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.CreateSpecificationTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.GetAllSpecificationTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.GetSpecificationTypeByComponentTypeResponse;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.SpecificationTypeRepository;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class SpecificationTypeManagerImpl implements SpecificationTypeManager {
    private final SpecificationTypeRepository specificationTypeRepository;
    private final ComponentRepository componentRepository;
    private final ComponentTypeRepository componentTypeRepository;

    @Override
    public GetAllSpecificationTypeResponse getAllSpecificationType() {
        List<SpecificationTypeEntity> specificationTypeEntities = specificationTypeRepository.findAll();
        List<SpecificationType> specificationTypeList = specificationTypeEntities.stream()
                .map(SpecificationTypeConverter::convertFromEntityToBase).collect(toList());
        return GetAllSpecificationTypeResponse.builder().specificationTypes(specificationTypeList).build();
    }
    @Override
    public CreateSpecificationTypeResponse createSpecificationType(CreateSpecificationTypeRequest request){
        if (request.getSpecificationTypeName() == null) {
            throw new IllegalArgumentException("Specification type name must not be null");
        }
        SpecificationTypeEntity specificationTypeEntity = SpecificationTypeEntity.builder()
                .specificationTypeName(request.getSpecificationTypeName())
                .build();
        specificationTypeEntity = specificationTypeRepository.save(specificationTypeEntity);
        SpecificationType specificationType = SpecificationTypeConverter
                .convertFromEntityToBase(specificationTypeEntity);
        return CreateSpecificationTypeResponse.builder()
                .specificationTypeId(specificationType.getSpecificationTypeId())
                .build();
    }
    @Override
    public SpecificationType getSpecificationType(Long id){
        SpecificationTypeEntity specificationTypeEntity = specificationTypeRepository.findById(id).orElse(null);
        if (specificationTypeEntity == null) {
            throw new IllegalArgumentException("Specification type not found");
        }
        return SpecificationTypeConverter.convertFromEntityToBase(specificationTypeEntity);
    }

    @Override
    public List<SpecificationType> getSpecificationTypesByComponentId(Long componentId) {
        if(!componentRepository.existsById(componentId)) {
            throw new IllegalArgumentException("Component not found");
        }
        List<SpecificationType> specifications = new ArrayList<>();
        List<SpecificationTypeEntity> specificationTypeEntities = specificationTypeRepository.findSpecificationTypeEntitiesByComponentId(componentId);
        for(SpecificationTypeEntity specificationTypeEntity : specificationTypeEntities) {
            specifications.add(SpecificationTypeConverter.convertFromEntityToBase(specificationTypeEntity));
        }
        return specifications;
    }

    @Override
    public GetSpecificationTypeByComponentTypeResponse getSpecificationTypesByComponentTypeId(Long componentTypeId, int currentPage, int itemsPerPage) {
        if(!componentTypeRepository.existsById(componentTypeId)) {
            throw new IllegalArgumentException("Component not found");
        }
        if(itemsPerPage > 20){
            throw new InvalidInputException("Too many items to load!");
        }
        List<SpecificationType> specifications = new ArrayList<>();
        Pageable pageable = PageRequest.of(currentPage-1, itemsPerPage, Sort.by("id").descending());
        Page<SpecificationTypeEntity> specificationTypeEntities = specificationTypeRepository.findSpecificationTypeEntitiesByComponentTypeId(componentTypeId, pageable);
        for(SpecificationTypeEntity specificationTypeEntity : specificationTypeEntities) {
            specifications.add(SpecificationTypeConverter.convertFromEntityToBase(specificationTypeEntity));
        }
        int totalNumberOfItems = specificationTypeRepository.countSpecificationTypesByComponentTypeId(componentTypeId);
        return GetSpecificationTypeByComponentTypeResponse.builder()
                .specificationTypes(specifications)
                .totalNumberOfItems(totalNumberOfItems)
                .build();
    }

    @Override
    public void deleteSpecificationType(Long id){
        specificationTypeRepository.deleteById(id);
    }
}
