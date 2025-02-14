package nl.fontys.s3.copacoproject.business.impl;

import nl.fontys.s3.copacoproject.business.WebHookManager;
import nl.fontys.s3.copacoproject.business.converters.StatusConverter;
import nl.fontys.s3.copacoproject.business.exception.InvalidInputException;
import nl.fontys.s3.copacoproject.business.exception.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.dto.component.ComponentInConfigurationResponse;
import nl.fontys.s3.copacoproject.business.dto.component_type_dto.ComponentTypeInCustomResponse;
import nl.fontys.s3.copacoproject.business.dto.custom_product_dto.*;
import nl.fontys.s3.copacoproject.domain.enums.Status;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomProductManagerImplTest {

    @Mock
    private CustomProductRepository mockCustomProductRepository;
    @Mock
    private AssemblingRepository mockAssemblingRepository;
    @Mock
    private ComponentRepository mockComponentRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private TemplateRepository mockTemplateRepository;
    @Mock
    private StatusRepository mockStatusRepository;
    @Mock
    private ComponentSpecificationListRepository mockComponentSpecificationListRepository;
    @Mock
    private WebHookManager mockWebHookManager;

    private CustomProductManagerImpl customProductManagerImplUnderTest;

    @BeforeEach
    void setUp(){
        customProductManagerImplUnderTest = new CustomProductManagerImpl(mockCustomProductRepository,
                mockAssemblingRepository, mockComponentRepository, mockUserRepository, mockTemplateRepository,
                mockStatusRepository, mockComponentSpecificationListRepository, mockWebHookManager);
    }

    @Test
    void testCreateCustomProduct() {
        // Setup
        final CreateCustomProductRequest request = CreateCustomProductRequest.builder()
                .templateId(0L)
                .userId(1L) // Ensure this matches the argument in the actual implementation
                .componentsIncluded(List.of(ComponentInCustomProductInput.builder()
                        .componentId(0L)
                        .build()))
                .statusId(1)
                .build();

        final CreateCustomProductResponse expectedResult = CreateCustomProductResponse.builder()
                .createdProductId(0L)
                .build();

        when(mockUserRepository.existsById(1L)).thenReturn(true);
        when(mockStatusRepository.existsById(1)).thenReturn(true);

        // Ensure this matches the actual ID being used in the implementation
        when(mockUserRepository.findUserEntityById(1L)).thenReturn(UserEntity.builder()
                .id(1L)
                .build());

        // Configure TemplateRepository.findTemplateEntityById(...)
        final TemplateEntity templateEntity = TemplateEntity.builder()
                .id(0L)
                .active(true)
                .build();
        when(mockTemplateRepository.findTemplateEntityById(0L)).thenReturn(templateEntity);

        // Configure CustomProductRepository.save(...)
        final CustomProductEntity productEntity = CustomProductEntity.builder()
                .id(0L)
                .userId(UserEntity.builder()
                        .id(1L)
                        .build())
                .template(TemplateEntity.builder()
                        .id(0L)
                        .build())
                .status(StatusEntity.builder()
                        .id(1)
                        .name("DRAFT")
                        .build())
                .build();
        when(mockCustomProductRepository.save(any(CustomProductEntity.class))).thenReturn(productEntity);

        when(mockComponentRepository.existsById(0L)).thenReturn(true);

        // Configure ComponentRepository.findComponentEntityByComponentId(...)
        final ComponentEntity componentEntity = ComponentEntity.builder()
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
                .build();
        when(mockComponentRepository.findComponentEntityByComponentId(0L)).thenReturn(componentEntity);
        when(mockTemplateRepository.existsActiveTemplateEntityById(templateEntity.getId())).thenReturn(true);

        // Run the test
        final CreateCustomProductResponse result = customProductManagerImplUnderTest.createCustomProduct(request, 1L);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }


    @Test
    void testCreateCustomProduct_UserRepositoryExistsByIdReturnsFalse() {
        // Setup
        final CreateCustomProductRequest request = CreateCustomProductRequest.builder()
                .templateId(0L)
                .userId(0L)
                .componentsIncluded(List.of(ComponentInCustomProductInput.builder()
                        .componentId(0L)
                        .build()))
                .statusId(0)
                .build();
        when(mockUserRepository.existsById(0L)).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> customProductManagerImplUnderTest.createCustomProduct(request, 0L))
                .isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testCreateCustomProduct_TemplateRepositoryExistsByIdReturnsFalse() {
        // Setup
        final CreateCustomProductRequest request = CreateCustomProductRequest.builder()
                .templateId(0L)
                .userId(0L)
                .componentsIncluded(List.of(ComponentInCustomProductInput.builder()
                        .componentId(0L)
                        .build()))
                .statusId(0)
                .build();

        // Run the test
        assertThatThrownBy(() -> customProductManagerImplUnderTest.createCustomProduct(request, 0L))
                .isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testCreateCustomProduct_StatusRepositoryReturnsFalse() {
        // Setup
        final CreateCustomProductRequest request = CreateCustomProductRequest.builder()
                .templateId(0L)
                .userId(0L)
                .componentsIncluded(List.of(ComponentInCustomProductInput.builder()
                        .componentId(0L)
                        .build()))
                .statusId(0)
                .build();
        when(mockUserRepository.existsById(0L)).thenReturn(true);
        when(mockTemplateRepository.existsActiveTemplateEntityById(0L)).thenReturn(true);
        when(mockStatusRepository.existsById(0)).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> customProductManagerImplUnderTest.createCustomProduct(request, 0L))
                .isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testCreateCustomProduct_ComponentRepositoryExistsByIdReturnsFalse() {
        // Setup
        final CreateCustomProductRequest request = CreateCustomProductRequest.builder()
                .templateId(0L)
                .userId(0L)
                .componentsIncluded(List.of(ComponentInCustomProductInput.builder()
                        .componentId(0L)
                        .build()))
                .statusId(1)
                .build();

        when(mockUserRepository.existsById(0L)).thenReturn(true);

        // Run the test
        assertThatThrownBy(() -> customProductManagerImplUnderTest.createCustomProduct(request, 0L))
                .isInstanceOf(ObjectNotFound.class);
    }


    @Test
    void testGetCustomProductsOfUserByState() {
        // Setup
        final List<CustomProductResponse> expectedResult = List.of(CustomProductResponse.builder()
                .userId(0L)
                .customProductId(0L)
                .templateId(0L)
                .componentsIncluded(List.of(ComponentInConfigurationResponse.builder()
                        .componentId(0L)
                        .componentName("componentName")
                        .componentImageUrl("componentImageUrl")
                        .componentPrice(0.0)
                        .componentType(ComponentTypeInCustomResponse.builder()
                                .id(0L)
                                .name("componentTypeName")
                                .build())
                        .build()))
                .statusId(1L)
                .build());

        when(mockUserRepository.existsById(0L)).thenReturn(true);
        when(mockStatusRepository.existsById(1)).thenReturn(true);
        when(mockUserRepository.findUserEntityById(0L)).thenReturn(UserEntity.builder()
                .id(0L)
                .build());

        // Correctly stub the StatusEntity to match the arguments
        final StatusEntity statusEntity = StatusEntity.builder()
                .id(1)
                .name("DRAFT")
                .build();

        // Configure CustomProductRepository.findCustomProductEntitiesByStatusAndUserId(...)
        final Page<CustomProductEntity> customProductEntities = new PageImpl<>(List.of(CustomProductEntity.builder()
                .id(0L)
                .userId(UserEntity.builder()
                        .id(0L)
                        .build())
                .template(TemplateEntity.builder()
                        .id(0L)
                        .build())
                .status(statusEntity)
                .build()));
        when(mockCustomProductRepository.findCustomProductEntitiesByStatusAndUserId(eq(statusEntity), eq(UserEntity.builder()
                .id(0L)
                .build()), any(Pageable.class))).thenReturn(customProductEntities);

        // Configure AssemblingRepository.findAssemblingEntitiesByCustomProductId(...)
        final List<AssemblingEntity> assemblingEntities = List.of(AssemblingEntity.builder()
                .customProductId(CustomProductEntity.builder()
                        .id(0L)
                        .userId(UserEntity.builder()
                                .id(0L)
                                .build())
                        .template(TemplateEntity.builder()
                                .id(0L)
                                .build())
                        .status(statusEntity)
                        .build())
                .componentId(ComponentEntity.builder()
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
                        .build())
                .build());
        when(mockAssemblingRepository.findAssemblingEntitiesByCustomProductId(any(CustomProductEntity.class))).thenReturn(assemblingEntities);

        // Configure ComponentSpecificationListRepository.findByComponentId(...)
        final List<Component_SpecificationList> componentSpecificationLists = List.of(
                Component_SpecificationList.builder()
                        .specificationType(SpecificationTypeEntity.builder()
                                .id(0L)
                                .specificationTypeName("specificationTypeName")
                                .build())
                        .value("value")
                        .build());
        when(mockComponentSpecificationListRepository.findByComponentId(any(ComponentEntity.class))).thenReturn(componentSpecificationLists);

        // Run the test
        final List<CustomProductResponse> result = customProductManagerImplUnderTest.getCustomProductsOfUserByState(0L,
                0L, 1, 10, 1);

        // Verify the results
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void testGetCustomProductsOfUserByState_UserRepositoryExistsByIdReturnsFalse() {
        // Setup
        when(mockUserRepository.existsById(0L)).thenReturn(false);

        // Run the test
        assertThatThrownBy(
                () -> customProductManagerImplUnderTest.getCustomProductsOfUserByState(0L, 0L, 0, 0, 0))
                .isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testGetCustomProductsOfUserByState_StatusRepositoryReturnsFalse() {
        // Setup
        when(mockUserRepository.existsById(0L)).thenReturn(true);
        when(mockStatusRepository.existsById(0)).thenReturn(false);

        // Run the test
        assertThatThrownBy(
                () -> customProductManagerImplUnderTest.getCustomProductsOfUserByState(0L, 0L, 0, 0, 0))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void testGetCustomProductsOfUserByState_CustomProductRepositoryReturnsNoItems() {
        // Setup
        when(mockUserRepository.existsById(0L)).thenReturn(true);
        when(mockStatusRepository.existsById(1)).thenReturn(true);
        when(mockUserRepository.findUserEntityById(0L)).thenReturn(UserEntity.builder()
                .id(0L)
                .build());
        when(mockCustomProductRepository.findCustomProductEntitiesByStatusAndUserId(eq(StatusEntity.builder()
                .id(1)
                .name("DRAFT")
                .build()), eq(UserEntity.builder()
                .id(0L)
                .build()), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final List<CustomProductResponse> result = customProductManagerImplUnderTest.getCustomProductsOfUserByState(0L,
                0L, 1, 10, 1);

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGetCustomProductsOfUserByState_AssemblingRepositoryReturnsNoItems() {
        // Setup
        final List<CustomProductResponse> expectedResult = List.of(CustomProductResponse.builder()
                .userId(0L)
                .customProductId(0L)
                .templateId(0L)
                .componentsIncluded(Collections.emptyList())
                .statusId(1L)
                .build());

        // Mock user and status existence
        when(mockUserRepository.existsById(0L)).thenReturn(true);
        when(mockStatusRepository.existsById(1)).thenReturn(true);
        when(mockUserRepository.findUserEntityById(0L)).thenReturn(UserEntity.builder()
                .id(0L)
                .build());

        // Configure CustomProductRepository.findCustomProductEntitiesByStatusAndUserId(...)
        final Page<CustomProductEntity> customProductEntities = new PageImpl<>(List.of(CustomProductEntity.builder()
                .id(0L)
                .userId(UserEntity.builder()
                        .id(0L)
                        .build())
                .template(TemplateEntity.builder()
                        .id(0L)
                        .build())
                .status(StatusEntity.builder()
                        .id(1)
                        .name("DRAFT")
                        .build())
                .build()));
        when(mockCustomProductRepository.findCustomProductEntitiesByStatusAndUserId(eq(StatusEntity.builder()
                .id(1)
                .name("DRAFT")
                .build()), eq(UserEntity.builder()
                .id(0L)
                .build()), any(Pageable.class))).thenReturn(customProductEntities);

        // Mock assembling repository to return empty list
        when(mockAssemblingRepository.findAssemblingEntitiesByCustomProductId(any(CustomProductEntity.class)))
                .thenReturn(Collections.emptyList());

        // Run the test
        final List<CustomProductResponse> result = customProductManagerImplUnderTest.getCustomProductsOfUserByState(0L,
                0L, 1, 1, 1);

        // Verify the results
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }


    @Test
    void testGetNumberOfCustomProductsOfUserByStatus() {
        // Setup
        when(mockUserRepository.existsById(0L)).thenReturn(true);
        when(mockStatusRepository.existsById(0)).thenReturn(true);

        // Configure StatusRepository.findById(...).
        final StatusEntity statusEntity = StatusEntity.builder()
                .id(0)
                .name("name")
                .build();
        when(mockStatusRepository.findById(0)).thenReturn(statusEntity);

        when(mockUserRepository.findUserEntityById(0L)).thenReturn(UserEntity.builder()
                .id(0L)
                .build());
        when(mockCustomProductRepository.countCustomProductEntitiesByStatusAndUserId(StatusEntity.builder()
                .id(0)
                .name("name")
                .build(), UserEntity.builder()
                .id(0L)
                .build())).thenReturn(0);

        // Run the test
        final int result = customProductManagerImplUnderTest.getNumberOfCustomProductsOfUserByStatus(0L, 0L, 0);

        // Verify the results
        assertThat(result).isZero();
    }

    @Test
    void testGetNumberOfCustomProductsOfUserByStatus_UserRepositoryExistsByIdReturnsFalse() {
        // Setup
        when(mockUserRepository.existsById(0L)).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> customProductManagerImplUnderTest.getNumberOfCustomProductsOfUserByStatus(0L, 0L,
                0)).isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testGetNumberOfCustomProductsOfUserByStatus_StatusRepositoryExistsByIdReturnsFalse() {
        // Setup
        when(mockUserRepository.existsById(0L)).thenReturn(true);
        when(mockStatusRepository.existsById(0)).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> customProductManagerImplUnderTest.getNumberOfCustomProductsOfUserByStatus(0L, 0L,
                0)).isInstanceOf(InvalidInputException.class);
    }

    @Test
    void testDeleteCustomProduct() {
        // Setup
        // Configure CustomProductRepository.findById(...).
        final CustomProductEntity productEntity = CustomProductEntity.builder()
                .id(0L)
                .userId(UserEntity.builder()
                        .id(0L)
                        .build())
                .template(TemplateEntity.builder()
                        .id(0L)
                        .build())
                .status(StatusEntity.builder()
                        .id(0)
                        .name("name")
                        .build())
                .build();
        when(mockCustomProductRepository.findById(0L)).thenReturn(productEntity);

        when(mockCustomProductRepository.existsById(0L)).thenReturn(true);

        // Run the test
        customProductManagerImplUnderTest.deleteCustomProduct(0L, 0L);

        // Verify the results
        verify(mockAssemblingRepository).deleteAssemblingEntitiesByCustomProductId(CustomProductEntity.builder()
                .id(0L)
                .userId(UserEntity.builder()
                        .id(0L)
                        .build())
                .template(TemplateEntity.builder()
                        .id(0L)
                        .build())
                .status(StatusEntity.builder()
                        .id(0)
                        .name("name")
                        .build())
                .build());
        verify(mockCustomProductRepository).deleteById(0L);
    }

    @Test
    void testDeleteCustomProduct_CustomProductRepositoryFindByIdReturnsNull() {
        // Setup
        when(mockCustomProductRepository.findById(0L)).thenReturn(null);

        // Run the test
        assertThatThrownBy(() -> customProductManagerImplUnderTest.deleteCustomProduct(0L, 0L))
                .isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testDeleteCustomProduct_CustomProductRepositoryExistsByIdReturnsFalse() {
        // Setup
        // Configure CustomProductRepository.findById(...).
        final CustomProductEntity productEntity = CustomProductEntity.builder()
                .id(0L)
                .userId(UserEntity.builder()
                        .id(0L)
                        .build())
                .template(TemplateEntity.builder()
                        .id(0L)
                        .build())
                .status(StatusEntity.builder()
                        .id(0)
                        .name("name")
                        .build())
                .build();
        when(mockCustomProductRepository.findById(0L)).thenReturn(productEntity);

        when(mockCustomProductRepository.existsById(0L)).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> customProductManagerImplUnderTest.deleteCustomProduct(0L, 0L))
                .isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testUpdateCustomProduct() {
        // Setup
        final UpdateCustomTemplateRequest request = UpdateCustomTemplateRequest.builder()
                .componentsIncluded(List.of(ComponentInCustomProductInput.builder()
                        .componentId(0L)
                        .build()))
                .statusId(1)
                .build();

        // Create consistent objects
        final ComponentEntity componentEntity = createDefaultComponentEntity();
        final CustomProductEntity productEntity = CustomProductEntity.builder()
                .id(0L)
                .userId(UserEntity.builder().id(0L).build())
                .template(TemplateEntity.builder().id(0L).active(true).build())
                .status(StatusEntity.builder().id(1).name("DRAFT").build())
                .build();

        // Mock repository behavior
        when(mockCustomProductRepository.findById(0L)).thenReturn(productEntity);
        when(mockCustomProductRepository.existsById(0L)).thenReturn(true);
        when(mockComponentRepository.existsById(0L)).thenReturn(true);
        when(mockComponentRepository.findComponentEntityByComponentId(0L)).thenReturn(componentEntity);

        when(mockAssemblingRepository.findAssemblingEntitiesByCustomProductId((productEntity)))
                .thenReturn(Collections.emptyList());

        when(mockAssemblingRepository.existsAssemblingEntityByComponentIdAndCustomProductId((componentEntity),(productEntity)))
                .thenReturn(false);
        when(mockTemplateRepository.existsActiveTemplateEntityById(0L)).thenReturn(true);

        // Run the test
        customProductManagerImplUnderTest.updateCustomProduct(0L, request, 0L);

        // Verify interactions
        verify(mockAssemblingRepository).deleteAll(Collections.emptyList());
        verify(mockAssemblingRepository).save(argThat(entity ->
                entity.getCustomProductId().equals(productEntity) &&
                        entity.getComponentId().equals(componentEntity)
        ));
    }


    @Test
    void testUpdateCustomProduct_CustomProductRepositoryFindByIdReturnsNull() {
        // Setup
        final UpdateCustomTemplateRequest request = UpdateCustomTemplateRequest.builder()
                .componentsIncluded(List.of(ComponentInCustomProductInput.builder()
                        .componentId(0L)
                        .build()))
                .build();
        when(mockCustomProductRepository.findById(0L)).thenReturn(null);

        // Run the test
        assertThatThrownBy(() -> customProductManagerImplUnderTest.updateCustomProduct(0L, request, 0L))
                .isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testUpdateCustomProduct_TemplateNotActive() {
        // Arrange
        long productId = 1L;
        long authenticatedUserId = 2L;

        // Mock product retrieval
        CustomProductEntity productEntity = CustomProductEntity.builder()
                .id(productId)
                .template(TemplateEntity.builder().id(3L).build())
                .status(StatusEntity.builder().id(1).build())
                .build();
        when(mockCustomProductRepository.findById(productId)).thenReturn(productEntity);

        // Mock active template check
        when(mockTemplateRepository.existsActiveTemplateEntityById(3L)).thenReturn(false);

        UpdateCustomTemplateRequest request = UpdateCustomTemplateRequest.builder()
                .statusId(2)
                .componentsIncluded(Collections.emptyList())
                .build();

        // Act & Assert
        assertThatThrownBy(() -> customProductManagerImplUnderTest.updateCustomProduct(productId, request, authenticatedUserId))
                .isInstanceOf(ObjectNotFound.class)
                .hasMessageContaining("Active template not found");

        verify(mockCustomProductRepository, times(1)).findById(productId);
        verify(mockTemplateRepository, times(1)).existsActiveTemplateEntityById(3L);
        verifyNoMoreInteractions(mockCustomProductRepository, mockTemplateRepository);
    }


    @Test
    void testUpdateCustomProduct_CustomProductRepositoryExistsByIdReturnsFalse() {
        // Setup
        final UpdateCustomTemplateRequest request = UpdateCustomTemplateRequest.builder()
                .componentsIncluded(List.of(ComponentInCustomProductInput.builder()
                        .componentId(0L)
                        .build()))
                .build();

        // Configure CustomProductRepository.findById(...).
        final CustomProductEntity productEntity = CustomProductEntity.builder()
                .id(0L)
                .userId(UserEntity.builder()
                        .id(0L)
                        .build())
                .template(TemplateEntity.builder()
                        .id(0L)
                        .build())
                .status(StatusEntity.builder()
                        .id(0)
                        .name("name")
                        .build())
                .build();
        when(mockCustomProductRepository.findById(0L)).thenReturn(productEntity);

        // Run the test
        assertThatThrownBy(() -> customProductManagerImplUnderTest.updateCustomProduct(0L, request, 0L))
                .isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testUpdateCustomProduct_AssemblingRepositoryFindAssemblingEntitiesByCustomProductIdReturnsNoItems() {
        // Setup
        final UpdateCustomTemplateRequest request = UpdateCustomTemplateRequest.builder()
                .componentsIncluded(List.of(ComponentInCustomProductInput.builder()
                        .componentId(0L)
                        .build()))
                .statusId(1)
                .build();

        // Create default entities
        final CustomProductEntity productEntity = CustomProductEntity.builder()
                .id(0L)
                .userId(UserEntity.builder().id(0L).build())
                .template(TemplateEntity.builder().id(0L).build())
                .status(StatusEntity.builder().id(0).name("name").build())
                .build();
        final ComponentEntity componentEntity = createDefaultComponentEntity();

        // Mock repository behavior
        when(mockCustomProductRepository.findById(0L)).thenReturn(productEntity);
        when(mockCustomProductRepository.existsById(0L)).thenReturn(true);
        when(mockAssemblingRepository.findAssemblingEntitiesByCustomProductId((productEntity)))
                .thenReturn(Collections.emptyList());
        when(mockComponentRepository.existsById(0L)).thenReturn(true);
        when(mockComponentRepository.findComponentEntityByComponentId(0L)).thenReturn(componentEntity);

        // Use any() matcher for complex objects in strict mode
        when(mockAssemblingRepository.existsAssemblingEntityByComponentIdAndCustomProductId(any(), eq(productEntity)))
                .thenReturn(false);
        when(mockTemplateRepository.existsActiveTemplateEntityById(0L)).thenReturn(true);

        // Run the test
        customProductManagerImplUnderTest.updateCustomProduct(0L, request, 0L);

        // Verify interactions
        verify(mockAssemblingRepository).deleteAll(Collections.emptyList());
        verify(mockAssemblingRepository).save(argThat(entity ->
                entity.getCustomProductId().equals(productEntity) &&
                        entity.getComponentId().equals(componentEntity)
        ));
    }


    @Test
    void testUpdateCustomProduct_ComponentSpecificationListRepositoryReturnsNoItems() {
        // Setup
        final UpdateCustomTemplateRequest request = UpdateCustomTemplateRequest.builder()
                .componentsIncluded(List.of(ComponentInCustomProductInput.builder()
                        .componentId(1L)
                        .build()))
                .statusId(1)
                .build();

        // Create default entities
        final CustomProductEntity productEntity = CustomProductEntity.builder()
                .id(1L)
                .userId(UserEntity.builder().id(0L).build())
                .template(TemplateEntity.builder().id(0L).build())
                .status(StatusEntity.builder().id(1).name("DRAFT").build())
                .build();
        final ComponentEntity componentEntity = createDefaultComponentEntity();
        componentEntity.setComponentId(1L); // Set ID to match the request input

        // Create an AssemblingEntity for mocking
        final AssemblingEntity assemblingEntity = AssemblingEntity.builder()
                .customProductId(productEntity)
                .componentId(componentEntity)
                .build();

        // Stub repository methods
        when(mockCustomProductRepository.findById(1L)).thenReturn(productEntity);
        when(mockCustomProductRepository.existsById(1L)).thenReturn(true);
        when(mockComponentRepository.existsById(1L)).thenReturn(true);
        when(mockComponentRepository.findComponentEntityByComponentId(1L)).thenReturn(componentEntity);

        when(mockAssemblingRepository.findAssemblingEntitiesByCustomProductId(productEntity))
                .thenReturn(List.of(assemblingEntity));

        when(mockComponentSpecificationListRepository.findByComponentId(componentEntity))
                .thenReturn(Collections.emptyList());

        when(mockAssemblingRepository.existsAssemblingEntityByComponentIdAndCustomProductId((componentEntity), (productEntity)))
                .thenReturn(false);

        when(mockTemplateRepository.existsActiveTemplateEntityById(0L)).thenReturn(true);

        // Run the test
        customProductManagerImplUnderTest.updateCustomProduct(1L, request, 0L);

        // Verify interactions
        verify(mockAssemblingRepository).deleteAll(argThat(argument ->
                argument instanceof List && ((List<?>) argument).stream().allMatch(entity ->
                        entity instanceof AssemblingEntity &&
                                ((AssemblingEntity) entity).getCustomProductId().equals(productEntity) &&
                                ((AssemblingEntity) entity).getComponentId().equals(componentEntity)
                )
        ));

        verify(mockAssemblingRepository).save(argThat(assemblingEntityArg ->
                assemblingEntityArg.getCustomProductId().equals(productEntity) &&
                        assemblingEntityArg.getComponentId().equals(componentEntity)
        ));
    }



    @Test
    void testUpdateCustomProduct_ComponentRepositoryExistsByIdReturnsFalse() {
        // Setup
        final UpdateCustomTemplateRequest request = UpdateCustomTemplateRequest.builder()
                .componentsIncluded(List.of(ComponentInCustomProductInput.builder()
                        .componentId(0L)
                        .build()))
                .statusId(1)
                .build();

        final CustomProductEntity productEntity = CustomProductEntity.builder()
                .id(0L)
                .userId(UserEntity.builder().id(0L).build())
                .template(TemplateEntity.builder().id(0L).build())
                .status(StatusEntity.builder().id(1).name("DRAFT").build())
                .build();
        when(mockCustomProductRepository.findById(0L)).thenReturn(productEntity);

        final ComponentEntity defaultComponentEntity = createDefaultComponentEntity();

        // Run the test
        assertThatThrownBy(() -> customProductManagerImplUnderTest.updateCustomProduct(0L, request, 0L))
                .isInstanceOf(ObjectNotFound.class);

        // Verify
        verify(mockComponentSpecificationListRepository, never()).findByComponentId(defaultComponentEntity);
    }


    @Test
    void testUpdateCustomProduct_AssemblingRepositoryExistsAssemblingEntityByComponentIdAndCustomProductIdReturnsTrue() {
        // Setup
        final UpdateCustomTemplateRequest request = UpdateCustomTemplateRequest.builder()
                .componentsIncluded(List.of(ComponentInCustomProductInput.builder()
                        .componentId(0L)
                        .build()))
                .statusId(1)
                .build();

        final CustomProductEntity productEntity = CustomProductEntity.builder()
                .id(0L)
                .userId(UserEntity.builder().id(0L).build())
                .template(TemplateEntity.builder().id(0L).active(true).build())
                .status(StatusEntity.builder().id(1).name("DRAFT").build())
                .build();
        when(mockCustomProductRepository.findById(0L)).thenReturn(productEntity);
        when(mockCustomProductRepository.existsById(0L)).thenReturn(true);
        when(mockTemplateRepository.existsActiveTemplateEntityById(0L)).thenReturn(true);

        final ComponentEntity componentEntity = ComponentEntity.builder()
                .componentId(0L) // Matches the ID in the request
                .componentName("componentName")
                .componentType(ComponentTypeEntity.builder()
                        .id(0L)
                        .componentTypeName("componentTypeName")
                        .category(CategoryEntity.builder()
                                .id(0L)
                                .categoryName("categoryName")
                                .build())
                        .build())
                .build();
        when(mockComponentRepository.findComponentEntityByComponentId(0L)).thenReturn(componentEntity);
        when(mockComponentRepository.existsById(0L)).thenReturn(true);

        // Run the test
        customProductManagerImplUnderTest.updateCustomProduct(0L, request, 0L);

        // Verify
        verify(mockAssemblingRepository).deleteAll(anyList());
    }

    @Test
    void testGetTotalNumberOfCustomProductsByStatus() {
        // Arrange
        Long categoryId = 1L;
        String status = "FINISHED";
        int expectedCount = 10;
        when(mockCustomProductRepository.countCustomProductEntitiesByStatusAndCategoryId(
                categoryId, StatusConverter.convertFromBaseToEntity(Status.valueOf(status))
        )).thenReturn(expectedCount);

        // Act
        int result = customProductManagerImplUnderTest.getTotalNumberOfCustomProductsByStatus(categoryId, status);

        // Assert
        assertThat(result).isEqualTo(expectedCount);
    }

    @Test
    void testGetTotalNumberOfProductsByConfigurationTypeAndStatus() {
        // Arrange
        String configurationType = "PC";
        String status = "FINISHED";
        int expectedCount = 5;
        when(mockCustomProductRepository.countCustomProductEntitiesByConfigurationTypeAndStatus(
                configurationType, StatusConverter.convertFromBaseToEntity(Status.valueOf(status))
        )).thenReturn(expectedCount);

        // Act
        int result = customProductManagerImplUnderTest.getTotalNumberOfProductsByConfigurationTypeAndStatus(configurationType, status);

        // Assert
        assertThat(result).isEqualTo(expectedCount);
    }

    @Test
    void testGetTotalIncome() {
        // Arrange
        double expectedIncome = 5000.00;
        when(mockCustomProductRepository.sumTotalIncome()).thenReturn(expectedIncome);

        // Act
        double result = customProductManagerImplUnderTest.getTotalIncome();

        // Assert
        assertThat(result).isEqualTo(expectedIncome);
    }

    @Test
    void testGetIncomeByConfigurationType() {
        // Arrange
        String configurationType = "DOWNHILL";
        double expectedIncome = 2000.00;
        when(mockCustomProductRepository.sumIncomeByConfigurationType(configurationType)).thenReturn(expectedIncome);

        // Act
        double result = customProductManagerImplUnderTest.getIncomeByConfigurationType(configurationType);

        // Assert
        assertThat(result).isEqualTo(expectedIncome);
    }

    @Test
    void testGetAverageOrderPrice() {
        // Arrange
        double expectedAveragePrice = 250.00;
        when(mockCustomProductRepository.calculateAverageFinishedProductPrice()).thenReturn(expectedAveragePrice);

        // Act
        double result = customProductManagerImplUnderTest.getAverageOrderPrice();

        // Assert
        assertThat(result).isEqualTo(expectedAveragePrice);
    }


    private CategoryEntity createDefaultCategoryEntity() {
        return CategoryEntity.builder()
                .id(1L)
                .categoryName("DefaultCategory")
                .build();
    }

    private ComponentEntity createDefaultComponentEntity() {
        return ComponentEntity.builder()
                .componentId(1L)
                .componentName("DefaultComponent")
                .componentType(ComponentTypeEntity.builder()
                        .id(1L)
                        .componentTypeName("DefaultComponentType")
                        .category(createDefaultCategoryEntity())
                        .build())
                .build();
    }


}
