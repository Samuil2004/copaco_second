package nl.fontys.s3.copacoproject.business.impl;

import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.CompatibilityManager;
import nl.fontys.s3.copacoproject.business.Exceptions.ComponentTypeNotFound;
import nl.fontys.s3.copacoproject.business.converters.CompatibilityTypeConverter;
import nl.fontys.s3.copacoproject.business.dto.CreateAutomaticCompatibilityDtoRequest;
import nl.fontys.s3.copacoproject.business.dto.CreateAutomaticCompatibilityDtoResponse;
import nl.fontys.s3.copacoproject.domain.AutomaticCompatibility;
import nl.fontys.s3.copacoproject.domain.CompatibilityType;
import nl.fontys.s3.copacoproject.domain.ComponentType;
import nl.fontys.s3.copacoproject.domain.Rule;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.entity.*;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor

public class CompatibilityManagerImpl implements CompatibilityManager {
    private final CompatibilityTypeEntityRepository compatibilityTypeEntityRepository;
    private final AutomaticCompatibilityRepository automaticCompatibilityRepository;
    private final SpecificationTypeList_ComponentTypeRepository specificationTypeList_ComponentTypeRepository;
    private final ComponentTypeRepository componentTypeRepository;
    private final RuleEntityRepository ruleEntityRepository;
    @Override
    public List<CompatibilityType> allCompatibilityTypes() {
        List<CompatibilityTypeEntity> allCompatibilityTypesEntity =  compatibilityTypeEntityRepository.findAll();
        return allCompatibilityTypesEntity.stream().map(CompatibilityTypeConverter::convertFromEntityToBase).toList();
    }

    @Override
    public CreateAutomaticCompatibilityDtoResponse createAutomaticCompatibility(CreateAutomaticCompatibilityDtoRequest createAutomaticCompatibilityDtoRequest) {

        Optional<SpecficationTypeList_ComponentTypeEntity> specificationEntity1 = specificationTypeList_ComponentTypeRepository.findById(createAutomaticCompatibilityDtoRequest.getSpecificationToConsiderId_from_component1());
        Optional<SpecficationTypeList_ComponentTypeEntity> specificationEntity2 = specificationTypeList_ComponentTypeRepository.findById(createAutomaticCompatibilityDtoRequest.getSpecificationToConsiderId_from_component2());
        Optional<ComponentTypeEntity> componentType1 = componentTypeRepository.findById(createAutomaticCompatibilityDtoRequest.getComponentType1Id());
        Optional<ComponentTypeEntity> componentType2 = componentTypeRepository.findById(createAutomaticCompatibilityDtoRequest.getComponentType2Id());

        if(specificationEntity1.isPresent() && specificationEntity2.isPresent() && componentType1.isPresent() && componentType2.isPresent()) {
            RuleEntity ruleEntity = RuleEntity.builder().specificationToConsider1Id(specificationEntity1.get()).specificationToConsider2Id(specificationEntity2.get()).build();
            RuleEntity returnedRuleEntity = ruleEntityRepository.save(ruleEntity);
            AutomaticCompatibilityEntity automaticCompatibilityEntity = AutomaticCompatibilityEntity.builder()
                    .component1Id(componentType1.get())
                    .component2Id(componentType2.get())
                    .ruleId(returnedRuleEntity)
                    .build();

            AutomaticCompatibilityEntity returnedEntity =  automaticCompatibilityRepository.save(automaticCompatibilityEntity);
            return CreateAutomaticCompatibilityDtoResponse.builder().automaticCompatibility(returnedEntity).build();
        }

        throw new ComponentTypeNotFound("ERROR SAVING AUTOMATIC COMPATIBILITY");
    }
}
