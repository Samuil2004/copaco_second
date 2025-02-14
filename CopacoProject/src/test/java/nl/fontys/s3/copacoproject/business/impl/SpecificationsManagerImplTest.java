package nl.fontys.s3.copacoproject.business.impl;

import nl.fontys.s3.copacoproject.business.exception.InvalidInputException;
import nl.fontys.s3.copacoproject.business.SpecificationIdsForComponentPurpose;
import nl.fontys.s3.copacoproject.persistence.ComponentSpecificationListRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.entity.SpecficationTypeList_ComponentTypeEntity;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeList_ComponentTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpecificationsManagerImplTest {

    @Mock
    private ComponentSpecificationListRepository mockComponentSpecificationListRepository;
    @Mock
    private ComponentTypeRepository mockComponentTypeRepository;
    @Mock
    private SpecificationTypeList_ComponentTypeRepository mockSpecificationTypeList_ComponentTypeRepository;
    @Mock
    private SpecificationIdsForComponentPurpose specificationIdsForComponentPurpose;

    private SpecificationsManagerImpl specificationsManagerImplUnderTest;

    @BeforeEach
    void setUp() {
        specificationsManagerImplUnderTest = new SpecificationsManagerImpl(mockComponentSpecificationListRepository,
                mockComponentTypeRepository, mockSpecificationTypeList_ComponentTypeRepository, specificationIdsForComponentPurpose);
    }

    @Test
    void testGetSpecificationValuesOfSpecificationTypeByComponentType() {
        // Setup
        when(mockComponentTypeRepository.existsById(0L)).thenReturn(true);

        // Configure SpecificationTypeList_ComponentTypeRepository.findByComponentTypeAndSpecificationType(...).
        final SpecficationTypeList_ComponentTypeEntity specficationTypeListComponentTypeEntity = SpecficationTypeList_ComponentTypeEntity.builder().build();
        when(mockSpecificationTypeList_ComponentTypeRepository.findByComponentTypeAndSpecificationType(0L,
                0L)).thenReturn(specficationTypeListComponentTypeEntity);

        when(mockSpecificationTypeList_ComponentTypeRepository.findSpecificationValuesBySpecificationTypeAndComponentType(
                eq(SpecficationTypeList_ComponentTypeEntity.builder().build()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of("value")));

        // Run the test
        final List<String> result = specificationsManagerImplUnderTest.getSpecificationValuesOfSpecificationTypeByComponentType(
                0L, 0L, 1, 10);

        // Verify the results
        assertThat(result).isEqualTo(List.of("value"));
    }

    @Test
    void testGetSpecificationValuesOfSpecificationTypeByComponentType_ComponentTypeRepositoryReturnsFalse() {
        // Setup
        when(mockComponentTypeRepository.existsById(0L)).thenReturn(false);

        // Run the test
        assertThatThrownBy(
                () -> specificationsManagerImplUnderTest.getSpecificationValuesOfSpecificationTypeByComponentType(0L,
                        0L, 0, 0)).isInstanceOf(InvalidInputException.class);
    }

    @Test
    void testGetSpecificationValuesOfSpecificationTypeByComponentType_SpecificationTypeList_ComponentTypeRepositoryFindByComponentTypeAndSpecificationTypeReturnsNull() {
        // Setup
        when(mockComponentTypeRepository.existsById(0L)).thenReturn(true);
        when(mockSpecificationTypeList_ComponentTypeRepository.findByComponentTypeAndSpecificationType(0L,
                0L)).thenReturn(null);

        // Run the test
        assertThatThrownBy(
                () -> specificationsManagerImplUnderTest.getSpecificationValuesOfSpecificationTypeByComponentType(0L,
                        0L, 0, 0)).isInstanceOf(InvalidInputException.class);
    }

    @Test
    void testGetSpecificationValuesOfSpecificationTypeByComponentType_SpecificationTypeList_ComponentTypeRepositoryFindSpecificationValuesBySpecificationTypeAndComponentTypeReturnsNoItems() {
        // Setup
        when(mockComponentTypeRepository.existsById(0L)).thenReturn(true);

        // Configure SpecificationTypeList_ComponentTypeRepository.findByComponentTypeAndSpecificationType(...).
        final SpecficationTypeList_ComponentTypeEntity specficationTypeListComponentTypeEntity = SpecficationTypeList_ComponentTypeEntity.builder().build();
        when(mockSpecificationTypeList_ComponentTypeRepository.findByComponentTypeAndSpecificationType(0L,
                0L)).thenReturn(specficationTypeListComponentTypeEntity);

        when(mockSpecificationTypeList_ComponentTypeRepository.findSpecificationValuesBySpecificationTypeAndComponentType(
                eq(SpecficationTypeList_ComponentTypeEntity.builder().build()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final List<String> result = specificationsManagerImplUnderTest.getSpecificationValuesOfSpecificationTypeByComponentType(
                0L, 0L, 1, 10);

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }
}
