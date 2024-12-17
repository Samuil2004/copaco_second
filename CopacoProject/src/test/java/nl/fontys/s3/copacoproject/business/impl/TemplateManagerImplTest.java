package nl.fontys.s3.copacoproject.business.impl;

import jakarta.persistence.EntityManager;
import nl.fontys.s3.copacoproject.business.CategoryManager;
import nl.fontys.s3.copacoproject.business.Exceptions.InvalidInputException;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectExistsAlreadyException;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.CreateTemplateRequest;
import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.TemplateObjectResponse;
import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.UpdateTemplateRequest;
import nl.fontys.s3.copacoproject.domain.Category;
import nl.fontys.s3.copacoproject.domain.ComponentType;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import nl.fontys.s3.copacoproject.domain.Template;
import nl.fontys.s3.copacoproject.persistence.CategoryRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeList_TemplateRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.TemplateRepository;
import nl.fontys.s3.copacoproject.persistence.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemplateManagerImplTest {

    @Mock
    private TemplateRepository mockTemplateRepository;
    @Mock
    private ComponentTypeList_TemplateRepository mockComponentTypeListRepository;
    @Mock
    private ComponentTypeRepository mockComponentTypeRepository;
    @Mock
    private CategoryManager mockCategoryManager;
    @Mock
    private CategoryRepository mockCategoryRepository;
    @Mock
    private EntityManager mockEntityManager;

    private TemplateManagerImpl templateManagerImplUnderTest;

    @BeforeEach
    void setUp() {
        templateManagerImplUnderTest = new TemplateManagerImpl(mockTemplateRepository, mockComponentTypeListRepository,
                mockComponentTypeRepository, mockCategoryManager, mockCategoryRepository);
        ReflectionTestUtils.setField(templateManagerImplUnderTest, "entityManager", mockEntityManager);
    }

    @Test
    void testCreateTemplate() throws Exception {
        // Setup
        final CreateTemplateRequest request = CreateTemplateRequest.builder()
                .categoryId(0L)
                .configurationType("configurationType")
                .name("newName")
                .componentTypes(List.of(0L))
                .build();
        final byte[] fileBytes = "mock image content".getBytes(); // Use consistent byte array for the image
        final MultipartFile file = new MockMultipartFile(
                "file", "test.png", MediaType.IMAGE_PNG_VALUE, fileBytes
        );

        // Configure CategoryManager.findCategoryById(...).
        final Category category = Category.builder()
                .categoryId(0L)
                .categoryName("categoryName")
                .build();
        when(mockCategoryManager.findCategoryById(0L)).thenReturn(category);

        when(mockTemplateRepository.existsTemplateEntityByNameAndCategory("newName", 0L)).thenReturn(false);
        when(mockComponentTypeRepository.existsById(0L)).thenReturn(true);

        // Configure TemplateRepository.save(...).
        final TemplateEntity templateEntity = TemplateEntity.builder()
                .id(0L)
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image(fileBytes) // Use the same byte array here
                .build();

        when(mockTemplateRepository.save(any(TemplateEntity.class))).thenReturn(templateEntity);

        // Configure EntityManager.find(...).
        final ComponentTypeEntity componentTypeEntity = ComponentTypeEntity.builder()
                .id(0L)
                .componentTypeName("componentTypeName")
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .build();
        when(mockEntityManager.find(ComponentTypeEntity.class, 0L)).thenReturn(componentTypeEntity);

        // Run the test
        templateManagerImplUnderTest.createTemplate(request, file);

        // Verify the results
        verify(mockComponentTypeListRepository).save(any(ComponentTypeList_Template.class));
    }


    @Test
    void testCreateTemplate_CategoryManagerReturnsNull() {
        // Setup
        final CreateTemplateRequest request = CreateTemplateRequest.builder()
                .categoryId(0L)
                .configurationType("configurationType")
                .name("newName")
                .componentTypes(List.of(0L))
                .build();
        final MultipartFile file = new MockMultipartFile("name", "content".getBytes());
        when(mockCategoryManager.findCategoryById(0L)).thenReturn(null);

        // Run the test
        assertThatThrownBy(() -> templateManagerImplUnderTest.createTemplate(request, file))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void testCreateTemplate_TemplateRepositoryExistsTemplateEntityByNameAndCategoryReturnsTrue() {
        // Setup
        final CreateTemplateRequest request = CreateTemplateRequest.builder()
                .categoryId(0L)
                .configurationType("configurationType")
                .name("newName")
                .componentTypes(List.of(0L))
                .build();
        final MultipartFile file = new MockMultipartFile("name", "content".getBytes());

        // Configure CategoryManager.findCategoryById(...).
        final Category category = Category.builder()
                .categoryId(0L)
                .categoryName("categoryName")
                .build();
        when(mockCategoryManager.findCategoryById(0L)).thenReturn(category);

        when(mockTemplateRepository.existsTemplateEntityByNameAndCategory("newName", 0L)).thenReturn(true);

        // Run the test
        assertThatThrownBy(() -> templateManagerImplUnderTest.createTemplate(request, file))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void testCreateTemplate_ComponentTypeRepositoryReturnsFalse() {
        // Setup
        final CreateTemplateRequest request = CreateTemplateRequest.builder()
                .categoryId(0L)
                .configurationType("configurationType")
                .name("newName")
                .componentTypes(List.of(0L))
                .build();
        final MultipartFile file = new MockMultipartFile("name", "content".getBytes());

        // Configure CategoryManager.findCategoryById(...).
        final Category category = Category.builder()
                .categoryId(0L)
                .categoryName("categoryName")
                .build();
        when(mockCategoryManager.findCategoryById(0L)).thenReturn(category);

        when(mockTemplateRepository.existsTemplateEntityByNameAndCategory("newName", 0L)).thenReturn(false);
        when(mockComponentTypeRepository.existsById(0L)).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> templateManagerImplUnderTest.createTemplate(request, file))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void testSaveComponentTypeListTemplate() {
        // Arrange
        final TemplateEntity templateEntity = TemplateEntity.builder()
                .id(0L)
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .build();

        final ComponentTypeEntity componentTypeEntity = ComponentTypeEntity.builder()
                .id(0L)
                .componentTypeName("componentTypeName")
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .build();

        when(mockEntityManager.find(ComponentTypeEntity.class, 0L)).thenReturn(componentTypeEntity);

        // Act
        templateManagerImplUnderTest.saveComponentTypeListTemplate(List.of(0L), templateEntity);

        // Capture the argument passed to save
        ArgumentCaptor<ComponentTypeList_Template> captor = ArgumentCaptor.forClass(ComponentTypeList_Template.class);
        verify(mockComponentTypeListRepository).save(captor.capture());

        // Verify the captured argument
        ComponentTypeList_Template savedItem = captor.getValue();
        assertThat(savedItem.getTemplate()).isEqualTo(templateEntity);
        assertThat(savedItem.getComponentType()).isEqualTo(componentTypeEntity);
    }


    @Test
    void testDeleteTemplate() {
        // Setup
        when(mockTemplateRepository.existsById(0L)).thenReturn(true);

        // Run the test
        templateManagerImplUnderTest.deleteTemplate(0L);

        // Verify the results
        verify(mockComponentTypeListRepository).deleteByTemplateId(0L);
        verify(mockTemplateRepository).deleteById(0L);
    }

    @Test
    void testDeleteTemplate_TemplateRepositoryExistsByIdReturnsFalse() {
        // Setup
        when(mockTemplateRepository.existsById(0L)).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> templateManagerImplUnderTest.deleteTemplate(0L)).isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testGetTemplateById() {
        // Setup
        final TemplateObjectResponse expectedResult = TemplateObjectResponse.builder()
                .templateId(0L)
                .category(Category.builder()
                        .categoryId(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .components(List.of("componentTypeName"))
                .build();
        when(mockTemplateRepository.existsById(0L)).thenReturn(true);

        // Configure TemplateRepository.findComponentTypeListByTemplateId(...).
        final List<ComponentTypeList_Template> componentTypeListTemplates = List.of(ComponentTypeList_Template.builder()
                .template(TemplateEntity.builder()
                        .id(0L)
                        .category(CategoryEntity.builder()
                                .id(0L)
                                .categoryName("categoryName")
                                .build())
                        .name("newName")
                        .configurationType("configurationType")
                        .image("content".getBytes())
                        .build())
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
                .build());
        when(mockTemplateRepository.findComponentTypeListByTemplateId(0L)).thenReturn(componentTypeListTemplates);

        // Configure TemplateRepository.findTemplateEntityById(...).
        final TemplateEntity templateEntity = TemplateEntity.builder()
                .id(0L)
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .build();
        when(mockTemplateRepository.findTemplateEntityById(0L)).thenReturn(templateEntity);

        // Run the test
        final TemplateObjectResponse result = templateManagerImplUnderTest.getTemplateById(0L);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetTemplateById_TemplateRepositoryExistsByIdReturnsFalse() {
        // Setup
        when(mockTemplateRepository.existsById(0L)).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> templateManagerImplUnderTest.getTemplateById(0L)).isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testGetTemplateById_TemplateRepositoryFindComponentTypeListByTemplateIdReturnsNoItems() {
        // Setup
        final TemplateObjectResponse expectedResult = TemplateObjectResponse.builder()
                .templateId(0L)
                .category(Category.builder()
                        .categoryId(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .components(Collections.emptyList())
                .build();
        when(mockTemplateRepository.existsById(0L)).thenReturn(true);
        when(mockTemplateRepository.findComponentTypeListByTemplateId(0L)).thenReturn(Collections.emptyList());

        // Configure TemplateRepository.findTemplateEntityById(...).
        final TemplateEntity templateEntity = TemplateEntity.builder()
                .id(0L)
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .build();
        when(mockTemplateRepository.findTemplateEntityById(0L)).thenReturn(templateEntity);

        // Run the test
        final TemplateObjectResponse result = templateManagerImplUnderTest.getTemplateById(0L);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetTemplatesByName() {
        // Setup
        final List<Template> expectedResult = List.of(Template.builder()
                .templateId(0L)
                .category(Category.builder()
                        .categoryId(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .components(List.of(ComponentType.builder()
                        .componentTypeId(0L)
                        .componentTypeName("componentTypeName")
                        .componentTypeImageUrl("componentTypeImageUrl")
                        .category(Category.builder()
                                .categoryId(0L)
                                .categoryName("categoryName")
                                .build())
                        .configurationTypes(List.of("configurationType"))
                        .specificationTypeList(List.of(SpecificationType.builder()
                                .specificationTypeId(0L)
                                .specificationTypeName("specificationTypeName")
                                .build()))
                        .build()))
                .build());

        // Configure TemplateRepository.findTemplateEntitiesByName(...).
        final List<TemplateEntity> templateEntities = List.of(TemplateEntity.builder()
                .id(0L)
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .build());
        when(mockTemplateRepository.findTemplateEntitiesByName("name")).thenReturn(templateEntities);

        // Configure TemplateRepository.findComponentTypeListByTemplateId(...).
        final List<ComponentTypeList_Template> componentTypeListTemplates = List.of(ComponentTypeList_Template.builder()
                .template(TemplateEntity.builder()
                        .id(0L)
                        .category(CategoryEntity.builder()
                                .id(0L)
                                .categoryName("categoryName")
                                .build())
                        .name("newName")
                        .configurationType("configurationType")
                        .image("content".getBytes())
                        .build())
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
                .build());
        when(mockTemplateRepository.findComponentTypeListByTemplateId(0L)).thenReturn(componentTypeListTemplates);

        // Run the test
        final List<Template> result = templateManagerImplUnderTest.getTemplatesByName("name");

        // Verify the results
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void testGetTemplatesByName_TemplateRepositoryFindTemplateEntitiesByNameReturnsNull() {
        // Setup
        when(mockTemplateRepository.findTemplateEntitiesByName("name")).thenReturn(null);

        // Run the test
        assertThatThrownBy(() -> templateManagerImplUnderTest.getTemplatesByName("name"))
                .isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testGetTemplatesByName_TemplateRepositoryFindTemplateEntitiesByNameReturnsNoItems() {
        // Setup
        when(mockTemplateRepository.findTemplateEntitiesByName("name")).thenReturn(Collections.emptyList());

        // Run the test
        assertThatThrownBy(() -> templateManagerImplUnderTest.getTemplatesByName("name"))
                .isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testGetTemplatesByName_TemplateRepositoryFindComponentTypeListByTemplateIdReturnsNoItems() {
        // Setup
        final List<Template> expectedResult = List.of(Template.builder()
                .templateId(0L)
                .category(Category.builder()
                        .categoryId(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                        .components(Collections.emptyList())
                .build());

        // Configure TemplateRepository.findTemplateEntitiesByName(...).
        final List<TemplateEntity> templateEntities = List.of(TemplateEntity.builder()
                .id(0L)
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .build());
        when(mockTemplateRepository.findTemplateEntitiesByName("name")).thenReturn(templateEntities);

        when(mockTemplateRepository.findComponentTypeListByTemplateId(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final List<Template> result = templateManagerImplUnderTest.getTemplatesByName("name");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetFilteredTemplates() {
        // Setup
        final List<TemplateObjectResponse> expectedResult = List.of(TemplateObjectResponse.builder()
                .templateId(0L)
                .category(Category.builder()
                        .categoryId(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .components(List.of("value")) // Expecting "value" in components
                .build());

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());

        // Mock template entities
        final TemplateEntity templateEntity = TemplateEntity.builder()
                .id(0L)
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .build();

        Page<TemplateEntity> templateEntities = new PageImpl<>(List.of(templateEntity));

        // Mock the componentTypeList associated with the template
        final ComponentTypeEntity componentTypeEntity = ComponentTypeEntity.builder()
                .id(0L)
                .componentTypeName("value") // Ensure this matches the expected "value"
                .build();

        final ComponentTypeList_Template componentTypeListTemplate = ComponentTypeList_Template.builder()
                .template(templateEntity)
                .componentType(componentTypeEntity)
                .build();

        // Mock repository behavior
        when(mockTemplateRepository.findTemplateEntitiesByCategoryAndConfigurationType(
                null, "configurationType", pageable)).thenReturn(templateEntities);

        when(mockTemplateRepository.findComponentTypeListByTemplateId(0L))
                .thenReturn(List.of(componentTypeListTemplate));

        // Run the test
        final List<TemplateObjectResponse> result = templateManagerImplUnderTest.getFilteredTemplates(10, 1, 0L,
                "configurationType");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }



    @Test
    void testGetFilteredTemplates_CategoryRepositoryExistsByIdReturnsFalse() {
        // Setup
        when(mockCategoryRepository.existsById(1L)).thenReturn(false);

        // Run the test
        assertThatThrownBy(
                () -> templateManagerImplUnderTest.getFilteredTemplates(10, 1, 1L, "configurationType"))
                .isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testGetFilteredTemplates_TemplateRepositoryFindTemplateEntitiesByCategoryAndConfigurationTypeReturnsNoItems() {
        // Arrange
        // Configure TemplateRepository to handle null category
        when(mockTemplateRepository.findTemplateEntitiesByCategoryAndConfigurationType(
                eq(null),
                eq("configurationType"),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Act & Assert
        assertThatThrownBy(
                () -> templateManagerImplUnderTest.getFilteredTemplates(10, 1, 0L, "configurationType"))
                .isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testGetFilteredTemplates_TemplateRepositoryFindComponentTypeListByTemplateIdReturnsNoItems() {
        // Setup
        final List<TemplateObjectResponse> expectedResult = List.of(TemplateObjectResponse.builder()
                .templateId(0L)
                .category(Category.builder()
                        .categoryId(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .components(Collections.emptyList())
                .build());
        // Configure TemplateRepository.findTemplateEntitiesByCategoryAndConfigurationType(...).
        final Page<TemplateEntity> templateEntities = new PageImpl<>(List.of(TemplateEntity.builder()
                .id(0L)
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .build()));
        when(mockTemplateRepository.findTemplateEntitiesByCategoryAndConfigurationType(eq(null), eq("configurationType"), any(Pageable.class))).thenReturn(templateEntities);

        when(mockTemplateRepository.findComponentTypeListByTemplateId(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final List<TemplateObjectResponse> result = templateManagerImplUnderTest.getFilteredTemplates(10, 1, 0L,
                "configurationType");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetNumberOfTemplates() {
        // Arrange
        Long categoryId = null;
        String configurationType = "configurationType";

        when(mockTemplateRepository.countTemplateEntitiesByCategoryAndConfigurationType(null, configurationType))
                .thenReturn(5);

        // Act
        int result = templateManagerImplUnderTest.getNumberOfTemplates(categoryId, configurationType);

        // Assert
        assertThat(result).isEqualTo(5);

        // Verify interaction with repository
        verify(mockTemplateRepository).countTemplateEntitiesByCategoryAndConfigurationType(null, configurationType);
    }


    @Test
    void testGetNumberOfTemplates_CategoryRepositoryExistsByIdReturnsFalse() {
        // Arrange
        when(mockCategoryRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> templateManagerImplUnderTest.getNumberOfTemplates(1L, "configurationType"))
                .isInstanceOf(ObjectNotFound.class);
    }

    @Test
    void testGetTemplates() {
        // Setup
        final List<Template> expectedResult = List.of(Template.builder()
                .templateId(0L)
                .category(Category.builder()
                        .categoryId(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .components(List.of(ComponentType.builder()
                        .componentTypeId(0L)
                        .componentTypeName("componentTypeName")
                        .componentTypeImageUrl("componentTypeImageUrl")
                        .category(Category.builder()
                                .categoryId(0L)
                                .categoryName("categoryName")
                                .build())
                        .configurationTypes(List.of("configurationType"))
                        .specificationTypeList(List.of(SpecificationType.builder()
                                .specificationTypeId(0L)
                                .specificationTypeName("specificationTypeName")
                                .build()))
                        .build()))
                .build());

        // Configure TemplateRepository.findAll(...).
        final List<TemplateEntity> templateEntities = List.of(TemplateEntity.builder()
                .id(0L)
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .build());
        when(mockTemplateRepository.findAll()).thenReturn(templateEntities);

        // Configure TemplateRepository.findComponentTypeListByTemplateId(...).
        final List<ComponentTypeList_Template> componentTypeListTemplates = List.of(ComponentTypeList_Template.builder()
                .template(TemplateEntity.builder()
                        .id(0L)
                        .category(CategoryEntity.builder()
                                .id(0L)
                                .categoryName("categoryName")
                                .build())
                        .name("newName")
                        .configurationType("configurationType")
                        .image("content".getBytes())
                        .build())
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
                .build());
        when(mockTemplateRepository.findComponentTypeListByTemplateId(0L)).thenReturn(componentTypeListTemplates);

        // Run the test
        final List<Template> result = templateManagerImplUnderTest.getTemplates();

        // Verify the results
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void testGetTemplates_TemplateRepositoryFindAllReturnsNoItems() {
        // Setup
        when(mockTemplateRepository.findAll()).thenReturn(Collections.emptyList());

        // Run the test
        final List<Template> result = templateManagerImplUnderTest.getTemplates();

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGetTemplates_TemplateRepositoryFindComponentTypeListByTemplateIdReturnsNoItems() {
        // Setup
        final List<Template> expectedResult = List.of(Template.builder()
                .templateId(0L)
                .category(Category.builder()
                        .categoryId(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .components(Collections.emptyList())
                .build());

        // Configure TemplateRepository.findAll(...).
        final List<TemplateEntity> templateEntities = List.of(TemplateEntity.builder()
                .id(0L)
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .build());
        when(mockTemplateRepository.findAll()).thenReturn(templateEntities);

        when(mockTemplateRepository.findComponentTypeListByTemplateId(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final List<Template> result = templateManagerImplUnderTest.getTemplates();

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testUpdateTemplate() throws Exception {
        // Setup
        final UpdateTemplateRequest request = UpdateTemplateRequest.builder()
                .categoryId(0L)
                .name("newName")
                .componentTypes(List.of(0L))
                .build();

        final MultipartFile file = new MockMultipartFile(
                "file", "test.png", MediaType.IMAGE_PNG_VALUE, "mock image content".getBytes()
        );

        final TemplateEntity templateEntity = TemplateEntity.builder()
                .id(0L)
                .category(CategoryEntity.builder().id(0L).categoryName("categoryName").build())
                .name("newName")
                .configurationType("configurationType")
                .image("mock image content".getBytes())
                .build();
        when(mockTemplateRepository.findTemplateEntityById(0L)).thenReturn(templateEntity);

        when(mockCategoryRepository.existsById(0L)).thenReturn(true);
        when(mockCategoryManager.findCategoryById(0L)).thenReturn(Category.builder()
                .categoryId(0L).categoryName("categoryName").build());
        when(mockComponentTypeRepository.existsById(0L)).thenReturn(true);

        when(mockComponentTypeListRepository.findComponentTypeList_TemplatesByTemplate(any(TemplateEntity.class)))
                .thenReturn(Collections.emptyList());
        // Configure ComponentTypeRepository.findComponentTypeEntityById(...)
        final ComponentTypeEntity componentTypeEntity = ComponentTypeEntity.builder()
                .id(0L)
                .componentTypeName("componentTypeName")
                .componentTypeImageUrl("componentTypeImageUrl")
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .configurationType("configurationType")
                .build();
        when(mockComponentTypeRepository.findComponentTypeEntityById(0L)).thenReturn(componentTypeEntity);

        // Run the test
        templateManagerImplUnderTest.updateTemplate(0L, request, file);

        // Verify interactions
        verify(mockTemplateRepository).save(any(TemplateEntity.class));
        verify(mockComponentTypeListRepository).deleteAll(Collections.emptyList());
        verify(mockComponentTypeListRepository).save(any(ComponentTypeList_Template.class));
    }


    @Test
    void testUpdateTemplate_TemplateRepositoryFindTemplateEntityByIdReturnsNull() {
        // Setup
        final UpdateTemplateRequest request = UpdateTemplateRequest.builder()
                .categoryId(0L)
                .name("newName")
                .componentTypes(List.of(0L))
                .build();
        final MultipartFile file = new MockMultipartFile("name", "content".getBytes());
        when(mockTemplateRepository.findTemplateEntityById(0L)).thenReturn(null);

        // Run the test
        assertThatThrownBy(() -> templateManagerImplUnderTest.updateTemplate(0L, request, file))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void testUpdateTemplate_CategoryRepositoryReturnsFalse() {
        // Setup
        final UpdateTemplateRequest request = UpdateTemplateRequest.builder()
                .categoryId(0L)
                .name("newName")
                .componentTypes(List.of(0L))
                .build();
        final MultipartFile file = new MockMultipartFile("name", "content".getBytes());

        // Configure TemplateRepository.findTemplateEntityById(...).
        final TemplateEntity templateEntity = TemplateEntity.builder()
                .id(0L)
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .build();
        when(mockTemplateRepository.findTemplateEntityById(0L)).thenReturn(templateEntity);

        when(mockCategoryRepository.existsById(0L)).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> templateManagerImplUnderTest.updateTemplate(0L, request, file))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void testUpdateTemplate_ComponentTypeRepositoryExistsByIdReturnsFalse() {
        // Setup
        final UpdateTemplateRequest request = UpdateTemplateRequest.builder()
                .categoryId(0L)
                .name("newName")
                .componentTypes(List.of(0L))
                .build();
        final MultipartFile file = new MockMultipartFile("name", "content".getBytes());

        // Configure TemplateRepository.findTemplateEntityById(...).
        final TemplateEntity templateEntity = TemplateEntity.builder()
                .id(0L)
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .build();
        when(mockTemplateRepository.findTemplateEntityById(0L)).thenReturn(templateEntity);

        when(mockCategoryRepository.existsById(0L)).thenReturn(true);

        // Configure CategoryManager.findCategoryById(...).
        final Category category = Category.builder()
                .categoryId(0L)
                .categoryName("categoryName")
                .build();
        when(mockCategoryManager.findCategoryById(0L)).thenReturn(category);

        when(mockComponentTypeRepository.existsById(0L)).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> templateManagerImplUnderTest.updateTemplate(0L, request, file))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void testUpdateTemplate_TemplateRepositoryExistsTemplateEntityForUpdateReturnsTrue() {
        // Setup
        final UpdateTemplateRequest request = UpdateTemplateRequest.builder()
                .categoryId(0L)
                .name("newName")
                .componentTypes(List.of(0L))
                .build();
        final MultipartFile file = new MockMultipartFile("name", "content".getBytes());

        // Configure TemplateRepository.findTemplateEntityById(...).
        final TemplateEntity templateEntity = TemplateEntity.builder()
                .id(0L)
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .build();
        when(mockTemplateRepository.findTemplateEntityById(0L)).thenReturn(templateEntity);

        when(mockCategoryRepository.existsById(0L)).thenReturn(true);

        // Configure CategoryManager.findCategoryById(...).
        final Category category = Category.builder()
                .categoryId(0L)
                .categoryName("categoryName")
                .build();
        when(mockCategoryManager.findCategoryById(0L)).thenReturn(category);

        when(mockComponentTypeRepository.existsById(0L)).thenReturn(true);
        when(mockTemplateRepository.existsTemplateEntityForUpdate(0L, "newName", 0L)).thenReturn(true);

        // Run the test
        assertThatThrownBy(() -> templateManagerImplUnderTest.updateTemplate(0L, request, file))
                .isInstanceOf(ObjectExistsAlreadyException.class);
    }

    @Test
    void testUpdateTemplate_ComponentTypeList_TemplateRepositoryFindComponentTypeList_TemplatesByTemplateReturnsNoItems() throws Exception {
        // Setup
        final UpdateTemplateRequest request = UpdateTemplateRequest.builder()
                .categoryId(0L)
                .name("newName")
                .componentTypes(List.of(0L))
                .build();
        final byte[] fileBytes = "mock image content".getBytes(); // Use consistent byte array for the image
        final MultipartFile file = new MockMultipartFile(
                "file", "test.png", MediaType.IMAGE_PNG_VALUE, fileBytes
        );

        // Configure TemplateRepository.findTemplateEntityById(...).
        final TemplateEntity templateEntity = TemplateEntity.builder()
                .id(0L)
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image(fileBytes)
                .build();
        when(mockTemplateRepository.findTemplateEntityById(0L)).thenReturn(templateEntity);

        when(mockCategoryRepository.existsById(0L)).thenReturn(true);

        // Configure CategoryManager.findCategoryById(...).
        final Category category = Category.builder()
                .categoryId(0L)
                .categoryName("categoryName")
                .build();
        when(mockCategoryManager.findCategoryById(0L)).thenReturn(category);

        when(mockComponentTypeRepository.existsById(0L)).thenReturn(true);
        when(mockTemplateRepository.existsTemplateEntityForUpdate(0L, "newName", 0L)).thenReturn(false);
        when(mockComponentTypeListRepository.findComponentTypeList_TemplatesByTemplate(TemplateEntity.builder()
                .id(0L)
                .category(CategoryEntity.builder()
                        .id(0L)
                        .categoryName("categoryName")
                        .build())
                .name("newName")
                .configurationType("configurationType")
                .image(fileBytes)
                .build())).thenReturn(Collections.emptyList());

        // Configure ComponentTypeRepository.findComponentTypeEntityById(...).
        final ComponentTypeEntity componentTypeEntity = ComponentTypeEntity.builder()
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
                .build();
        when(mockComponentTypeRepository.findComponentTypeEntityById(0L)).thenReturn(componentTypeEntity);

        when(mockComponentTypeListRepository.existsById(argThat(cpk ->
                cpk.getTemplate().getId() == 0L &&
                        cpk.getComponentType().getId() == 0L
        ))).thenReturn(false);


        // Run the test
        templateManagerImplUnderTest.updateTemplate(0L, request, file);

        // Verify the results
        ArgumentCaptor<ComponentTypeList_Template> captor = ArgumentCaptor.forClass(ComponentTypeList_Template.class);

        verify(mockComponentTypeListRepository).save(captor.capture());
        ComponentTypeList_Template captured = captor.getValue();

        assertThat(captured.getTemplate().getId()).isEqualTo(0L);
        assertThat(captured.getComponentType().getId()).isEqualTo(0L);

    }

    @Test
    void testUpdateTemplate_ComponentTypeList_TemplateRepositoryExistsByIdReturnsTrue() throws Exception {
        // Setup
        final UpdateTemplateRequest request = UpdateTemplateRequest.builder()
                .categoryId(0L)
                .name("newName")
                .componentTypes(List.of(0L))
                .build();
        final MultipartFile file = new MockMultipartFile(
                "file", "test.png", MediaType.IMAGE_PNG_VALUE, "mock image content".getBytes()
        );

        final CategoryEntity categoryEntity = CategoryEntity.builder()
                .id(0L)
                .categoryName("categoryName")
                .build();

        final TemplateEntity templateEntity = TemplateEntity.builder()
                .id(0L)
                .category(categoryEntity)
                .name("newName")
                .configurationType("configurationType")
                .image("content".getBytes())
                .build();

        // Mock repository responses
        when(mockTemplateRepository.findTemplateEntityById(0L)).thenReturn(templateEntity);
        when(mockCategoryRepository.existsById(0L)).thenReturn(true);
        when(mockCategoryManager.findCategoryById(0L)).thenReturn(Category.builder()
                .categoryId(0L)
                .categoryName("categoryName")
                .build());
        when(mockComponentTypeRepository.existsById(0L)).thenReturn(true); // Ensure this passes
        when(mockComponentTypeRepository.findComponentTypeEntityById(0L)).thenReturn(
                ComponentTypeEntity.builder()
                        .id(0L)
                        .componentTypeName("componentTypeName")
                        .category(categoryEntity)
                        .build()
        );

        when(mockComponentTypeListRepository.findComponentTypeList_TemplatesByTemplate(templateEntity))
                .thenReturn(Collections.emptyList());
        when(mockComponentTypeListRepository.existsById(any())).thenReturn(true);

        // Run the test
        templateManagerImplUnderTest.updateTemplate(0L, request, file);

        // Verify results
        verify(mockTemplateRepository).save(templateEntity);
        verify(mockComponentTypeListRepository).save(any());
    }

}
