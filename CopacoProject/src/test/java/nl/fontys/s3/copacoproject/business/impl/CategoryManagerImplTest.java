package nl.fontys.s3.copacoproject.business.impl;

import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.domain.Category;
import nl.fontys.s3.copacoproject.persistence.CategoryRepository;
import nl.fontys.s3.copacoproject.persistence.entity.CategoryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryManagerImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryManagerImpl categoryManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategories_WhenCategoriesExist() {
        CategoryEntity categoryEntity1 = CategoryEntity.builder()
                .id(1L)
                .categoryName("Category A")
                .build();
        CategoryEntity categoryEntity2 = CategoryEntity.builder()
                .id(2L)
                .categoryName("Category B")
                .build();

        when(categoryRepository.findAll()).thenReturn(List.of(categoryEntity1, categoryEntity2));

        List<Category> result = categoryManager.getAllCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Category A", result.get(0).getCategoryName());
        assertEquals("Category B", result.get(1).getCategoryName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getAllCategories_ShouldReturnEmptyList_WhenNoCategoriesExist() {
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        List<Category> result = categoryManager.getAllCategories();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void findCategoryById_ShouldReturnCategory_WhenIdIsValid() {
        long id = 1L;
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .id(id)
                .categoryName("Test Category")
                .build();

        when(categoryRepository.existsById(id)).thenReturn(true);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(categoryEntity));

        Category result = categoryManager.findCategoryById(id);

        assertNotNull(result);
        assertEquals("Test Category", result.getCategoryName());
        assertEquals(1L, result.getCategoryId());
        verify(categoryRepository, times(1)).existsById(id);
        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    void findCategoryById_ShouldThrowException_WhenIdIsInvalid() {
        long id = 99L;
        when(categoryRepository.existsById(id)).thenReturn(false);

        assertThrows(ObjectNotFound.class, () -> categoryManager.findCategoryById(id),
                "Expected ObjectNotFound exception when the category ID is invalid");

        verify(categoryRepository, times(1)).existsById(id);
        verify(categoryRepository, never()).findById(anyLong());
    }

    @Test
    void findCategoryByName_ShouldReturnCategoryEntity_WhenNameIsValid() {
        String categoryName = "Valid Category";
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .id(1L)
                .categoryName(categoryName)
                .build();

        when(categoryRepository.findByCategoryName(categoryName)).thenReturn(categoryEntity);

        CategoryEntity result = categoryManager.findCategoryByName(categoryName);

        assertNotNull(result);
        assertEquals(categoryName, result.getCategoryName());
        assertEquals(1L, result.getId());
        verify(categoryRepository, times(1)).findByCategoryName(categoryName);
    }

    @Test
    void findCategoryByName_ShouldReturnNull_WhenNameIsInvalid() {
        String categoryName = "Invalid Category";
        when(categoryRepository.findByCategoryName(categoryName)).thenReturn(null);

        CategoryEntity result = categoryManager.findCategoryByName(categoryName);

        assertNull(result);
        verify(categoryRepository, times(1)).findByCategoryName(categoryName);
    }
}
