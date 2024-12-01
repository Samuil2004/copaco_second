package nl.fontys.s3.copacoproject.business.impl;

import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.domain.Brand;
import nl.fontys.s3.copacoproject.persistence.BrandRepository;
import nl.fontys.s3.copacoproject.persistence.entity.BrandEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BrandManagerImplTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandManagerImpl brandManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBrandById_ShouldReturnBrand_WhenIdIsValid() {
        long id = 1L;
        BrandEntity brandEntity = BrandEntity.builder()
                .id(id)
                .name("Test Brand")
                .build();

        when(brandRepository.existsById(id)).thenReturn(true);
        when(brandRepository.findBrandById(id)).thenReturn(brandEntity);

        Brand result = brandManager.getBrandById(id);

        assertNotNull(result);
        assertEquals("Test Brand", result.getName());
        verify(brandRepository, times(1)).existsById(id);
        verify(brandRepository, times(1)).findBrandById(id);
    }

    @Test
    void getBrandById_ShouldThrowException_WhenIdIsInvalid() {
        long id = 99L;
        when(brandRepository.existsById(id)).thenReturn(false);

        assertThrows(ObjectNotFound.class, () -> brandManager.getBrandById(id));
        verify(brandRepository, times(1)).existsById(id);
        verify(brandRepository, never()).findBrandById(anyLong());
    }

    @Test
    void getAllBrands_ShouldReturnListOfBrands_WhenBrandsExist() {
        BrandEntity brandEntity1 = BrandEntity.builder()
                .id(1L)
                .name("Brand A")
                .build();
        BrandEntity brandEntity2 = BrandEntity.builder()
                .id(2L)
                .name("Brand B")
                .build();

        when(brandRepository.findAll()).thenReturn(List.of(brandEntity1, brandEntity2));

        List<Brand> result = brandManager.getAllBrands();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Brand A", result.get(0).getName());
        assertEquals("Brand B", result.get(1).getName());
        verify(brandRepository, times(1)).findAll();
    }

    @Test
    void getAllBrands_ShouldReturnEmptyList_WhenNoBrandsExist() {
        when(brandRepository.findAll()).thenReturn(Collections.emptyList());

        List<Brand> result = brandManager.getAllBrands();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(brandRepository, times(1)).findAll();
    }
}
