package nl.fontys.s3.copacoproject.business.impl;

import nl.fontys.s3.copacoproject.business.exception.InvalidInputException;
import nl.fontys.s3.copacoproject.business.dto.specification_type_dto.CreateSpecificationTypeRequest;
import nl.fontys.s3.copacoproject.business.dto.specification_type_dto.CreateSpecificationTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.specification_type_dto.GetAllSpecificationTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.specification_type_dto.GetSpecificationTypeByComponentTypeResponse;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.SpecificationTypeRepository;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpecificationTypeManagerImplTest {

    @Mock
    private SpecificationTypeRepository mockSpecificationTypeRepository;
    @Mock
    private ComponentRepository mockComponentRepository;
    @Mock
    private ComponentTypeRepository mockComponentTypeRepository;

    private SpecificationTypeManagerImpl specificationTypeManagerImplUnderTest;

    @BeforeEach
    void setUp() {
        specificationTypeManagerImplUnderTest = new SpecificationTypeManagerImpl(mockSpecificationTypeRepository,
                mockComponentRepository, mockComponentTypeRepository);
    }

    @Test
    void testGetAllSpecificationType() {
        // Setup
        // Configure SpecificationTypeRepository.findAll(...).
        final List<SpecificationTypeEntity> specificationTypeEntities = List.of(SpecificationTypeEntity.builder()
                .id(0L)
                .specificationTypeName("specificationTypeName")
                .build());
        when(mockSpecificationTypeRepository.findAll()).thenReturn(specificationTypeEntities);

        // Run the test
        final GetAllSpecificationTypeResponse result = specificationTypeManagerImplUnderTest.getAllSpecificationType();

        // Verify the results
        assertThat(result.getSpecificationTypes()).isNotNull();
        assertThat(result.getSpecificationTypes()).hasSize(1);
        assertThat(result.getSpecificationTypes().get(0).getSpecificationTypeName()).isEqualTo("specificationTypeName");
    }

    @Test
    void testGetAllSpecificationType_SpecificationTypeRepositoryReturnsNoItems() {
        // Setup
        when(mockSpecificationTypeRepository.findAll()).thenReturn(Collections.emptyList());

        // Run the test
        final GetAllSpecificationTypeResponse result = specificationTypeManagerImplUnderTest.getAllSpecificationType();

        // Verify the results
        assertThat(result.getSpecificationTypes()).isEmpty();
    }

    @Test
    void testCreateSpecificationType() {
        // Setup
        final CreateSpecificationTypeRequest request = CreateSpecificationTypeRequest.builder()
                .specificationTypeName("specificationTypeName")
                .build();

        // Configure SpecificationTypeRepository.save(...).
        final SpecificationTypeEntity specificationTypeEntity = SpecificationTypeEntity.builder()
                .id(0L)
                .specificationTypeName("specificationTypeName")
                .build();
        when(mockSpecificationTypeRepository.save(SpecificationTypeEntity.builder()
                .id(0L)
                .specificationTypeName("specificationTypeName")
                .build())).thenReturn(specificationTypeEntity);

        // Run the test
        final CreateSpecificationTypeResponse result = specificationTypeManagerImplUnderTest.createSpecificationType(
                request);

        // Verify the results
        assertThat(result).isNotNull();
        assertThat(result.getSpecificationTypeId()).isZero();
    }

    @Test
    void testCreateSpecificationType_InvalidSpecificationTypeName() {
        //Arrange
        final CreateSpecificationTypeRequest request = CreateSpecificationTypeRequest.builder()
                .specificationTypeName(null)
                .build();
        // Run the test
        assertThatThrownBy(() -> specificationTypeManagerImplUnderTest.createSpecificationType(request)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testGetSpecificationType() {
        // Setup
        final SpecificationType expectedResult = SpecificationType.builder()
                .specificationTypeId(0L)
                .specificationTypeName("specificationTypeName")
                .build();

        // Configure SpecificationTypeRepository.findById(...).
        final Optional<SpecificationTypeEntity> specificationTypeEntity = Optional.of(SpecificationTypeEntity.builder()
                .id(0L)
                .specificationTypeName("specificationTypeName")
                .build());
        when(mockSpecificationTypeRepository.findById(0L)).thenReturn(specificationTypeEntity);

        // Run the test
        final SpecificationType result = specificationTypeManagerImplUnderTest.getSpecificationType(0L);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetSpecificationType_SpecificationTypeRepositoryReturnsAbsent() {
        // Setup
        when(mockSpecificationTypeRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> specificationTypeManagerImplUnderTest.getSpecificationType(0L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testGetSpecificationTypesByComponentId() {
        // Setup
        final List<SpecificationType> expectedResult = List.of(SpecificationType.builder()
                .specificationTypeId(0L)
                .specificationTypeName("specificationTypeName")
                .build());
        when(mockComponentRepository.existsById(0L)).thenReturn(true);

        // Configure SpecificationTypeRepository.findSpecificationTypeEntitiesByComponentId(...).
        final List<SpecificationTypeEntity> specificationTypeEntities = List.of(SpecificationTypeEntity.builder()
                .id(0L)
                .specificationTypeName("specificationTypeName")
                .build());
        when(mockSpecificationTypeRepository.findSpecificationTypeEntitiesByComponentId(0L))
                .thenReturn(specificationTypeEntities);

        // Run the test
        final List<SpecificationType> result = specificationTypeManagerImplUnderTest.getSpecificationTypesByComponentId(
                0L);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetSpecificationTypesByComponentId_ComponentRepositoryReturnsFalse() {
        // Setup
        when(mockComponentRepository.existsById(0L)).thenReturn(false);

        // Run the test
        assertThatThrownBy(
                () -> specificationTypeManagerImplUnderTest.getSpecificationTypesByComponentId(0L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testGetSpecificationTypesByComponentId_SpecificationTypeRepositoryReturnsNoItems() {
        // Setup
        when(mockComponentRepository.existsById(0L)).thenReturn(true);
        when(mockSpecificationTypeRepository.findSpecificationTypeEntitiesByComponentId(0L))
                .thenReturn(Collections.emptyList());

        // Run the test
        final List<SpecificationType> result = specificationTypeManagerImplUnderTest.getSpecificationTypesByComponentId(
                0L);

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGetSpecificationTypesByComponentTypeId() {
        // Setup
        when(mockComponentTypeRepository.existsById(0L)).thenReturn(true);

        // Configure SpecificationTypeRepository.findSpecificationTypeEntitiesByComponentTypeId(...).
        final Page<SpecificationTypeEntity> specificationTypeEntities = new PageImpl<>(
                List.of(SpecificationTypeEntity.builder()
                        .id(0L)
                        .specificationTypeName("specificationTypeName")
                        .build()));
        when(mockSpecificationTypeRepository.findSpecificationTypeEntitiesByComponentTypeId(eq(0L),
                any(Pageable.class))).thenReturn(specificationTypeEntities);

        when(mockSpecificationTypeRepository.countSpecificationTypesByComponentTypeId(0L)).thenReturn(0);

        // Run the test
        final GetSpecificationTypeByComponentTypeResponse result = specificationTypeManagerImplUnderTest.getSpecificationTypesByComponentTypeId(
                0L, 1, 10);

        // Verify the results
        assertThat(result).isNotNull();
    }

    @Test
    void testGetSpecificationTypesByComponentTypeId_ComponentTypeRepositoryReturnsFalse() {
        // Setup
        when(mockComponentTypeRepository.existsById(0L)).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> specificationTypeManagerImplUnderTest.getSpecificationTypesByComponentTypeId(0L, 0,
                0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testGetSpecificationTypesByComponentTypeId_TooManyItemsRequested() {
        // Setup
        when(mockComponentTypeRepository.existsById(0L)).thenReturn(true);

        // Run the test
        assertThatThrownBy(() -> specificationTypeManagerImplUnderTest.getSpecificationTypesByComponentTypeId(0L, 1,
                30)).isInstanceOf(InvalidInputException.class);
    }

    @Test
    void testGetSpecificationTypesByComponentTypeId_SpecificationTypeRepositoryFindSpecificationTypeEntitiesByComponentTypeIdReturnsNoItems() {
        // Setup
        when(mockComponentTypeRepository.existsById(0L)).thenReturn(true);
        when(mockSpecificationTypeRepository.findSpecificationTypeEntitiesByComponentTypeId(eq(0L),
                any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(mockSpecificationTypeRepository.countSpecificationTypesByComponentTypeId(0L)).thenReturn(0);

        // Run the test
        final GetSpecificationTypeByComponentTypeResponse result = specificationTypeManagerImplUnderTest.getSpecificationTypesByComponentTypeId(
                0L, 1, 10);

        // Verify the results
        assertThat(result).isNotNull();
    }

    @Test
    void testDeleteSpecificationType() {
        // Setup
        // Run the test
        specificationTypeManagerImplUnderTest.deleteSpecificationType(0L);

        // Verify the results
        verify(mockSpecificationTypeRepository).deleteById(0L);
    }
}
