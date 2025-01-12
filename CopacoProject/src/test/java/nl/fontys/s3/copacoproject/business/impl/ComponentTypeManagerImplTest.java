package nl.fontys.s3.copacoproject.business.impl;

import nl.fontys.s3.copacoproject.business.exception.InvalidInputException;
import nl.fontys.s3.copacoproject.business.exception.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.dto.component_type_dto.ComponentTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.component_type_dto.GetAllComponentTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.component_type_dto.GetDistCompTypesByTyOfConfRequest;
import nl.fontys.s3.copacoproject.business.SpecificationIdsForComponentPurpose;
import nl.fontys.s3.copacoproject.domain.ComponentType;
import nl.fontys.s3.copacoproject.persistence.CategoryRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.TemplateRepository;
import nl.fontys.s3.copacoproject.persistence.entity.CategoryEntity;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeEntity;
import nl.fontys.s3.copacoproject.persistence.entity.SpecficationTypeList_ComponentTypeEntity;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComponentTypeManagerImplTest {

    @Mock
    private ComponentTypeRepository mockComponentTypeRepository;
    @Mock
    private CategoryRepository mockCategoryRepository;
    @Mock
    private TemplateRepository mockTemplateRepository;
    @Mock
    private SpecificationIdsForComponentPurpose specificationIdsForComponentPurpose;


    private ComponentTypeManagerImpl componentTypeManagerImplUnderTest;

    @BeforeEach
    void setUp(){
        componentTypeManagerImplUnderTest = new ComponentTypeManagerImpl(mockComponentTypeRepository,
                mockCategoryRepository, mockTemplateRepository,specificationIdsForComponentPurpose);
    }

    @Test
    void testGetAllComponentTypes() {
        // Setup
        // Configure ComponentTypeRepository.findAll(...).
        final List<ComponentTypeEntity> componentTypeEntities = List.of(ComponentTypeEntity.builder()
                .id(0L)
                .componentTypeName("componentTypeName")
                .componentTypeImageUrl("componentTypeImageUrl")
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .configurationType("configurationType")
                .specifications(List.of(SpecficationTypeList_ComponentTypeEntity.builder()
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(0L)
                                .specificationTypeName("specificationTypeName")
                                .build())
                        .build()))
                .build());
        when(mockComponentTypeRepository.findAll()).thenReturn(componentTypeEntities);

        // Run the test
        final GetAllComponentTypeResponse result = componentTypeManagerImplUnderTest.getAllComponentTypes();

        // Verify the results
        assertThat(result).isNotNull();
    }

    @Test
    void testGetAllComponentTypes_ComponentTypeRepositoryReturnsNoItems() {
        // Setup
        when(mockComponentTypeRepository.findAll()).thenReturn(Collections.emptyList());

        // Run the test
        final GetAllComponentTypeResponse result = componentTypeManagerImplUnderTest.getAllComponentTypes();

        // Verify the results
        assertThat(result)
                .isNotNull()
                .isInstanceOf(GetAllComponentTypeResponse.class);
    }

    @Test
    void testGetComponentTypeById() {
        // Setup
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
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(0L)
                                .specificationTypeName("specificationTypeName")
                                .build())
                        .build()))
                .build());
        when(mockComponentTypeRepository.findById(0L)).thenReturn(componentTypeEntity);

        // Run the test
        final ComponentType result = componentTypeManagerImplUnderTest.getComponentTypeById(0L);

        // Verify the results
        assertThat(result).isNotNull();
    }

    @Test
    void testGetComponentTypeById_ComponentTypeRepositoryReturnsAbsent() {
        // Setup
        when(mockComponentTypeRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> componentTypeManagerImplUnderTest.getComponentTypeById(0L))
                .isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testGetComponentTypesByCategory() {
        // Setup
        when(mockCategoryRepository.existsById(0L)).thenReturn(true);

        // Configure CategoryRepository.findById(...).
        final Optional<CategoryEntity> categoryEntity = Optional.of(CategoryEntity.builder()
                .id(0L)
                .categoryName("categoryName")
                .build());
        when(mockCategoryRepository.findById(0L)).thenReturn(categoryEntity);

        // Configure ComponentTypeRepository.findComponentTypeEntitiesByCategory(...).
        final List<ComponentTypeEntity> componentTypeEntities = List.of(ComponentTypeEntity.builder()
                .id(0L)
                .componentTypeName("componentTypeName")
                .componentTypeImageUrl("componentTypeImageUrl")
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .configurationType("configurationType")
                .specifications(List.of(SpecficationTypeList_ComponentTypeEntity.builder()
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(0L)
                                .specificationTypeName("specificationTypeName")
                                .build())
                        .build()))
                .build());
        when(mockComponentTypeRepository.findComponentTypeEntitiesByCategory(CategoryEntity.builder()
                .id(0L)
                .categoryName("categoryName")
                .build())).thenReturn(componentTypeEntities);

        // Run the test
        final List<ComponentType> result = componentTypeManagerImplUnderTest.getComponentTypesByCategory(0L);

        // Verify the results
        assertThat(result).isNotNull();
    }

    @Test
    void testGetComponentTypesByCategory_CategoryRepositoryExistsByIdReturnsFalse() {
        // Setup
        when(mockCategoryRepository.existsById(0L)).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> componentTypeManagerImplUnderTest.getComponentTypesByCategory(0L))
                .isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testGetComponentTypesByCategory_CategoryRepositoryFindByIdReturnsAbsent() {
        // Setup
        when(mockCategoryRepository.existsById(0L)).thenReturn(true);
        when(mockCategoryRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> componentTypeManagerImplUnderTest.getComponentTypesByCategory(0L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testGetComponentTypesByCategory_ComponentTypeRepositoryReturnsNoItems() {
        // Setup
        when(mockCategoryRepository.existsById(0L)).thenReturn(true);

        // Configure CategoryRepository.findById(...).
        final Optional<CategoryEntity> categoryEntity = Optional.of(CategoryEntity.builder()
                .id(0L)
                .categoryName("categoryName")
                .build());
        when(mockCategoryRepository.findById(0L)).thenReturn(categoryEntity);

        when(mockComponentTypeRepository.findComponentTypeEntitiesByCategory(CategoryEntity.builder()
                .id(0L)
                .categoryName("categoryName")
                .build())).thenReturn(Collections.emptyList());

        // Run the test
        final List<ComponentType> result = componentTypeManagerImplUnderTest.getComponentTypesByCategory(0L);

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testFindDistinctComponentTypesByTypeOfConfiguration() {
        // Setup
        final GetDistCompTypesByTyOfConfRequest request = GetDistCompTypesByTyOfConfRequest.builder()
                .typeOfConfiguration("typeOfConfiguration")
                .build();
        final List<ComponentTypeResponse> expectedResult = List.of(ComponentTypeResponse.builder()
                .id(0L)
                .componentTypeName("componentTypeName")
                .componentTypeImageUrl("componentTypeImageUrl")
                .categoryName("categoryName")
                .configurationType("configurationType")
                .build());

        // Configure ComponentTypeRepository.findDistinctComponentTypesByTypeOfConfiguration(...).
        final List<ComponentTypeEntity> componentTypeEntities = List.of(ComponentTypeEntity.builder()
                .id(0L)
                .componentTypeName("componentTypeName")
                .componentTypeImageUrl("componentTypeImageUrl")
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .configurationType("configurationType")
                .specifications(List.of(SpecficationTypeList_ComponentTypeEntity.builder()
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(0L)
                                .specificationTypeName("specificationTypeName")
                                .build())
                        .build()))
                .build());
        when(specificationIdsForComponentPurpose.getAllDistinctSpecificationIdsThatHoldConfigurationType()).thenReturn(List.of(947L, 954L, 1070L,1792L));
        when(mockComponentTypeRepository.findDistinctComponentTypesByTypeOfConfiguration(
                "typeOfConfiguration",List.of(947L, 954L, 1070L,1792L))).thenReturn(componentTypeEntities);

        // Run the test
        final List<ComponentTypeResponse> result = componentTypeManagerImplUnderTest.findDistinctComponentTypesByTypeOfConfiguration(
                request);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testFindDistinctComponentTypesByTypeOfConfiguration_ComponentTypeRepositoryReturnsNoItems() {
        // Setup
        final GetDistCompTypesByTyOfConfRequest request = GetDistCompTypesByTyOfConfRequest.builder()
                .typeOfConfiguration("typeOfConfiguration")
                .build();
        when(specificationIdsForComponentPurpose.getAllDistinctSpecificationIdsThatHoldConfigurationType()).thenReturn(List.of(947L, 954L, 1070L,1792L));
        when(mockComponentTypeRepository.findDistinctComponentTypesByTypeOfConfiguration(
                "typeOfConfiguration",List.of(947L, 954L, 1070L,1792L))).thenReturn(Collections.emptyList());

        // Run the test
        assertThatThrownBy(() -> componentTypeManagerImplUnderTest.findDistinctComponentTypesByTypeOfConfiguration(
                request)).isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testGetComponentTypesByTemplateId() {
        // Setup
        final List<ComponentTypeResponse> expectedResult = List.of(ComponentTypeResponse.builder()
                .id(0L)
                .componentTypeName("componentTypeName")
                .componentTypeImageUrl("componentTypeImageUrl")
                .categoryName("categoryName")
                .configurationType("configurationType")
                .build());
        when(mockTemplateRepository.existsById(0L)).thenReturn(true);

        // Configure ComponentTypeRepository.getComponentTypeEntitiesByTemplateId(...).
        final List<ComponentTypeEntity> componentTypeEntities = List.of(ComponentTypeEntity.builder()
                .id(0L)
                .componentTypeName("componentTypeName")
                .componentTypeImageUrl("componentTypeImageUrl")
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .configurationType("configurationType")
                .specifications(List.of(SpecficationTypeList_ComponentTypeEntity.builder()
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(0L)
                                .specificationTypeName("specificationTypeName")
                                .build())
                        .build()))
                .build());
        when(mockComponentTypeRepository.getComponentTypeEntitiesByTemplateId(0L)).thenReturn(componentTypeEntities);

        // Run the test
        final List<ComponentTypeResponse> result = componentTypeManagerImplUnderTest.getComponentTypesByTemplateId(0L);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetComponentTypesByTemplateId_TemplateRepositoryReturnsFalse() {
        // Setup
        when(mockTemplateRepository.existsById(0L)).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> componentTypeManagerImplUnderTest.getComponentTypesByTemplateId(0L))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void testGetComponentTypesByTemplateId_ComponentTypeRepositoryReturnsNoItems() {
        // Setup
        when(mockTemplateRepository.existsById(0L)).thenReturn(true);
        when(mockComponentTypeRepository.getComponentTypeEntitiesByTemplateId(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final List<ComponentTypeResponse> result = componentTypeManagerImplUnderTest.getComponentTypesByTemplateId(0L);

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }
}
