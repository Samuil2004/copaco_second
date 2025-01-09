package nl.fontys.s3.copacoproject.business.impl;

import nl.fontys.s3.copacoproject.business.exception.InvalidInputException;
import nl.fontys.s3.copacoproject.business.exception.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.SpecificationIdsForComponentPurpose;
import nl.fontys.s3.copacoproject.business.dto.GetComponentResponse;
import nl.fontys.s3.copacoproject.business.dto.component.SimpleComponentResponse;
import nl.fontys.s3.copacoproject.domain.Category;
import nl.fontys.s3.copacoproject.domain.Component;
import nl.fontys.s3.copacoproject.domain.ComponentType;
import nl.fontys.s3.copacoproject.persistence.*;
import nl.fontys.s3.copacoproject.persistence.entity.*;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComponentManagerImplTest {

    @Mock
    private ComponentRepository mockComponentRepository;
    @Mock
    private ComponentSpecificationListRepository mockComponentSpecificationListRepository;
    @Mock
    private CategoryRepository mockCategoryRepository;
    @Mock
    private ComponentTypeRepository mockComponentTypeRepository;
    @Mock
    private SpecificationIdsForComponentPurpose specificationIdsForComponentPurpose;

    private ComponentManagerImpl componentManagerImplUnderTest;

    @BeforeEach
    void setUp() {
        componentManagerImplUnderTest = new ComponentManagerImpl(mockComponentRepository,
                mockComponentSpecificationListRepository, mockSpecificationTypeRepository, mockCategoryRepository,
                mockComponentTypeRepository,specificationIdsForComponentPurpose);
    }

    @Test
    void testGetAllComponents() {
        // Setup
        // Mock component entity
        ComponentEntity mockComponentEntity = ComponentEntity.builder()
                .componentId(0L)
                .componentName("componentName")
                .componentType(ComponentTypeEntity.builder()
                        .id(1L)
                        .componentTypeName("componentTypeName")
                        .category(CategoryEntity.builder()
                                .id(1L)
                                .categoryName("categoryName")
                                .build())
                        .build())
                .componentImageUrl("componentImageUrl")
                .componentPrice(0.0)
                .build();

        // Mock repository to return the component entity
        when(mockComponentRepository.findAll()).thenReturn(List.of(mockComponentEntity));

        // Use `any` matcher for `findByComponentId`
        when(mockComponentSpecificationListRepository.findByComponentId(any(ComponentEntity.class)))
                .thenReturn(Collections.emptyList());

        // Expected result
        List<GetComponentResponse> expectedResponse = List.of(
                GetComponentResponse.builder()
                        .componentId(0L)
                        .componentName("componentName")
                        .componentTypeId(1L)
                        .componentTypeName("componentTypeName")
                        .categoryName("categoryName")
                        .componentImageUrl("componentImageUrl")
                        .componentPrice(0.0)
                        .specifications(Collections.emptyMap())
                        .build()
        );

        // Run the test
        List<GetComponentResponse> result = componentManagerImplUnderTest.getAllComponents();

        // Verify the results
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    @Test
    void testGetAllComponents_ComponentRepositoryReturnsNoItems() {
        // Setup
        when(mockComponentRepository.findAll()).thenReturn(Collections.emptyList());

        // Run the test
        final List<GetComponentResponse> result = componentManagerImplUnderTest.getAllComponents();

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGetAllComponents_ComponentSpecificationListRepositoryReturnsNoItems() {
        // Setup
        // Mock component entity
        ComponentEntity mockComponentEntity = ComponentEntity.builder()
                .componentId(0L)
                .componentName("componentName")
                .componentType(ComponentTypeEntity.builder()
                        .id(1L)
                        .componentTypeName("componentTypeName")
                        .category(CategoryEntity.builder()
                                .id(1L)
                                .categoryName("categoryName")
                                .build())
                        .build())
                .componentImageUrl("componentImageUrl")
                .componentPrice(0.0)
                .build();

        // Mock repository returning the component entity
        when(mockComponentRepository.findAll()).thenReturn(List.of(mockComponentEntity));

        // Mock specification list repository to return no specifications
        when(mockComponentSpecificationListRepository.findByComponentId(mockComponentEntity))
                .thenReturn(Collections.emptyList());

        // Expected result
        List<GetComponentResponse> expectedResponse = List.of(
                GetComponentResponse.builder()
                        .componentId(0L)
                        .componentName("componentName")
                        .componentTypeId(1L)
                        .componentTypeName("componentTypeName")
                        .categoryName("categoryName")
                        .componentImageUrl("componentImageUrl")
                        .componentPrice(0.0)
                        .specifications(Collections.emptyMap())
                        .build()
        );

        // Run the test
        List<GetComponentResponse> result = componentManagerImplUnderTest.getAllComponents();

        // Verify the results
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResponse);
    }


    @Test
    void testGetAllComponentsByCategory() {
        // Setup
        final long categoryId = 0L;

        // Mock the category existence
        when(mockCategoryRepository.existsById(categoryId)).thenReturn(true);

        // Mock category entity
        CategoryEntity mockCategoryEntity = CategoryEntity.builder()
                .id(categoryId)
                .categoryName("categoryName")
                .build();

        // Mock component type entity
        ComponentTypeEntity mockComponentTypeEntity = ComponentTypeEntity.builder()
                .id(1L)
                .componentTypeName("componentTypeName")
                .category(mockCategoryEntity)
                .build();

        // Mock component entity
        ComponentEntity mockComponentEntity = ComponentEntity.builder()
                .componentId(1L)
                .componentName("TestComponent")
                .componentType(mockComponentTypeEntity)
                .componentImageUrl("testUrl")
                .componentPrice(150.0)
                .build();

        // Mock repository returning the filtered components by category
        when(mockComponentRepository.findComponentEntitiesByCategory(categoryId))
                .thenReturn(List.of(mockComponentEntity));

        // Mock specification list repository to return no specifications
        when(mockComponentSpecificationListRepository.findByComponentId(any(ComponentEntity.class)))
                .thenReturn(Collections.emptyList());

        // Expected result
        List<Component> expectedComponents = List.of(
                Component.builder()
                        .componentId(1L)
                        .componentName("TestComponent")
                        .componentType(ComponentType.builder()
                                .componentTypeId(1L)
                                .componentTypeName("componentTypeName")
                                .category(Category.builder()
                                        .categoryId(categoryId)
                                        .categoryName("categoryName")
                                        .build())
                                .configurationTypes(Collections.emptyList())
                                .specificationTypeList(Collections.emptyList())
                                .build())
                        .componentImageUrl("testUrl")
                        .componentPrice(150.0)
                        .specifications(Collections.emptyMap())
                        .build()
        );

        // Run the test
        List<Component> result = componentManagerImplUnderTest.getAllComponentsByCategory(categoryId);

        // Verify the results
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedComponents);
    }


    @Test
    void testGetAllComponentsByCategory_CategoryRepositoryReturnsFalse() {
        // Setup
        when(mockCategoryRepository.existsById(0L)).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> componentManagerImplUnderTest.getAllComponentsByCategory(0L))
                .isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testGetAllComponentsByCategory_ComponentRepositoryReturnsNoItems() {
        // Setup
        when(mockCategoryRepository.existsById(0L)).thenReturn(true);
        when(mockComponentRepository.findComponentEntitiesByCategory(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final List<Component> result = componentManagerImplUnderTest.getAllComponentsByCategory(0L);

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGetAllComponentsByCategory_ComponentSpecificationListRepositoryReturnsNoItems() {
        // Setup
        final long categoryId = 0L;

        when(mockCategoryRepository.existsById(categoryId)).thenReturn(true);

        // Create a component entity to be returned by the component repository
        ComponentTypeEntity componentTypeEntity = ComponentTypeEntity.builder()
                .id(0L)
                .componentTypeName("componentTypeName")
                .category(CategoryEntity.builder()
                        .id(categoryId)
                        .categoryName("categoryName")
                        .build())
                .build();

        ComponentEntity componentEntity = ComponentEntity.builder()
                .componentId(0L)
                .componentName("componentName")
                .componentType(componentTypeEntity)
                .componentImageUrl("componentImageUrl")
                .componentPrice(0.0)
                .build();

        when(mockComponentRepository.findComponentEntitiesByCategory(categoryId))
                .thenReturn(List.of(componentEntity));

        // Stub the component specification list repository to return an empty list
        when(mockComponentSpecificationListRepository.findByComponentId(any(ComponentEntity.class)))
                .thenReturn(Collections.emptyList());

        // Expected result
        List<Component> expectedComponents = List.of(Component.builder()
                .componentId(0L)
                .componentName("componentName")
                .componentType(ComponentType.builder()
                        .componentTypeId(0L)
                        .componentTypeName("componentTypeName")
                        .category(Category.builder()
                                .categoryId(categoryId)
                                .categoryName("categoryName")
                                .build())
                        .configurationTypes(Collections.emptyList()) // Empty list
                        .specificationTypeList(Collections.emptyList()) // Empty list
                        .build())
                .componentImageUrl("componentImageUrl")
                .componentPrice(0.0)
                .specifications(Collections.emptyMap()) // Empty specifications
                .build());

        // Run the test
        List<Component> result = componentManagerImplUnderTest.getAllComponentsByCategory(categoryId);

        // Verify the results
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedComponents);
    }

    @Test
    void testGetAllComponentFromComponentType() {
        // Setup
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .id(0L)
                .categoryName("categoryName")
                .build();

        ComponentTypeEntity componentTypeEntity = ComponentTypeEntity.builder()
                .id(0L)
                .componentTypeName("componentTypeName")
                .category(categoryEntity)
                .build();

        ComponentEntity componentEntity = ComponentEntity.builder()
                .componentId(0L)
                .componentName("componentName")
                .componentType(componentTypeEntity)
                .componentImageUrl("componentImageUrl")
                .componentPrice(0.0)
                .build();

        // Mock the component type repository to return the component type entity
        when(mockComponentTypeRepository.findById(0L)).thenReturn(Optional.of(componentTypeEntity));

        // Mock the component repository to return the list of components
        when(mockComponentRepository.findByComponentType_Id(0L)).thenReturn(List.of(componentEntity));

        // Mock the component specification list repository to return an empty list of specifications
        when(mockComponentSpecificationListRepository.findByComponentId(componentEntity))
                .thenReturn(Collections.emptyList());

        // Expected result: Component is returned even if specifications list is empty
        List<Component> expectedComponents = List.of(Component.builder()
                .componentId(0L)
                .componentName("componentName")
                .componentType(ComponentType.builder()
                        .componentTypeId(0L)
                        .componentTypeName("componentTypeName")
                        .category(Category.builder()
                                .categoryId(0L)
                                .categoryName("categoryName")
                                .build())
                        .configurationTypes(Collections.emptyList()) // Match empty list
                        .specificationTypeList(Collections.emptyList()) // Match empty list
                        .build())
                .componentImageUrl("componentImageUrl")
                .componentPrice(0.0)
                .specifications(Collections.emptyMap()) // Empty specifications
                .build());

        // Run the test
        List<Component> result = componentManagerImplUnderTest.getAllComponentFromComponentType(0L);

        // Verify the results
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedComponents);
    }


    @Test
    void testGetAllComponentFromComponentType_ComponentSpecificationListRepositoryReturnsNoItems() {
        // Setup
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .id(0L)
                .categoryName("categoryName")
                .build();

        ComponentTypeEntity componentTypeEntity = ComponentTypeEntity.builder()
                .id(0L)
                .componentTypeName("componentTypeName")
                .category(categoryEntity)
                .build();

        ComponentEntity componentEntity = ComponentEntity.builder()
                .componentId(0L)
                .componentName("componentName")
                .componentType(componentTypeEntity)
                .build();

        when(mockComponentTypeRepository.findById(0L)).thenReturn(Optional.of(componentTypeEntity));
        when(mockComponentRepository.findByComponentType_Id(0L)).thenReturn(List.of(componentEntity));
        when(mockComponentSpecificationListRepository.findByComponentId(any(ComponentEntity.class)))
                .thenReturn(Collections.emptyList());

        // Run the test
        List<Component> result = componentManagerImplUnderTest.getAllComponentFromComponentType(0L);

        // Verify the results
        assertThat(result).hasSize(1); // Expecting one component with empty specifications
        assertThat(result.get(0).getSpecifications()).isEmpty(); // Verify specifications are empty
    }



    @Test
    void testGetComponentsByComponentTypeAndConfigurationType() {
        // Setup
        when(mockComponentTypeRepository.existsById(0L)).thenReturn(true);
        List<SimpleComponentResponse> expectedResult = List.of(SimpleComponentResponse.builder()
                .componentId(0L)
                .componentName("componentName")
                .componentImageUrl("componentImageUrl")
                .componentPrice(0.0)
                .build());

        // Configure ComponentRepository.findComponentEntityByComponentTypeAndConfigurationType(...).
        final Page<ComponentEntity> componentEntities = new PageImpl<>(List.of(ComponentEntity.builder()
                .componentId(0L)
                .componentName("componentName")
                .componentType(ComponentTypeEntity.builder()
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
                        .build())
                .componentImageUrl("componentImageUrl")
                .componentPrice(0.0)
                .build()));
        when(mockComponentRepository.findComponentEntityByComponentTypeAndConfigurationType(eq(0L),
                eq("configurationType"),List.of(947L, 954L, 1070L,1792L), any(Pageable.class))).thenReturn(componentEntities);

        // Run the test
        final List<SimpleComponentResponse> result = componentManagerImplUnderTest.getComponentsByComponentTypeAndConfigurationType(
                0L, "configurationType", 1);

        // Verify the results
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void testGetComponentsByComponentTypeAndConfigurationType_ComponentTypeRepositoryReturnsFalse() {
        // Setup
        when(mockComponentTypeRepository.existsById(0L)).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> componentManagerImplUnderTest.getComponentsByComponentTypeAndConfigurationType(0L,
                "configurationType", 0)).isInstanceOf(InvalidInputException.class);
    }

    @Test
    void testGetComponentsByComponentTypeAndConfigurationType_ComponentRepositoryReturnsNoItems() {
        // Setup
        when(mockComponentTypeRepository.existsById(0L)).thenReturn(true);
        when(mockComponentRepository.findComponentEntityByComponentTypeAndConfigurationType(eq(0L),
                eq("configurationType"),List.of(947L, 954L, 1070L,1792L), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final List<SimpleComponentResponse> result = componentManagerImplUnderTest.getComponentsByComponentTypeAndConfigurationType(
                0L, "configurationType", 1);

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGetComponentCountByComponentTypeAndConfigurationType() {
        // Setup
        when(mockComponentTypeRepository.existsById(0L)).thenReturn(true);
        when(mockComponentRepository.countByComponentTypeAndConfigurationType(0L, "configurationTypeId",List.of(947L, 954L, 1070L,1792L))).thenReturn(0);

        // Run the test
        final Integer result = componentManagerImplUnderTest.getComponentCountByComponentTypeAndConfigurationType(0L,
                "configurationTypeId");

        // Verify the results
        assertThat(result).isZero();
    }

    @Test
    void testGetComponentCountByComponentTypeAndConfigurationType_ComponentTypeRepositoryReturnsFalse() {
        // Setup
        when(mockComponentTypeRepository.existsById(0L)).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> componentManagerImplUnderTest.getComponentCountByComponentTypeAndConfigurationType(0L,
                "configurationTypeId")).isInstanceOf(InvalidInputException.class);
    }
}
