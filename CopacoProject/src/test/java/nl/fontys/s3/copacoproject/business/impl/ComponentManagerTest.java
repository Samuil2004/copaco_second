//package nl.fontys.s3.copacoproject.business.impl;
//
//import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
//import nl.fontys.s3.copacoproject.business.dto.GetComponentResponse;
//import nl.fontys.s3.copacoproject.domain.Component;
//import nl.fontys.s3.copacoproject.persistence.*;
//import nl.fontys.s3.copacoproject.persistence.entity.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class ComponentManagerImplTest {
//
//    @Mock
//    private ComponentRepository componentRepository;
//
//    @Mock
//    private ComponentSpecificationListRepository componentSpecificationListRepository;
//
//    @Mock
//    private SpecificationTypeRepository specificationTypeRepository;
//
//    @Mock
//    private CategoryRepository categoryRepository;
//
//    @Mock
//    private ComponentTypeRepository componentTypeRepository;
//
//    @InjectMocks
//    private ComponentManagerImpl componentManager;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void getAllComponents_ShouldReturnAllComponents() {
//        // Arrange
//        ComponentEntity componentEntity = ComponentEntity.builder()
//                .componentId(1L)
//                .componentName("Component1")
//                .componentType(ComponentTypeEntity.builder()
//                        .id(1L)
//                        .componentTypeName("Type1")
//                        .category(CategoryEntity.builder()
//                                .categoryName("Category1")
//                                .build())
//                        .build())
//                .componentImageUrl("url1")
//                .brand(BrandEntity.builder().name("Brand1").build())
//                .componentPrice(100.0)
//                .build();
//
//        Component_SpecificationList specificationList = Component_SpecificationList.builder()
//                .componentId(componentEntity)
//                .specificationType(SpecificationTypeEntity.builder()
//                        .id(1L)
//                        .specificationTypeName("Spec1")
//                        .build())
//                .value("Value1")
//                .build();
//
//        when(componentRepository.findAll()).thenReturn(List.of(componentEntity));
//        when(componentSpecificationListRepository.findByComponentId(componentEntity)).thenReturn(List.of(specificationList));
//
//        // Act
//        List<GetComponentResponse> responses = componentManager.getAllComponents();
//
//        // Assert
//        assertNotNull(responses);
//        assertEquals(1, responses.size());
//        assertEquals("Component1", responses.get(0).getComponentName());
//        verify(componentRepository, times(1)).findAll();
//        verify(componentSpecificationListRepository, times(1)).findByComponentId(componentEntity);
//    }
//
//    @Test
//    void getAllComponentsByCategory_ShouldReturnComponents_WhenCategoryExists() {
//        // Arrange
//        long categoryId = 1L;
//
//        // Properly initialize CategoryEntity
//        CategoryEntity categoryEntity = CategoryEntity.builder()
//                .id(categoryId)
//                .categoryName("Category1")
//                .build();
//
//        // Properly initialize BrandEntity
//        BrandEntity brandEntity = BrandEntity.builder()
//                .id(1L)
//                .name("Brand1")
//                .build();
//
//        // Properly initialize ComponentTypeEntity
//        ComponentTypeEntity componentTypeEntity = ComponentTypeEntity.builder()
//                .id(1L)
//                .componentTypeName("Type1")
//                .category(categoryEntity)
//                .build();
//
//        // Properly initialize ComponentEntity with BrandEntity and ComponentTypeEntity
//        ComponentEntity componentEntity = ComponentEntity.builder()
//                .componentId(1L)
//                .componentName("Component1")
//                .componentType(componentTypeEntity)
//                .brand(brandEntity)
//                .componentImageUrl("url1")
//                .componentPrice(100.0)
//                .build();
//
//        when(categoryRepository.existsById(categoryId)).thenReturn(true);
//        when(componentRepository.findComponentEntitiesByCategory(categoryId)).thenReturn(List.of(componentEntity));
//        when(componentSpecificationListRepository.findByComponentId(componentEntity)).thenReturn(Collections.emptyList());
//
//        // Act
//        List<Component> components = componentManager.getAllComponentsByCategory(categoryId);
//
//        // Assert
//        assertNotNull(components);
//        assertEquals(1, components.size());
//        assertEquals("Component1", components.get(0).getComponentName());
//        verify(categoryRepository, times(1)).existsById(categoryId);
//        verify(componentRepository, times(1)).findComponentEntitiesByCategory(categoryId);
//        verify(componentSpecificationListRepository, times(1)).findByComponentId(componentEntity);
//    }
//
//
//
//    @Test
//    void getAllComponentsByCategory_ShouldThrowException_WhenCategoryDoesNotExist() {
//        // Arrange
//        long categoryId = 1L;
//        when(categoryRepository.existsById(categoryId)).thenReturn(false);
//
//        // Act & Assert
//        assertThrows(ObjectNotFound.class, () -> componentManager.getAllComponentsByCategory(categoryId));
//        verify(categoryRepository, times(1)).existsById(categoryId);
//    }
//
//    @Test
//    void getAllComponentFromComponentType_ShouldReturnComponents_WhenComponentTypeExists() {
//        // Arrange
//        long componentTypeId = 1L;
//        long categoryId = 2L;
//
//        // Create a fully initialized CategoryEntity
//        CategoryEntity categoryEntity = CategoryEntity.builder()
//                .id(categoryId)
//                .categoryName("Category1")
//                .build();
//
//        // Create a fully initialized ComponentTypeEntity with a CategoryEntity
//        ComponentTypeEntity componentTypeEntity = ComponentTypeEntity.builder()
//                .id(componentTypeId)
//                .componentTypeName("Type1")
//                .category(categoryEntity)
//                .build();
//
//        // Create a ComponentEntity with the ComponentTypeEntity
//        ComponentEntity componentEntity = ComponentEntity.builder()
//                .componentId(1L)
//                .componentName("Component1")
//                .componentType(componentTypeEntity)
//                .componentImageUrl("url1")
//                .brand(BrandEntity.builder().name("Brand1").build())
//                .componentPrice(100.0)
//                .build();
//
//        // Mock repository calls
//        when(componentTypeRepository.findById(componentTypeId)).thenReturn(Optional.of(componentTypeEntity));
//        when(componentRepository.findByComponentType_Id(componentTypeId)).thenReturn(List.of(componentEntity));
//        when(componentSpecificationListRepository.findByComponentId(componentEntity)).thenReturn(Collections.emptyList());
//
//        // Act
//        List<Component> components = componentManager.getAllComponentFromComponentType(componentTypeId);
//
//        // Assert
//        assertNotNull(components);
//        assertEquals(1, components.size());
//        assertEquals("Component1", components.get(0).getComponentName());
//        verify(componentTypeRepository, times(1)).findById(componentTypeId);
//        verify(componentRepository, times(1)).findByComponentType_Id(componentTypeId);
//        verify(componentSpecificationListRepository, times(1)).findByComponentId(componentEntity);
//    }
//
//
//    @Test
//    void getAllComponentFromComponentType_ShouldThrowException_WhenComponentTypeDoesNotExist() {
//        // Arrange
//        long componentTypeId = 1L;
//        when(componentTypeRepository.findById(componentTypeId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(ObjectNotFound.class, () -> componentManager.getAllComponentFromComponentType(componentTypeId));
//        verify(componentTypeRepository, times(1)).findById(componentTypeId);
//    }
//    @Test
//    void getAllComponents_ShouldReturnAllComponents_WithSpecifications() {
//        // Arrange
//        ComponentEntity componentEntity = ComponentEntity.builder()
//                .componentId(1L)
//                .componentName("Component1")
//                .componentType(ComponentTypeEntity.builder()
//                        .id(1L)
//                        .componentTypeName("Type1")
//                        .category(CategoryEntity.builder()
//                                .id(1L)
//                                .categoryName("Category1")
//                                .build())
//                        .build())
//                .componentImageUrl("url1")
//                .brand(BrandEntity.builder().id(1L).name("Brand1").build())
//                .componentPrice(100.0)
//                .build();
//
//        SpecificationTypeEntity specType1 = SpecificationTypeEntity.builder()
//                .id(1L)
//                .specificationTypeName("Spec1")
//                .build();
//
//        SpecificationTypeEntity specType2 = SpecificationTypeEntity.builder()
//                .id(2L)
//                .specificationTypeName("Spec2")
//                .build();
//
//        Component_SpecificationList specList1 = Component_SpecificationList.builder()
//                .componentId(componentEntity)
//                .specificationType(specType1)
//                .value("Value1")
//                .build();
//
//        Component_SpecificationList specList2 = Component_SpecificationList.builder()
//                .componentId(componentEntity)
//                .specificationType(specType1)
//                .value("Value2")
//                .build();
//
//        Component_SpecificationList specList3 = Component_SpecificationList.builder()
//                .componentId(componentEntity)
//                .specificationType(specType2)
//                .value("Value3")
//                .build();
//
//        when(componentRepository.findAll()).thenReturn(List.of(componentEntity));
//        when(componentSpecificationListRepository.findByComponentId(componentEntity))
//                .thenReturn(List.of(specList1, specList2, specList3));
//
//        // Act
//        List<GetComponentResponse> responses = componentManager.getAllComponents();
//
//        // Assert
//        assertNotNull(responses);
//        assertEquals(1, responses.size());
//        GetComponentResponse response = responses.get(0);
//        assertEquals("Component1", response.getComponentName());
//        assertEquals("Type1", response.getComponentTypeName());
//        assertEquals("Category1", response.getCategoryName());
//        assertEquals(2, response.getSpecifications().size());
//        assertTrue(response.getSpecifications().containsKey("Spec1"));
//        assertTrue(response.getSpecifications().containsKey("Spec2"));
//        assertEquals(List.of("Value1", "Value2"), response.getSpecifications().get("Spec1"));
//        assertEquals(List.of("Value3"), response.getSpecifications().get("Spec2"));
//
//        verify(componentRepository, times(1)).findAll();
//        verify(componentSpecificationListRepository, times(1)).findByComponentId(componentEntity);
//    }
//
//}
