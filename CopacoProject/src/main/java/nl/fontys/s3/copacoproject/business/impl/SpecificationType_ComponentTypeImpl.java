package nl.fontys.s3.copacoproject.business.impl;

import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.SpecificationType_ComponentType;
import nl.fontys.s3.copacoproject.persistence.SpecificationTypeComponentTypeRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SpecificationType_ComponentTypeImpl implements SpecificationType_ComponentType {
    private final SpecificationTypeComponentTypeRepository specificationTypeComponentTypeRepository;

    @Override
    public Long findIdByComponentTypeIdAndSpecificationTypeId(Long componentTypeId, Long specificationTypeId) {
        return specificationTypeComponentTypeRepository.findIdByComponentTypeIdAndSpecificationTypeId(componentTypeId, specificationTypeId);
    }
}
