package nl.fontys.s3.copacoproject.business.impl;

import nl.fontys.s3.copacoproject.business.Exceptions.ObjectExistsAlreadyException;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.dto.CreateAutomaticCompatibilityDtoRequest;
import nl.fontys.s3.copacoproject.business.dto.CreateAutomaticCompatibilityDtoResponse;
import nl.fontys.s3.copacoproject.business.dto.CreateManualCompatibilityDtoRequest;
import nl.fontys.s3.copacoproject.business.dto.GetAutomaticCompatibilityByIdResponse;
import nl.fontys.s3.copacoproject.business.dto.rule.RuleResponse;
import nl.fontys.s3.copacoproject.business.dto.rule.UpdateRuleRequest;
import nl.fontys.s3.copacoproject.business.dto.userDto.CreateManualCompatibilityDtoResponse;
import nl.fontys.s3.copacoproject.domain.CompatibilityType;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompatibilityManagerImplTest {

    @Mock
    private CompatibilityTypeEntityRepository mockCompatibilityTypeEntityRepository;
    @Mock
    private CompatibilityRepository mockCompatibilityRepository;
    @Mock
    private SpecificationTypeList_ComponentTypeRepository mockSpecificationTypeList_ComponentTypeRepository;
    @Mock
    private ComponentTypeRepository mockComponentTypeRepository;
    @Mock
    private RuleEntityRepository mockRuleEntityRepository;

    private CompatibilityManagerImpl compatibilityManagerImplUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        compatibilityManagerImplUnderTest = new CompatibilityManagerImpl(mockCompatibilityTypeEntityRepository,
                mockCompatibilityRepository, mockSpecificationTypeList_ComponentTypeRepository,
                mockComponentTypeRepository, mockRuleEntityRepository);
    }

    @Test
    void testAllCompatibilityTypes() {
        // Setup
        List<CompatibilityType> expected = new ArrayList<>(List.of(CompatibilityType.builder()
                .id(0L)
                .typeOfCompatibility("typeOfCompatibility").build()));
        // Configure CompatibilityTypeEntityRepository.findAll(...).
        final List<CompatibilityTypeEntity> compatibilityTypeEntities = List.of(CompatibilityTypeEntity.builder()
                .id(0L)
                .typeOfCompatibility("typeOfCompatibility")
                .build());
        when(mockCompatibilityTypeEntityRepository.findAll()).thenReturn(compatibilityTypeEntities);

        // Run the test
        final List<CompatibilityType> result = compatibilityManagerImplUnderTest.allCompatibilityTypes();

        // Verify the results
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void testAllCompatibilityTypes_CompatibilityTypeEntityRepositoryReturnsNoItems() {
        // Setup
        when(mockCompatibilityTypeEntityRepository.findAll()).thenReturn(Collections.emptyList());

        // Run the test
        final List<CompatibilityType> result = compatibilityManagerImplUnderTest.allCompatibilityTypes();

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testCreateAutomaticCompatibility_ThrowsObjectExistsAlreadyException() {
        // Setup
        final CreateAutomaticCompatibilityDtoRequest createAutomaticCompatibilityDtoRequest = CreateAutomaticCompatibilityDtoRequest.builder()
                .componentType1Id(0L)
                .componentType2Id(1L)
                .specificationToConsiderId_from_component1(0L)
                .specificationToConsiderId_from_component2(1L)
                .configurationType("configurationType")
                .build();

        // Mock ComponentTypeRepository.findById
        final Optional<ComponentTypeEntity> componentTypeEntity1 = Optional.of(ComponentTypeEntity.builder()
                .id(0L)
                .componentTypeName("componentTypeName1")
                .build());
        final Optional<ComponentTypeEntity> componentTypeEntity2 = Optional.of(ComponentTypeEntity.builder()
                .id(1L)
                .componentTypeName("componentTypeName2")
                .build());
        when(mockComponentTypeRepository.findById(0L)).thenReturn(componentTypeEntity1);
        when(mockComponentTypeRepository.findById(1L)).thenReturn(componentTypeEntity2);

        // Mock SpecificationTypeList_ComponentTypeRepository.findById
        final Optional<SpecficationTypeList_ComponentTypeEntity> spec1 = Optional.of(
                SpecficationTypeList_ComponentTypeEntity.builder()
                        .id(0L)
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(0L)
                                .specificationTypeName("specificationTypeName1")
                                .build())
                        .build());
        final Optional<SpecficationTypeList_ComponentTypeEntity> spec2 = Optional.of(
                SpecficationTypeList_ComponentTypeEntity.builder()
                        .id(1L)
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(1L)
                                .specificationTypeName("specificationTypeName2")
                                .build())
                        .build());
        when(mockSpecificationTypeList_ComponentTypeRepository.findById(0L)).thenReturn(spec1);
        when(mockSpecificationTypeList_ComponentTypeRepository.findById(1L)).thenReturn(spec2);

        // Mock RuleEntityRepository.findBySpecificationToConsider1IdIdAndSpecificationToConsider2IdIdAndConfigurationType
        final List<RuleEntity> existingRules = List.of(RuleEntity.builder()
                .id(1L)
                .specificationToConsider1Id(spec1.get())
                .specificationToConsider2Id(spec2.get())
                .configurationType("configurationType")
                .build());
        when(mockRuleEntityRepository.findBySpecificationToConsider1IdIdAndSpecificationToConsider2IdIdAndConfigurationType(
                0L, 1L, "configurationType")).thenReturn(existingRules);

        // Run the test and verify
        assertThatThrownBy(() -> compatibilityManagerImplUnderTest.createAutomaticCompatibility(
                createAutomaticCompatibilityDtoRequest)).isInstanceOf(ObjectExistsAlreadyException.class);

        verify(mockComponentTypeRepository, times(1)).findById(0L);
        verify(mockComponentTypeRepository, times(1)).findById(1L);
        verify(mockSpecificationTypeList_ComponentTypeRepository, times(1)).findById(0L);
        verify(mockSpecificationTypeList_ComponentTypeRepository, times(1)).findById(1L);
        verify(mockRuleEntityRepository, times(1))
                .findBySpecificationToConsider1IdIdAndSpecificationToConsider2IdIdAndConfigurationType(0L, 1L, "configurationType");
    }


    @Test
    void testCreateAutomaticCompatibility_ComponentTypeRepositoryReturnsAbsent() {
        // Setup
        final CreateAutomaticCompatibilityDtoRequest createAutomaticCompatibilityDtoRequest = CreateAutomaticCompatibilityDtoRequest.builder()
                .componentType1Id(0L)
                .componentType2Id(1L)
                .specificationToConsiderId_from_component1(0L)
                .specificationToConsiderId_from_component2(1L)
                .configurationType("configurationType")
                .build();
        when(mockComponentTypeRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> compatibilityManagerImplUnderTest.createAutomaticCompatibility(
                createAutomaticCompatibilityDtoRequest)).isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testCreateAutomaticCompatibility_SpecificationTypeList_ComponentTypeRepositoryReturnsAbsent() {
        // Setup
        final CreateAutomaticCompatibilityDtoRequest createAutomaticCompatibilityDtoRequest = CreateAutomaticCompatibilityDtoRequest.builder()
                .componentType1Id(0L)
                .componentType2Id(1L)
                .specificationToConsiderId_from_component1(0L)
                .specificationToConsiderId_from_component2(1L)
                .configurationType("configurationType")
                .build();

        // Mock ComponentTypeRepository.findById
        final Optional<ComponentTypeEntity> componentTypeEntity1 = Optional.of(ComponentTypeEntity.builder()
                .id(0L)
                .componentTypeName("componentTypeName1")
                .build());
        final Optional<ComponentTypeEntity> componentTypeEntity2 = Optional.of(ComponentTypeEntity.builder()
                .id(1L)
                .componentTypeName("componentTypeName2")
                .build());
        when(mockComponentTypeRepository.findById(0L)).thenReturn(componentTypeEntity1);
        when(mockComponentTypeRepository.findById(1L)).thenReturn(componentTypeEntity2);

        // Mock SpecificationTypeList_ComponentTypeRepository.findById
        when(mockSpecificationTypeList_ComponentTypeRepository.findById(0L)).thenReturn(Optional.empty());
        when(mockSpecificationTypeList_ComponentTypeRepository.findById(1L)).thenReturn(Optional.of(
                SpecficationTypeList_ComponentTypeEntity.builder()
                        .id(1L)
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(1L)
                                .specificationTypeName("specificationTypeName2")
                                .build())
                        .build()
        ));

        // Run the test and verify
        assertThatThrownBy(() -> compatibilityManagerImplUnderTest.createAutomaticCompatibility(
                createAutomaticCompatibilityDtoRequest)).isInstanceOf(ObjectNotFound.class);

        verify(mockComponentTypeRepository, times(1)).findById(0L);
        verify(mockComponentTypeRepository, times(1)).findById(1L);
        verify(mockSpecificationTypeList_ComponentTypeRepository, times(1)).findById(0L);
        verify(mockSpecificationTypeList_ComponentTypeRepository, times(1)).findById(1L);
    }


    @Test
    void testCreateAutomaticCompatibility_RuleEntityRepositoryFindBySpecificationToConsider1IdIdAndSpecificationToConsider2IdIdAndConfigurationTypeReturnsNoItems() {
        // Setup
        final CreateAutomaticCompatibilityDtoRequest createAutomaticCompatibilityDtoRequest = CreateAutomaticCompatibilityDtoRequest.builder()
                .componentType1Id(1L)
                .componentType2Id(2L) // Ensuring different component types
                .specificationToConsiderId_from_component1(1L)
                .specificationToConsiderId_from_component2(2L)
                .configurationType("configurationType")
                .build();

        // Mock ComponentTypeRepository.findById
        final Optional<ComponentTypeEntity> componentTypeEntity1 = Optional.of(ComponentTypeEntity.builder()
                .id(1L)
                .componentTypeName("componentTypeName1")
                .build());
        final Optional<ComponentTypeEntity> componentTypeEntity2 = Optional.of(ComponentTypeEntity.builder()
                .id(2L)
                .componentTypeName("componentTypeName2")
                .build());
        when(mockComponentTypeRepository.findById(1L)).thenReturn(componentTypeEntity1);
        when(mockComponentTypeRepository.findById(2L)).thenReturn(componentTypeEntity2);

        // Mock SpecificationTypeList_ComponentTypeRepository.findById
        final Optional<SpecficationTypeList_ComponentTypeEntity> specificationType1 = Optional.of(
                SpecficationTypeList_ComponentTypeEntity.builder()
                        .id(1L)
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(1L)
                                .specificationTypeName("specificationTypeName1")
                                .build())
                        .build());
        final Optional<SpecficationTypeList_ComponentTypeEntity> specificationType2 = Optional.of(
                SpecficationTypeList_ComponentTypeEntity.builder()
                        .id(2L)
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(2L)
                                .specificationTypeName("specificationTypeName2")
                                .build())
                        .build());
        when(mockSpecificationTypeList_ComponentTypeRepository.findById(1L)).thenReturn(specificationType1);
        when(mockSpecificationTypeList_ComponentTypeRepository.findById(2L)).thenReturn(specificationType2);

        // Mock RuleEntityRepository.findBySpecificationToConsider1IdIdAndSpecificationToConsider2IdIdAndConfigurationType
        when(mockRuleEntityRepository.findBySpecificationToConsider1IdIdAndSpecificationToConsider2IdIdAndConfigurationType(
                1L, 2L, "configurationType")).thenReturn(Collections.emptyList());

        // Mock RuleEntityRepository.save
        final RuleEntity savedRuleEntity = RuleEntity.builder()
                .id(1L)
                .specificationToConsider1Id(specificationType1.get())
                .specificationToConsider2Id(specificationType2.get())
                .configurationType("configurationType")
                .build();
        when(mockRuleEntityRepository.save(any(RuleEntity.class))).thenReturn(savedRuleEntity);

        // Mock CompatibilityRepository.save
        final CompatibilityEntity savedCompatibilityEntity = CompatibilityEntity.builder()
                .id(1L)
                .component1Id(componentTypeEntity1.get())
                .component2Id(componentTypeEntity2.get())
                .ruleId(savedRuleEntity)
                .configurationType("configurationType")
                .build();
        when(mockCompatibilityRepository.save(any(CompatibilityEntity.class))).thenReturn(savedCompatibilityEntity);

        // Run the test
        final CreateAutomaticCompatibilityDtoResponse result = compatibilityManagerImplUnderTest.createAutomaticCompatibility(
                createAutomaticCompatibilityDtoRequest);

        // Verify the results
        assertThat(result).isNotNull();
        assertThat(result.getAutomaticCompatibility()).isNotNull();
        assertThat(result.getAutomaticCompatibility().getId()).isEqualTo(savedCompatibilityEntity.getId());
        assertThat(result.getAutomaticCompatibility().getRuleId().getId()).isEqualTo(savedRuleEntity.getId());
        assertThat(result.getAutomaticCompatibility().getComponent1Id().getId()).isEqualTo(1L); // Verify first component type
        assertThat(result.getAutomaticCompatibility().getComponent2Id().getId()).isEqualTo(2L); // Verify second component type

        // Verify interactions
        verify(mockComponentTypeRepository, times(1)).findById(1L);
        verify(mockComponentTypeRepository, times(1)).findById(2L);
        verify(mockSpecificationTypeList_ComponentTypeRepository, times(1)).findById(1L);
        verify(mockSpecificationTypeList_ComponentTypeRepository, times(1)).findById(2L);
        verify(mockRuleEntityRepository, times(1)).findBySpecificationToConsider1IdIdAndSpecificationToConsider2IdIdAndConfigurationType(
                1L, 2L, "configurationType");
        verify(mockCompatibilityRepository, times(2)).save(any(CompatibilityEntity.class)); // Direct and reverse compatibility
    }



    @Test
    void testCreateManualCompatibility() {
        // Setup
        final CreateManualCompatibilityDtoRequest createManualCompatibilityDtoRequest = CreateManualCompatibilityDtoRequest.builder()
                .componentType1Id(1L)
                .componentType2Id(2L)
                .specificationToConsiderId_from_component1(1L)
                .specificationToConsiderId_from_component2(2L)
                .valueForTheFirstSpecification("valueForTheFirstSpecification")
                .valuesForTheSecondSpecification(List.of("value"))
                .configurationType("configurationType")
                .build();

        // Mock ComponentTypeRepository.findById
        final Optional<ComponentTypeEntity> componentTypeEntity1 = Optional.of(ComponentTypeEntity.builder()
                .id(1L)
                .componentTypeName("componentTypeName1")
                .build());
        final Optional<ComponentTypeEntity> componentTypeEntity2 = Optional.of(ComponentTypeEntity.builder()
                .id(2L)
                .componentTypeName("componentTypeName2")
                .build());
        when(mockComponentTypeRepository.findById(1L)).thenReturn(componentTypeEntity1);
        when(mockComponentTypeRepository.findById(2L)).thenReturn(componentTypeEntity2);

        // Mock SpecificationTypeList_ComponentTypeRepository.findById
        final Optional<SpecficationTypeList_ComponentTypeEntity> specificationType1 = Optional.of(
                SpecficationTypeList_ComponentTypeEntity.builder()
                        .id(1L)
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(1L)
                                .specificationTypeName("specificationTypeName1")
                                .build())
                        .build());
        final Optional<SpecficationTypeList_ComponentTypeEntity> specificationType2 = Optional.of(
                SpecficationTypeList_ComponentTypeEntity.builder()
                        .id(2L)
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(2L)
                                .specificationTypeName("specificationTypeName2")
                                .build())
                        .build());
        when(mockSpecificationTypeList_ComponentTypeRepository.findById(1L)).thenReturn(specificationType1);
        when(mockSpecificationTypeList_ComponentTypeRepository.findById(2L)).thenReturn(specificationType2);

        // Mock RuleEntityRepository.findBySpecificationToConsider1IdIdAndSpecificationToConsider2IdIdAndConfigurationType
        when(mockRuleEntityRepository.findBySpecificationToConsider1IdIdAndSpecificationToConsider2IdIdAndConfigurationType(
                1L, 2L, "configurationType")).thenReturn(Collections.emptyList());

        // Mock RuleEntityRepository.save
        final RuleEntity savedRuleEntity = RuleEntity.builder()
                .id(1L)
                .specificationToConsider1Id(specificationType1.get())
                .specificationToConsider2Id(specificationType2.get())
                .valueOfFirstSpecification("valueForTheFirstSpecification")
                .valueOfSecondSpecification("value")
                .configurationType("configurationType")
                .build();
        when(mockRuleEntityRepository.save(any(RuleEntity.class))).thenReturn(savedRuleEntity);

        // Mock CompatibilityRepository.save
        final CompatibilityEntity savedCompatibilityEntity = CompatibilityEntity.builder()
                .id(1L)
                .component1Id(componentTypeEntity1.get())
                .component2Id(componentTypeEntity2.get())
                .ruleId(savedRuleEntity)
                .configurationType("configurationType")
                .build();
        when(mockCompatibilityRepository.save(any(CompatibilityEntity.class))).thenReturn(savedCompatibilityEntity);

        // Run the test
        final CreateManualCompatibilityDtoResponse result = compatibilityManagerImplUnderTest.createManualCompatibility(
                createManualCompatibilityDtoRequest);

        // Verify the results
        assertThat(result).isNotNull();
        assertThat(result.getAutomaticCompatibility()).isNotNull();
        assertThat(result.getAutomaticCompatibility().getId()).isEqualTo(1L);
        assertThat(result.getAutomaticCompatibility().getRuleId().getId()).isEqualTo(1L);
        assertThat(result.getAutomaticCompatibility().getComponent1Id().getId()).isEqualTo(1L); // Compare the id of the component
        assertThat(result.getAutomaticCompatibility().getComponent2Id().getId()).isEqualTo(2L); // Compare the id of the component
        assertThat(result.getAutomaticCompatibility().getConfigurationType()).isEqualTo("configurationType");

        // Verify method calls
        verify(mockComponentTypeRepository, times(1)).findById(1L);
        verify(mockComponentTypeRepository, times(1)).findById(2L);
        verify(mockSpecificationTypeList_ComponentTypeRepository, times(1)).findById(1L);
        verify(mockSpecificationTypeList_ComponentTypeRepository, times(1)).findById(2L);
        verify(mockRuleEntityRepository, times(1)).findBySpecificationToConsider1IdIdAndSpecificationToConsider2IdIdAndConfigurationType(
                1L, 2L, "configurationType");
        verify(mockRuleEntityRepository, times(1)).save(any(RuleEntity.class));
        verify(mockCompatibilityRepository, times(1)).save(any(CompatibilityEntity.class));
    }


    @Test
    void testCreateManualCompatibility_ComponentTypeRepositoryReturnsAbsent() {
        // Setup
        final CreateManualCompatibilityDtoRequest createManualCompatibilityDtoRequest = CreateManualCompatibilityDtoRequest.builder()
                .componentType1Id(0L)
                .componentType2Id(1L)
                .specificationToConsiderId_from_component1(0L)
                .specificationToConsiderId_from_component2(1L)
                .valueForTheFirstSpecification("valueForTheFirstSpecification")
                .valuesForTheSecondSpecification(List.of("value"))
                .configurationType("configurationType")
                .build();
        when(mockComponentTypeRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> compatibilityManagerImplUnderTest.createManualCompatibility(
                createManualCompatibilityDtoRequest)).isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testCreateManualCompatibility_SpecificationTypeList_ComponentTypeRepositoryReturnsAbsent() {
        // Setup
        final CreateManualCompatibilityDtoRequest createManualCompatibilityDtoRequest = CreateManualCompatibilityDtoRequest.builder()
                .componentType1Id(0L)
                .componentType2Id(1L)
                .specificationToConsiderId_from_component1(0L)
                .specificationToConsiderId_from_component2(1L)
                .valueForTheFirstSpecification("valueForTheFirstSpecification")
                .valuesForTheSecondSpecification(List.of("value"))
                .configurationType("configurationType")
                .build();

        // Configure ComponentTypeRepository.findById(...).
        final Optional<ComponentTypeEntity> componentTypeEntity = Optional.of(ComponentTypeEntity.builder()
                .id(0L)
                .componentTypeName("componentTypeName")
                .componentTypeImageUrl("componentTypeImageUrl")
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .configurationType("configurationType")
                .specifications(List.of(SpecficationTypeList_ComponentTypeEntity.builder()
                        .id(0L)
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(0L)
                                .specificationTypeName("specificationTypeName")
                                .build())
                        .build()))
                .build());
        when(mockComponentTypeRepository.findById(0L)).thenReturn(componentTypeEntity);

        // Run the test
        assertThatThrownBy(() -> compatibilityManagerImplUnderTest.createManualCompatibility(
                createManualCompatibilityDtoRequest)).isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testCreateManualCompatibility_RuleEntityRepositoryFindBySpecificationToConsider1IdIdAndSpecificationToConsider2IdIdAndConfigurationTypeReturnsNoItems() {
        // Setup
        final CreateManualCompatibilityDtoRequest createManualCompatibilityDtoRequest = CreateManualCompatibilityDtoRequest.builder()
                .componentType1Id(1L)
                .componentType2Id(2L) // Different component types
                .specificationToConsiderId_from_component1(1L)
                .specificationToConsiderId_from_component2(2L)
                .valueForTheFirstSpecification("valueForTheFirstSpecification")
                .valuesForTheSecondSpecification(List.of("value"))
                .configurationType("configurationType")
                .build();

        // Mock ComponentTypeRepository.findById
        final Optional<ComponentTypeEntity> componentTypeEntity1 = Optional.of(ComponentTypeEntity.builder()
                .id(1L)
                .componentTypeName("componentTypeName1")
                .build());
        final Optional<ComponentTypeEntity> componentTypeEntity2 = Optional.of(ComponentTypeEntity.builder()
                .id(2L)
                .componentTypeName("componentTypeName2")
                .build());
        when(mockComponentTypeRepository.findById(1L)).thenReturn(componentTypeEntity1);
        when(mockComponentTypeRepository.findById(2L)).thenReturn(componentTypeEntity2);

        // Mock SpecificationTypeList_ComponentTypeRepository.findById
        final Optional<SpecficationTypeList_ComponentTypeEntity> specificationType1 = Optional.of(
                SpecficationTypeList_ComponentTypeEntity.builder()
                        .id(1L)
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(1L)
                                .specificationTypeName("specificationTypeName1")
                                .build())
                        .build());
        final Optional<SpecficationTypeList_ComponentTypeEntity> specificationType2 = Optional.of(
                SpecficationTypeList_ComponentTypeEntity.builder()
                        .id(2L)
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(2L)
                                .specificationTypeName("specificationTypeName2")
                                .build())
                        .build());
        when(mockSpecificationTypeList_ComponentTypeRepository.findById(1L)).thenReturn(specificationType1);
        when(mockSpecificationTypeList_ComponentTypeRepository.findById(2L)).thenReturn(specificationType2);

        // Mock RuleEntityRepository.findBySpecificationToConsider1IdIdAndSpecificationToConsider2IdIdAndConfigurationType
        when(mockRuleEntityRepository.findBySpecificationToConsider1IdIdAndSpecificationToConsider2IdIdAndConfigurationType(
                1L, 2L, "configurationType")).thenReturn(Collections.emptyList());

        // Mock RuleEntityRepository.save
        final RuleEntity savedRuleEntity = RuleEntity.builder()
                .id(1L)
                .specificationToConsider1Id(specificationType1.get())
                .specificationToConsider2Id(specificationType2.get())
                .valueOfFirstSpecification("valueForTheFirstSpecification")
                .valueOfSecondSpecification("value")
                .configurationType("configurationType")
                .build();
        when(mockRuleEntityRepository.save(any(RuleEntity.class))).thenReturn(savedRuleEntity);

        // Mock CompatibilityRepository.save
        final CompatibilityEntity savedCompatibilityEntity = CompatibilityEntity.builder()
                .id(1L)
                .component1Id(componentTypeEntity1.get())
                .component2Id(componentTypeEntity2.get())
                .ruleId(savedRuleEntity)
                .configurationType("configurationType")
                .build();
        when(mockCompatibilityRepository.save(any(CompatibilityEntity.class))).thenReturn(savedCompatibilityEntity);

        // Run the test
        final CreateManualCompatibilityDtoResponse result = compatibilityManagerImplUnderTest.createManualCompatibility(
                createManualCompatibilityDtoRequest);

        // Verify the results
        assertThat(result).isNotNull();
        assertThat(result.getAutomaticCompatibility()).isNotNull();
        assertThat(result.getAutomaticCompatibility().getId()).isEqualTo(1L);
        assertThat(result.getAutomaticCompatibility().getRuleId().getId()).isEqualTo(1L);
        assertThat(result.getAutomaticCompatibility().getComponent1Id().getId()).isEqualTo(1L); // Check id of the first component
        assertThat(result.getAutomaticCompatibility().getComponent2Id().getId()).isEqualTo(2L); // Check id of the second component
        assertThat(result.getAutomaticCompatibility().getConfigurationType()).isEqualTo("configurationType");

        // Verify method calls
        verify(mockComponentTypeRepository, times(1)).findById(1L);
        verify(mockComponentTypeRepository, times(1)).findById(2L);
        verify(mockSpecificationTypeList_ComponentTypeRepository, times(1)).findById(1L);
        verify(mockSpecificationTypeList_ComponentTypeRepository, times(1)).findById(2L);
        verify(mockRuleEntityRepository, times(1)).findBySpecificationToConsider1IdIdAndSpecificationToConsider2IdIdAndConfigurationType(
                1L, 2L, "configurationType");
        verify(mockRuleEntityRepository, times(1)).save(any(RuleEntity.class));
        verify(mockCompatibilityRepository, times(1)).save(any(CompatibilityEntity.class));
    }


    @Test
    void testAutomaticCompatibilityByCompatibilityId() {
        // Setup
        // Configure CompatibilityRepository.findById(...).
        final Optional<CompatibilityEntity> compatibilityEntity = Optional.of(CompatibilityEntity.builder()
                .id(0L)
                .component1Id(ComponentTypeEntity.builder()
                        .id(0L)
                        .componentTypeName("componentTypeName")
                        .componentTypeImageUrl("componentTypeImageUrl")
                        .category(CategoryEntity.builder()
                                .id(0L)
                                .categoryName("categoryName")
                                .build())
                        .configurationType("configurationType")
                        .specifications(List.of(SpecficationTypeList_ComponentTypeEntity.builder()
                                .id(0L)
                                .specificationType(SpecificationTypeEntity.builder()
                                        .id(0L)
                                        .specificationTypeName("specificationTypeName")
                                        .build())
                                .build()))
                        .build())
                .component2Id(ComponentTypeEntity.builder()
                        .id(0L)
                        .componentTypeName("componentTypeName")
                        .componentTypeImageUrl("componentTypeImageUrl")
                        .category(CategoryEntity.builder()
                                .id(0L)
                                .categoryName("categoryName")
                                .build())
                        .configurationType("configurationType")
                        .specifications(List.of(SpecficationTypeList_ComponentTypeEntity.builder()
                                .id(0L)
                                .specificationType(SpecificationTypeEntity.builder()
                                        .id(0L)
                                        .specificationTypeName("specificationTypeName")
                                        .build())
                                .build()))
                        .build())
                .ruleId(RuleEntity.builder()
                        .id(0L)
                        .specificationToConsider1Id(SpecficationTypeList_ComponentTypeEntity.builder()
                                .id(0L)
                                .specificationType(SpecificationTypeEntity.builder()
                                        .id(0L)
                                        .specificationTypeName("specificationTypeName")
                                        .build())
                                .build())
                        .specificationToConsider2Id(SpecficationTypeList_ComponentTypeEntity.builder()
                                .id(0L)
                                .specificationType(SpecificationTypeEntity.builder()
                                        .id(0L)
                                        .specificationTypeName("specificationTypeName")
                                        .build())
                                .build())
                        .valueOfFirstSpecification("valueForTheFirstSpecification")
                        .valueOfSecondSpecification("valueOfSecondSpecification")
                        .configurationType("configurationType")
                        .build())
                .configurationType("configurationType")
                .build());
        when(mockCompatibilityRepository.findById(0L)).thenReturn(compatibilityEntity);

        // Run the test
        final GetAutomaticCompatibilityByIdResponse result = compatibilityManagerImplUnderTest.automaticCompatibilityByCompatibilityId(
                0L);

        // Verify the results
    }

    @Test
    void testAutomaticCompatibilityByCompatibilityId_CompatibilityRepositoryReturnsAbsent() {
        // Setup
        when(mockCompatibilityRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
                () -> compatibilityManagerImplUnderTest.automaticCompatibilityByCompatibilityId(0L))
                .isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testAllCompatibilitiesForComponentTypeByComponentTypeId() {
        // Setup
        // Configure CompatibilityRepository.findByComponent1Id_IdOrComponent2Id_Id(...).
        final List<CompatibilityEntity> compatibilityEntities = List.of(CompatibilityEntity.builder()
                .id(0L)
                .component1Id(ComponentTypeEntity.builder()
                        .id(0L)
                        .componentTypeName("componentTypeName")
                        .componentTypeImageUrl("componentTypeImageUrl")
                        .category(CategoryEntity.builder()
                                .id(0L)
                                .categoryName("categoryName")
                                .build())
                        .configurationType("configurationType")
                        .specifications(List.of(SpecficationTypeList_ComponentTypeEntity.builder()
                                .id(0L)
                                .specificationType(SpecificationTypeEntity.builder()
                                        .id(0L)
                                        .specificationTypeName("specificationTypeName")
                                        .build())
                                .build()))
                        .build())
                .component2Id(ComponentTypeEntity.builder()
                        .id(0L)
                        .componentTypeName("componentTypeName")
                        .componentTypeImageUrl("componentTypeImageUrl")
                        .category(CategoryEntity.builder()
                                .id(0L)
                                .categoryName("categoryName")
                                .build())
                        .configurationType("configurationType")
                        .specifications(List.of(SpecficationTypeList_ComponentTypeEntity.builder()
                                .id(0L)
                                .specificationType(SpecificationTypeEntity.builder()
                                        .id(0L)
                                        .specificationTypeName("specificationTypeName")
                                        .build())
                                .build()))
                        .build())
                .ruleId(RuleEntity.builder()
                        .id(0L)
                        .specificationToConsider1Id(SpecficationTypeList_ComponentTypeEntity.builder()
                                .id(0L)
                                .specificationType(SpecificationTypeEntity.builder()
                                        .id(0L)
                                        .specificationTypeName("specificationTypeName")
                                        .build())
                                .build())
                        .specificationToConsider2Id(SpecficationTypeList_ComponentTypeEntity.builder()
                                .id(0L)
                                .specificationType(SpecificationTypeEntity.builder()
                                        .id(0L)
                                        .specificationTypeName("specificationTypeName")
                                        .build())
                                .build())
                        .valueOfFirstSpecification("valueForTheFirstSpecification")
                        .valueOfSecondSpecification("valueOfSecondSpecification")
                        .configurationType("configurationType")
                        .build())
                .configurationType("configurationType")
                .build());
        when(mockCompatibilityRepository.findByComponent1Id_IdOrComponent2Id_Id(0L, 0L))
                .thenReturn(compatibilityEntities);

        // Run the test
        final List<GetAutomaticCompatibilityByIdResponse> result = compatibilityManagerImplUnderTest.allCompatibilitiesForComponentTypeByComponentTypeId(
                0L);

        // Verify the results
    }

    @Test
    void testAllCompatibilitiesForComponentTypeByComponentTypeId_CompatibilityRepositoryReturnsNoItems() {
        // Setup
        when(mockCompatibilityRepository.findByComponent1Id_IdOrComponent2Id_Id(0L, 0L))
                .thenReturn(Collections.emptyList());

        // Run the test
        final List<GetAutomaticCompatibilityByIdResponse> result = compatibilityManagerImplUnderTest.allCompatibilitiesForComponentTypeByComponentTypeId(
                0L);

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGetRulesByCategoryAndConfigurationType() {
        // Setup
        final List<RuleResponse> expectedResult = List.of(RuleResponse.builder().build());

        // Configure CompatibilityRepository.findRulesByConfigurationType(...).
        final Page<RuleResponse> ruleResponses = new PageImpl<>(List.of(RuleResponse.builder().build()));
        when(mockCompatibilityRepository.findRulesByConfigurationType(eq("compatibilityType"),
                any(Pageable.class))).thenReturn(ruleResponses);

        // Run the test
        final List<RuleResponse> result = compatibilityManagerImplUnderTest.getRulesByCategoryAndConfigurationType(
                "compatibilityType", 1, 10);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetRulesByCategoryAndConfigurationType_CompatibilityRepositoryReturnsNoItems() {
        // Setup
        when(mockCompatibilityRepository.findRulesByConfigurationType(eq("compatibilityType"),
                any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final List<RuleResponse> result = compatibilityManagerImplUnderTest.getRulesByCategoryAndConfigurationType(
                "compatibilityType", 1, 10);

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGetRuleById() {
        // Setup
        final RuleResponse expectedResult = RuleResponse.builder().build();

        // Configure CompatibilityRepository.findRuleById(...).
        final Optional<RuleResponse> ruleResponse = Optional.of(RuleResponse.builder().build());
        when(mockCompatibilityRepository.findRuleById(0L)).thenReturn(ruleResponse);

        // Run the test
        final RuleResponse result = compatibilityManagerImplUnderTest.getRuleById(0L);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetRuleById_CompatibilityRepositoryReturnsAbsent() {
        // Setup
        when(mockCompatibilityRepository.findRuleById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> compatibilityManagerImplUnderTest.getRuleById(0L)).isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testDeleteRuleById() {
        // Setup
        // Configure CompatibilityRepository.findByRuleId(...).
        final Optional<CompatibilityEntity> compatibilityEntity = Optional.of(CompatibilityEntity.builder()
                .id(0L)
                .component1Id(ComponentTypeEntity.builder()
                        .id(0L)
                        .componentTypeName("componentTypeName")
                        .componentTypeImageUrl("componentTypeImageUrl")
                        .category(CategoryEntity.builder()
                                .id(0L)
                                .categoryName("categoryName")
                                .build())
                        .configurationType("configurationType")
                        .specifications(List.of(SpecficationTypeList_ComponentTypeEntity.builder()
                                .id(0L)
                                .specificationType(SpecificationTypeEntity.builder()
                                        .id(0L)
                                        .specificationTypeName("specificationTypeName")
                                        .build())
                                .build()))
                        .build())
                .component2Id(ComponentTypeEntity.builder()
                        .id(0L)
                        .componentTypeName("componentTypeName")
                        .componentTypeImageUrl("componentTypeImageUrl")
                        .category(CategoryEntity.builder()
                                .id(0L)
                                .categoryName("categoryName")
                                .build())
                        .configurationType("configurationType")
                        .specifications(List.of(SpecficationTypeList_ComponentTypeEntity.builder()
                                .id(0L)
                                .specificationType(SpecificationTypeEntity.builder()
                                        .id(0L)
                                        .specificationTypeName("specificationTypeName")
                                        .build())
                                .build()))
                        .build())
                .ruleId(RuleEntity.builder()
                        .id(0L)
                        .specificationToConsider1Id(SpecficationTypeList_ComponentTypeEntity.builder()
                                .id(0L)
                                .specificationType(SpecificationTypeEntity.builder()
                                        .id(0L)
                                        .specificationTypeName("specificationTypeName")
                                        .build())
                                .build())
                        .specificationToConsider2Id(SpecficationTypeList_ComponentTypeEntity.builder()
                                .id(0L)
                                .specificationType(SpecificationTypeEntity.builder()
                                        .id(0L)
                                        .specificationTypeName("specificationTypeName")
                                        .build())
                                .build())
                        .valueOfFirstSpecification("valueForTheFirstSpecification")
                        .valueOfSecondSpecification("valueOfSecondSpecification")
                        .configurationType("configurationType")
                        .build())
                .configurationType("configurationType")
                .build());
        when(mockCompatibilityRepository.findByRuleId(0L)).thenReturn(compatibilityEntity);

        // Run the test
        compatibilityManagerImplUnderTest.deleteRuleById(0L);

        // Verify the results
        verify(mockCompatibilityRepository).deleteById(0L);
        verify(mockRuleEntityRepository).deleteById(0L);
    }

    @Test
    void testDeleteRuleById_CompatibilityRepositoryFindByRuleIdReturnsAbsent() {
        // Setup
        when(mockCompatibilityRepository.findByRuleId(0L)).thenReturn(Optional.empty());

        // Run the test
        compatibilityManagerImplUnderTest.deleteRuleById(0L);

        // Verify the results
    }

    @Test
    void testUpdateRuleById() {
        // Setup
        final UpdateRuleRequest request = UpdateRuleRequest.builder()
                .ruleId(0L)
                .valueForTheFirstSpecification("valueForTheFirstSpecification")
                .valuesForTheSecondSpecification(List.of("value"))
                .build();
        final RuleResponse expectedResult = RuleResponse.builder().build();

        // Configure RuleEntityRepository.findById(...).
        final Optional<RuleEntity> ruleEntity = Optional.of(RuleEntity.builder()
                .id(0L)
                .specificationToConsider1Id(SpecficationTypeList_ComponentTypeEntity.builder()
                        .id(0L)
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(0L)
                                .specificationTypeName("specificationTypeName")
                                .build())
                        .build())
                .specificationToConsider2Id(SpecficationTypeList_ComponentTypeEntity.builder()
                        .id(0L)
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(0L)
                                .specificationTypeName("specificationTypeName")
                                .build())
                        .build())
                .valueOfFirstSpecification("valueForTheFirstSpecification")
                .valueOfSecondSpecification("valueOfSecondSpecification")
                .configurationType("configurationType")
                .build());
        when(mockRuleEntityRepository.findById(0L)).thenReturn(ruleEntity);

        // Configure CompatibilityRepository.findRuleById(...).
        final Optional<RuleResponse> ruleResponse = Optional.of(RuleResponse.builder().build());
        when(mockCompatibilityRepository.findRuleById(0L)).thenReturn(ruleResponse);

        // Run the test
        final RuleResponse result = compatibilityManagerImplUnderTest.updateRuleById(request);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockRuleEntityRepository).save(any(RuleEntity.class));
    }

    @Test
    void testUpdateRuleById_RuleEntityRepositoryFindByIdReturnsAbsent() {
        // Setup
        final UpdateRuleRequest request = UpdateRuleRequest.builder()
                .ruleId(0L)
                .valueForTheFirstSpecification("valueForTheFirstSpecification")
                .valuesForTheSecondSpecification(List.of("value"))
                .build();
        when(mockRuleEntityRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> compatibilityManagerImplUnderTest.updateRuleById(request))
                .isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testUpdateRuleById_CompatibilityRepositoryReturnsAbsent() {
        // Setup
        final UpdateRuleRequest request = UpdateRuleRequest.builder()
                .ruleId(0L)
                .valueForTheFirstSpecification("valueForTheFirstSpecification")
                .valuesForTheSecondSpecification(List.of("value"))
                .build();

        // Configure RuleEntityRepository.findById(...).
        final Optional<RuleEntity> ruleEntity = Optional.of(RuleEntity.builder()
                .id(0L)
                .specificationToConsider1Id(SpecficationTypeList_ComponentTypeEntity.builder()
                        .id(0L)
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(0L)
                                .specificationTypeName("specificationTypeName")
                                .build())
                        .build())
                .specificationToConsider2Id(SpecficationTypeList_ComponentTypeEntity.builder()
                        .id(0L)
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(0L)
                                .specificationTypeName("specificationTypeName")
                                .build())
                        .build())
                .valueOfFirstSpecification("valueForTheFirstSpecification")
                .valueOfSecondSpecification("valueOfSecondSpecification")
                .configurationType("configurationType")
                .build());
        when(mockRuleEntityRepository.findById(0L)).thenReturn(ruleEntity);

        when(mockCompatibilityRepository.findRuleById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> compatibilityManagerImplUnderTest.updateRuleById(request))
                .isInstanceOf(ObjectNotFound.class);
        verify(mockRuleEntityRepository).save(any(RuleEntity.class));
    }
}
