package nl.fontys.s3.copacoproject.business.impl;

import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.CreateSpecificationTypeRequest;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.CreateSpecificationTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.GetAllSpecificationTypeResponse;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
import nl.fontys.s3.copacoproject.persistence.SpecificationTypeRepository;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpecificationTypeManagerImplTest {

    @Mock
    private SpecificationTypeRepository specificationTypeRepository;

    @Mock
    private ComponentRepository componentRepository;

    @InjectMocks
    private SpecificationTypeManagerImpl specificationTypeManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllSpecificationType_ShouldReturnAllSpecificationTypes() {
        // Arrange
        SpecificationTypeEntity entity1 = SpecificationTypeEntity.builder()
                .id(1L)
                .specificationTypeName("Type1")
                .build();
        SpecificationTypeEntity entity2 = SpecificationTypeEntity.builder()
                .id(2L)
                .specificationTypeName("Type2")
                .build();

        when(specificationTypeRepository.findAll()).thenReturn(List.of(entity1, entity2));

        // Act
        GetAllSpecificationTypeResponse response = specificationTypeManager.getAllSpecificationType();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getSpecificationTypes().size());
        assertEquals("Type1", response.getSpecificationTypes().get(0).getSpecificationTypeName());
        assertEquals("Type2", response.getSpecificationTypes().get(1).getSpecificationTypeName());
        verify(specificationTypeRepository, times(1)).findAll();
    }


    @Test
    void createSpecificationType_ShouldReturnResponse_WhenRequestIsValid() {
        // Arrange
        CreateSpecificationTypeRequest request = CreateSpecificationTypeRequest.builder()
                .specificationTypeName("Type1")
                .build();

        SpecificationTypeEntity entity = SpecificationTypeEntity.builder()
                .id(1L)
                .specificationTypeName("Type1")
                .build();

        when(specificationTypeRepository.save(any(SpecificationTypeEntity.class))).thenReturn(entity);

        // Act
        CreateSpecificationTypeResponse response = specificationTypeManager.createSpecificationType(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getSpecificationTypeId());
        verify(specificationTypeRepository, times(1)).save(any(SpecificationTypeEntity.class));
    }

    @Test
    void createSpecificationType_ShouldThrowException_WhenNameIsNull() {
        // Arrange
        CreateSpecificationTypeRequest request = CreateSpecificationTypeRequest.builder()
                .specificationTypeName(null)
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> specificationTypeManager.createSpecificationType(request));
        verify(specificationTypeRepository, never()).save(any(SpecificationTypeEntity.class));
    }

    @Test
    void getSpecificationType_ShouldReturnSpecificationType_WhenIdIsValid() {
        // Arrange
        long id = 1L;
        SpecificationTypeEntity entity = SpecificationTypeEntity.builder()
                .id(id)
                .specificationTypeName("Type1")
                .build();

        when(specificationTypeRepository.findById(id)).thenReturn(Optional.of(entity));

        // Act
        SpecificationType result = specificationTypeManager.getSpecificationType(id);

        // Assert
        assertNotNull(result);
        assertEquals("Type1", result.getSpecificationTypeName());
        verify(specificationTypeRepository, times(1)).findById(id);
    }

    @Test
    void getSpecificationType_ShouldThrowException_WhenIdIsInvalid() {
        // Arrange
        long id = 1L;
        when(specificationTypeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> specificationTypeManager.getSpecificationType(id));
        verify(specificationTypeRepository, times(1)).findById(id);
    }

    @Test
    void getSpecificationTypesByComponentId_ShouldReturnSpecificationTypes_WhenComponentIdIsValid() {
        // Arrange
        long componentId = 1L;
        SpecificationTypeEntity entity1 = SpecificationTypeEntity.builder()
                .id(1L)
                .specificationTypeName("Type1")
                .build();
        SpecificationTypeEntity entity2 = SpecificationTypeEntity.builder()
                .id(2L)
                .specificationTypeName("Type2")
                .build();

        when(componentRepository.existsById(componentId)).thenReturn(true);
        when(specificationTypeRepository.findSpecificationTypeEntitiesByComponentId(componentId)).thenReturn(List.of(entity1, entity2));

        // Act
        List<SpecificationType> result = specificationTypeManager.getSpecificationTypesByComponentId(componentId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Type1", result.get(0).getSpecificationTypeName());
        assertEquals("Type2", result.get(1).getSpecificationTypeName());
        verify(componentRepository, times(1)).existsById(componentId);
        verify(specificationTypeRepository, times(1)).findSpecificationTypeEntitiesByComponentId(componentId);
    }

    @Test
    void getSpecificationTypesByComponentId_ShouldThrowException_WhenComponentIdIsInvalid() {
        // Arrange
        long componentId = 1L;
        when(componentRepository.existsById(componentId)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> specificationTypeManager.getSpecificationTypesByComponentId(componentId));
        verify(componentRepository, times(1)).existsById(componentId);
        verify(specificationTypeRepository, never()).findSpecificationTypeEntitiesByComponentId(anyLong());
    }

    @Test
    void deleteSpecificationType_ShouldDeleteSuccessfully_WhenIdIsValid() {
        // Arrange
        long id = 1L;

        // Act
        specificationTypeManager.deleteSpecificationType(id);

        // Assert
        verify(specificationTypeRepository, times(1)).deleteById(id);
    }
}
