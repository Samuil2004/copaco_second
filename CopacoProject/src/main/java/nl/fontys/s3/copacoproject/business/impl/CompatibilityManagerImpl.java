package nl.fontys.s3.copacoproject.business.impl;

import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.CompatibilityManager;
import nl.fontys.s3.copacoproject.business.Exceptions.CompatibilityError;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectExistsAlreadyException;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.converters.CompatibilityTypeConverter;
import nl.fontys.s3.copacoproject.business.converters.ComponentTypeConverter;
import nl.fontys.s3.copacoproject.business.dto.CreateAutomaticCompatibilityDtoRequest;
import nl.fontys.s3.copacoproject.business.dto.CreateAutomaticCompatibilityDtoResponse;
import nl.fontys.s3.copacoproject.business.dto.CreateManualCompatibilityDtoRequest;
import nl.fontys.s3.copacoproject.business.dto.GetAutomaticCompatibilityByIdResponse;
import nl.fontys.s3.copacoproject.business.dto.userDto.CreateManualCompatibilityDtoResponse;
import nl.fontys.s3.copacoproject.domain.*;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.entity.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        if(createAutomaticCompatibilityDtoRequest.getComponentType1Id().equals(createAutomaticCompatibilityDtoRequest.getComponentType2Id())){
            throw new CompatibilityError("Component types must be different");
        }

        Optional<ComponentTypeEntity> componentType1 = componentTypeRepository.findById(createAutomaticCompatibilityDtoRequest.getComponentType1Id());
        Optional<ComponentTypeEntity> componentType2 = componentTypeRepository.findById(createAutomaticCompatibilityDtoRequest.getComponentType2Id());

        if(componentType1.isPresent() && componentType2.isPresent()) {
            //find the required specificationType of each componentType
            Optional<SpecficationTypeList_ComponentTypeEntity> sp1 = specificationTypeList_ComponentTypeRepository.findById(createAutomaticCompatibilityDtoRequest.getSpecificationToConsiderId_from_component1());
            Optional<SpecficationTypeList_ComponentTypeEntity> sp2 = specificationTypeList_ComponentTypeRepository.findById(createAutomaticCompatibilityDtoRequest.getSpecificationToConsiderId_from_component2());

            if(sp1.isEmpty() || sp2.isEmpty())
            {
                throw new ObjectNotFound("Specification not found");
            }

            List<RuleEntity> foundRulesForThisCombinationOfSpecifications = ruleEntityRepository.findBySpecificationToConsider1IdIdAndSpecificationToConsider2IdIdAndConfigurationType(sp1.get().getId(), sp2.get().getId(), createAutomaticCompatibilityDtoRequest.getConfigurationType());
            if(!foundRulesForThisCombinationOfSpecifications.isEmpty())
            {
                throw new ObjectExistsAlreadyException("Rules for selected specifications already exist in this configuration type");
            }
            RuleEntity ruleEntity = RuleEntity.builder()
                    .specificationToConsider1Id(sp1.get())
                    .specificationToConsider2Id(sp2.get())
                    .configurationType(createAutomaticCompatibilityDtoRequest.getConfigurationType())
                    .build();

            RuleEntity returnedRuleEntity = ruleEntityRepository.save(ruleEntity);

            CompatibilityEntity compatibilityEntity = CompatibilityEntity.builder()
                    .component1Id(componentType1.get())
                    .component2Id(componentType2.get())
                    .ruleId(returnedRuleEntity)
                    .configurationType(createAutomaticCompatibilityDtoRequest.getConfigurationType())
                    .build();

            CompatibilityEntity returnedEntity =  automaticCompatibilityRepository.save(compatibilityEntity);
            return CreateAutomaticCompatibilityDtoResponse.builder().automaticCompatibility(returnedEntity).build();
        }

        throw new ObjectNotFound("ERROR SAVING AUTOMATIC COMPATIBILITY");
    }

    @Override
    public CreateManualCompatibilityDtoResponse createManualCompatibility(CreateManualCompatibilityDtoRequest createManualCompatibilityDtoRequest) {
        if(createManualCompatibilityDtoRequest.getComponentType1Id().equals(createManualCompatibilityDtoRequest.getComponentType2Id())){
            throw new CompatibilityError("Component types must be different");
        }

        Optional<ComponentTypeEntity> componentType1 = componentTypeRepository.findById(createManualCompatibilityDtoRequest.getComponentType1Id());
        Optional<ComponentTypeEntity> componentType2 = componentTypeRepository.findById(createManualCompatibilityDtoRequest.getComponentType2Id());

        if(componentType1.isPresent() && componentType2.isPresent()) {
            Optional<SpecficationTypeList_ComponentTypeEntity> sp1 = specificationTypeList_ComponentTypeRepository.findById(createManualCompatibilityDtoRequest.getSpecificationToConsiderId_from_component1());
            Optional<SpecficationTypeList_ComponentTypeEntity> sp2 = specificationTypeList_ComponentTypeRepository.findById(createManualCompatibilityDtoRequest.getSpecificationToConsiderId_from_component2());

            if(sp1.isEmpty() || sp2.isEmpty())
            {
                throw new ObjectNotFound("Specification not found");
            }

            List<RuleEntity> foundRulesForThisCombinationOfSpecifications = ruleEntityRepository.findBySpecificationToConsider1IdIdAndSpecificationToConsider2IdId(sp1.get().getId(), sp2.get().getId());
            if(!foundRulesForThisCombinationOfSpecifications.isEmpty())
            {
                boolean alreadyExistsAutomaticRuleForThisSpecification = foundRulesForThisCombinationOfSpecifications.stream()
                        .anyMatch(rule -> rule.getValueOfFirstSpecification() == null);

                if(alreadyExistsAutomaticRuleForThisSpecification) {
                    throw new ObjectExistsAlreadyException("Automatic compatibility rule for this specifications already exist");
                }
                //In case there it already exists a rule for this specification value, it should be updated instead of added
                boolean alreadyExistsARuleForThisSpecification = foundRulesForThisCombinationOfSpecifications.stream()
                        .anyMatch(rule -> createManualCompatibilityDtoRequest.getValueForTheFirstSpecification().equals(rule.getValueOfFirstSpecification()));

                if(alreadyExistsARuleForThisSpecification) {
                    throw new ObjectExistsAlreadyException("Rules for selected specifications already exist");
                }

            }
            RuleEntity ruleEntity = RuleEntity.builder()
                    .specificationToConsider1Id(sp1.get())
                    .specificationToConsider2Id(sp2.get())
                    .valueOfFirstSpecification(createManualCompatibilityDtoRequest.getValueForTheFirstSpecification())
                    .valueOfSecondSpecification(String.join(",",createManualCompatibilityDtoRequest.getValuesForTheSecondSpecification()))
                    .build();

            RuleEntity returnedRuleEntity = ruleEntityRepository.save(ruleEntity);

            CompatibilityEntity compatibilityEntity = CompatibilityEntity.builder()
                    .component1Id(componentType1.get())
                    .component2Id(componentType2.get())
                    .ruleId(returnedRuleEntity)
                    .build();

            CompatibilityEntity returnedEntity =  automaticCompatibilityRepository.save(compatibilityEntity);
            return CreateManualCompatibilityDtoResponse.builder().automaticCompatibility(returnedEntity).build();
        }

        throw new ObjectNotFound("ERROR SAVING AUTOMATIC COMPATIBILITY");
    }

    @Override
    public GetAutomaticCompatibilityByIdResponse automaticCompatibilityByCompatibilityId(Long automaticCompatibilityId) {
        Optional<CompatibilityEntity> automaticCompatibilityEntityOptional = automaticCompatibilityRepository.findById(automaticCompatibilityId);
        if(automaticCompatibilityEntityOptional.isPresent()) {
            CompatibilityEntity compatibilityEntity = automaticCompatibilityEntityOptional.get();
            ComponentType componentTypeBase1 = ComponentTypeConverter.convertFromEntityToBase(compatibilityEntity.getComponent1Id());
            ComponentType componentTypeBase2 = ComponentTypeConverter.convertFromEntityToBase(compatibilityEntity.getComponent2Id());

            SpecificationType specificationType1 = SpecificationType.builder()
                    .specificationTypeId(compatibilityEntity.getRuleId().getSpecificationToConsider1Id().getSpecificationType().getId())
                    .specificationTypeName(compatibilityEntity.getRuleId().getSpecificationToConsider1Id().getSpecificationType().getSpecificationTypeName())
                    .build();

            SpecificationType specificationType2 = SpecificationType.builder()
                    .specificationTypeId(compatibilityEntity.getRuleId().getSpecificationToConsider2Id().getSpecificationType().getId())
                    .specificationTypeName(compatibilityEntity.getRuleId().getSpecificationToConsider2Id().getSpecificationType().getSpecificationTypeName())
                    .build();


            SpecificationType_ComponentType specification1 = SpecificationType_ComponentType.builder()
                    .id(compatibilityEntity.getRuleId().getSpecificationToConsider1Id().getId())
                    .componentType(componentTypeBase1)
                    .specificationType(specificationType1)
                    .build();

            SpecificationType_ComponentType specification2 = SpecificationType_ComponentType.builder()
                    .id(compatibilityEntity.getRuleId().getSpecificationToConsider2Id().getId())
                    .componentType(componentTypeBase2)
                    .specificationType(specificationType2)
                    .build();

            Rule ruleBase = Rule.builder()
                    .id(compatibilityEntity.getRuleId().getId())
                    .specificationToConsider1Id(specification1)
                    .specificationToConsider2Id(specification2)
                    .build();

            AutomaticCompatibility automaticCompatibility = AutomaticCompatibility.builder()
                    .id(compatibilityEntity.getId())
                    .component1Id(compatibilityEntity.getComponent1Id().getId())
                    .component2Id(compatibilityEntity.getComponent2Id().getId())
                    .rule(ruleBase)
                    .build();

            GetAutomaticCompatibilityByIdResponse response = GetAutomaticCompatibilityByIdResponse.builder()
                    .automaticCompatibilityId(automaticCompatibility.getId())
                    .componentType1Id(automaticCompatibility.getComponent1Id())
                    .componentType2Id(automaticCompatibility.getComponent2Id())
                    .specificationTypeFromComponentType1(automaticCompatibility.getRule().getSpecificationToConsider1Id().getComponentType().getSpecificationTypeList().stream().filter(specificationType -> specificationType.getSpecificationTypeId().equals(automaticCompatibility.getRule().getSpecificationToConsider1Id().getSpecificationType().getSpecificationTypeId())).findFirst().orElse(null))
                    .specificationTypeFromComponentType2(automaticCompatibility.getRule().getSpecificationToConsider2Id().getComponentType().getSpecificationTypeList().stream().filter(specificationType -> specificationType.getSpecificationTypeId().equals(automaticCompatibility.getRule().getSpecificationToConsider2Id().getSpecificationType().getSpecificationTypeId())).findFirst().orElse(null))
                    .build();

            return response;
        }
        throw new ObjectNotFound("ERROR FINDING AUTOMATIC COMPATIBILITY");

    }

    @Override
    public List<GetAutomaticCompatibilityByIdResponse> allCompatibilitiesForComponentTypeByComponentTypeId(Long componentTypeId) {
        List<CompatibilityEntity> automaticCompatibilities = automaticCompatibilityRepository.findByComponent1Id_IdOrComponent2Id_Id(componentTypeId, componentTypeId);
        List<GetAutomaticCompatibilityByIdResponse> listOFCompatibilityBetweenComponentTypesByGivenId = new ArrayList<>();
        for (CompatibilityEntity compatibilityEntity : automaticCompatibilities) {
            ComponentType componentTypeBase1 = ComponentTypeConverter.convertFromEntityToBase(compatibilityEntity.getComponent1Id());
            ComponentType componentTypeBase2 = ComponentTypeConverter.convertFromEntityToBase(compatibilityEntity.getComponent2Id());

            SpecificationType specificationType1 = SpecificationType.builder()
                    .specificationTypeId(compatibilityEntity.getRuleId().getSpecificationToConsider1Id().getSpecificationType().getId())
                    .specificationTypeName(compatibilityEntity.getRuleId().getSpecificationToConsider1Id().getSpecificationType().getSpecificationTypeName())
                    .build();

            SpecificationType specificationType2 = SpecificationType.builder()
                    .specificationTypeId(compatibilityEntity.getRuleId().getSpecificationToConsider2Id().getSpecificationType().getId())
                    .specificationTypeName(compatibilityEntity.getRuleId().getSpecificationToConsider2Id().getSpecificationType().getSpecificationTypeName())
                    .build();


            SpecificationType_ComponentType specification1 = SpecificationType_ComponentType.builder()
                    .id(compatibilityEntity.getRuleId().getSpecificationToConsider1Id().getId())
                    .componentType(componentTypeBase1)
                    .specificationType(specificationType1)
                    .build();

            SpecificationType_ComponentType specification2 = SpecificationType_ComponentType.builder()
                    .id(compatibilityEntity.getRuleId().getSpecificationToConsider2Id().getId())
                    .componentType(componentTypeBase2)
                    .specificationType(specificationType2)
                    .build();

            Rule ruleBase = Rule.builder()
                    .id(compatibilityEntity.getRuleId().getId())
                    .specificationToConsider1Id(specification1)
                    .specificationToConsider2Id(specification2)
                    .build();

            AutomaticCompatibility automaticCompatibility = AutomaticCompatibility.builder()
                    .id(compatibilityEntity.getId())
                    .component1Id(compatibilityEntity.getComponent1Id().getId())
                    .component2Id(compatibilityEntity.getComponent2Id().getId())
                    .rule(ruleBase)
                    .build();

            GetAutomaticCompatibilityByIdResponse response = GetAutomaticCompatibilityByIdResponse.builder()
                    .automaticCompatibilityId(automaticCompatibility.getId())
                    .componentType1Id(automaticCompatibility.getComponent1Id())
                    .componentType2Id(automaticCompatibility.getComponent2Id())
                    .specificationTypeFromComponentType1(automaticCompatibility.getRule().getSpecificationToConsider1Id().getComponentType().getSpecificationTypeList().stream().filter(specificationType -> specificationType.getSpecificationTypeId().equals(automaticCompatibility.getRule().getSpecificationToConsider1Id().getSpecificationType().getSpecificationTypeId())).findFirst().orElse(null))
                    .specificationTypeFromComponentType2(automaticCompatibility.getRule().getSpecificationToConsider2Id().getComponentType().getSpecificationTypeList().stream().filter(specificationType -> specificationType.getSpecificationTypeId().equals(automaticCompatibility.getRule().getSpecificationToConsider2Id().getSpecificationType().getSpecificationTypeId())).findFirst().orElse(null))
                    .build();
            listOFCompatibilityBetweenComponentTypesByGivenId.add(response);
        }
        return listOFCompatibilityBetweenComponentTypesByGivenId;
    }

}
