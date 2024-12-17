package nl.fontys.s3.copacoproject.business.impl;

import nl.fontys.s3.copacoproject.business.CategoryManager;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.domain.Category;
import nl.fontys.s3.copacoproject.domain.Template;
import nl.fontys.s3.copacoproject.persistence.*;
import nl.fontys.s3.copacoproject.persistence.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TemplateManagerImplTest {

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private ComponentTypeList_TemplateRepository componentTypeListRepository;

    @Mock
    private ComponentTypeRepository componentTypeRepository;

    @Mock
    private CategoryManager categoryManager;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private TemplateManagerImpl templateManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inject the mock EntityManager
        templateManager = new TemplateManagerImpl(
                templateRepository,
                componentTypeListRepository,
                componentTypeRepository,
                categoryManager,
                categoryRepository
        );
        ReflectionTestUtils.setField(templateManager, "entityManager", entityManager);
    }


   /* @Test
    void createTemplate_ShouldSaveTemplate_WhenRequestIsValid() {
        // Arrange
        CreateTemplateRequest request = CreateTemplateRequest.builder()
                .name("Template1")
                .categoryId(1L)
                .imageUrl("image_url")
                .componentTypes(List.of(
                        ComponentTypeItemInTemplate.builder()
                                .componentTypeId(1L)
                                .orderOfImportance(1)
                                .build()))
                .build();

        Category category = mockCategory();
        ComponentTypeEntity componentTypeEntity = mockComponentTypeEntity(1L);

        when(categoryManager.findCategoryById(1L)).thenReturn(category);
        when(brandManager.getBrandById(2L)).thenReturn(brand);
        when(componentTypeRepository.existsById(1L)).thenReturn(true);
        when(templateRepository.existsTemplateEntityByNameAndBrandAndCategory("Template1", 2L, 1L)).thenReturn(false);
        when(entityManager.find(ComponentTypeEntity.class, 1L)).thenReturn(componentTypeEntity);

        // Act
        templateManager.createTemplate(request);

        // Assert
        verify(templateRepository, times(1)).save(any(TemplateEntity.class));
        verify(componentTypeListRepository, times(1)).save(any(ComponentTypeList_Template.class));
    }*/

    /*@Test
    void createTemplate_ShouldThrowException_WhenInputsAreInvalid() {
        // Arrange
        CreateTemplateRequest request = CreateTemplateRequest.builder()
                .name("Template1")
                .categoryId(1L)
                .brandId(2L)
                .imageUrl("image_url")
                .componentTypes(Collections.emptyList())
                .build();

        // Act & Assert
        assertThrows(InvalidParameterException.class, () -> templateManager.createTemplate(request));
    }*/

    @Test
    void deleteTemplate_ShouldDeleteTemplate_WhenTemplateExists() {
        // Arrange
        long templateId = 1L;
        when(templateRepository.existsById(templateId)).thenReturn(true);

        // Act
        templateManager.deleteTemplate(templateId);

        // Assert
        verify(templateRepository, times(1)).deleteById(templateId);
        verify(componentTypeListRepository, times(1)).deleteByTemplateId(templateId);
    }

    @Test
    void deleteTemplate_ShouldThrowException_WhenTemplateDoesNotExist() {
        // Arrange
        long templateId = 1L;
        when(templateRepository.existsById(templateId)).thenReturn(false);

        // Act & Assert
        assertThrows(ObjectNotFound.class, () -> templateManager.deleteTemplate(templateId));
    }

    @Test
    void getTemplateById_ShouldReturnTemplate_WhenTemplateExists() {
        // Arrange
        long templateId = 1L;
        TemplateEntity templateEntity = mockTemplateEntity(templateId,"name");
        when(templateRepository.existsById(templateId)).thenReturn(true);
        when(templateRepository.findTemplateEntityById(templateId)).thenReturn(templateEntity);
        when(templateRepository.findComponentTypeListByTemplateId(templateId)).thenReturn(Collections.emptyList());

        // Act
        var template = templateManager.getTemplateById(templateId);

        // Assert
        assertNotNull(template);
        verify(templateRepository, times(1)).findTemplateEntityById(templateId);
    }

    @Test
    void getTemplateById_ShouldThrowException_WhenTemplateDoesNotExist() {
        // Arrange
        long templateId = 1L;
        when(templateRepository.existsById(templateId)).thenReturn(false);

        // Act & Assert
        assertThrows(ObjectNotFound.class, () -> templateManager.getTemplateById(templateId));
    }

    @Test
    void getTemplatesByName_ShouldReturnTemplates_WhenTemplatesExist() {
        // Arrange
        String name = "Template1";
        TemplateEntity templateEntity = mockTemplateEntity(1L, name);
        when(templateRepository.findTemplateEntitiesByName(name)).thenReturn(List.of(templateEntity));
        when(templateRepository.findComponentTypeListByTemplateId(1L)).thenReturn(Collections.emptyList());

        // Act
        var templates = templateManager.getTemplatesByName(name);

        // Assert
        assertNotNull(templates);
        assertEquals(1, templates.size());
    }

    @Test
    void getTemplatesByName_ShouldThrowException_WhenTemplatesDoNotExist() {
        // Arrange
        String name = "Template1";
        when(templateRepository.findTemplateEntitiesByName(name)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(ObjectNotFound.class, () -> templateManager.getTemplatesByName(name));
    }

    //getTemplate()
    @Test
    void getTemplates_ShouldReturnAllTemplates() {
        // Arrange
        TemplateEntity templateEntity1 = mockTemplateEntity(1L, "Template1");
        TemplateEntity templateEntity2 = mockTemplateEntity(2L, "Template2");

        List<ComponentTypeList_Template> componentList1 = List.of(
                mockComponentTypeList_Template(1L, 1L, 1),
                mockComponentTypeList_Template(1L, 2L, 2)
        );
        List<ComponentTypeList_Template> componentList2 = List.of(
                mockComponentTypeList_Template(2L, 3L, 1)
        );

        // Mock the findAll and findComponentTypeListByTemplateId calls
        when(templateRepository.findAll()).thenReturn(List.of(templateEntity1, templateEntity2));
        when(templateRepository.findComponentTypeListByTemplateId(1L)).thenReturn(componentList1);
        when(templateRepository.findComponentTypeListByTemplateId(2L)).thenReturn(componentList2);

        // Act
        List<Template> templates = templateManager.getTemplates();

        // Assert
        assertNotNull(templates);
        assertEquals(2, templates.size());

        Template template1 = templates.get(0);
        Template template2 = templates.get(1);

        assertEquals("Template1", template1.getName());
        assertEquals(2, template1.getComponents().size());
        assertEquals("Template2", template2.getName());
        assertEquals(1, template2.getComponents().size());

        // Verify repository interactions
        verify(templateRepository, times(1)).findAll();
        verify(templateRepository, times(1)).findComponentTypeListByTemplateId(1L);
        verify(templateRepository, times(1)).findComponentTypeListByTemplateId(2L);
    }

    //update
    /*@Test
    void updateTemplate_ShouldUpdateTemplate_WhenTemplateExists() {
        // Arrange
        long templateId = 1L;
        UpdateTemplateRequest request = UpdateTemplateRequest.builder()
                .name("UpdatedTemplate")
                .categoryId(2L)
                .brandId(3L)
                .imageUrl("new_image_url")
                .componentTypes(List.of(
                        ComponentTypeItemInTemplate.builder()
                                .componentTypeId(1L) // Match this ID with your mock
                                .orderOfImportance(1)
                                .build()))
                .build();

        TemplateEntity existingTemplate = mockTemplateEntity(1L, "template");
        Category category = mockCategory();
        Brand brand = mockBrand();

        // Mocking category and brand existence
        when(templateRepository.findTemplateEntityById(templateId)).thenReturn(existingTemplate);
        when(categoryRepository.existsById(2L)).thenReturn(true);
        when(brandRepository.existsById(3L)).thenReturn(true);
        when(categoryManager.findCategoryById(2L)).thenReturn(category);
        when(brandManager.getBrandById(3L)).thenReturn(brand);

        // Mocking component type checks
        when(componentTypeRepository.existsById(1L)).thenReturn(true); // Match ID here
        when(componentTypeRepository.findComponentTypeEntityById(1L)).thenReturn(mockComponentTypeEntity(1L)); // Match ID here

        // Mocking template name uniqueness
        when(templateRepository.existsTemplateEntityForUpdate(templateId, "UpdatedTemplate", 3L, 2L)).thenReturn(false);

        // Act
        templateManager.updateTemplate(templateId, request);

        // Assert
        verify(templateRepository, times(1)).save(existingTemplate);
        verify(componentTypeListRepository, times(1)).deleteAll(anyList()); // Verify components deletion
        verify(componentTypeListRepository, times(1)).save(any(ComponentTypeList_Template.class)); // Verify components addition
    }*/

    // Helper methods
    private TemplateEntity mockTemplateEntity(long id, String name) {
        return TemplateEntity.builder()
                .id(id)
                .name(name)
                .category(mockCategoryEntity())
                .brand(mockBrandEntity())
                .imageURL("image_url")
                .build();
    }


    private CategoryEntity mockCategoryEntity() {
        return CategoryEntity.builder()
                .id(1L)
                .categoryName("Category1")
                .build();
    }

    private BrandEntity mockBrandEntity() {
        return BrandEntity.builder()
                .id(2L)
                .name("Brand1")
                .build();
    }

    private Category mockCategory() {
        return Category.builder()
                .categoryId(1L)
                .categoryName("Category1")
                .build();
    }

    private Brand mockBrand() {
        return Brand.builder()
                .id(2L)
                .name("Brand1")
                .build();
    }

    private ComponentTypeEntity mockComponentTypeEntity(long id) {
        return ComponentTypeEntity.builder()
                .id(id)
                .componentTypeName("ComponentType1")
                .build();
    }
    private ComponentTypeList_Template mockComponentTypeList_Template(long templateId, long componentTypeId, int orderOfImportance) {
        return ComponentTypeList_Template.builder()
                .template(mockTemplateEntity(templateId, "Template" + templateId)) // Ensure template has a valid category
                .componentType(ComponentTypeEntity.builder()
                        .id(componentTypeId)
                        .componentTypeName("ComponentType" + componentTypeId)
                        .category(mockCategoryEntity()) // Add category here
                        .build())
                .orderOfImportance(orderOfImportance)
                .build();
    }


}
