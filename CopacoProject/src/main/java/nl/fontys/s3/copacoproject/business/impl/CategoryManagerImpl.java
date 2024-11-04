package nl.fontys.s3.copacoproject.business.impl;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.CategoryManager;
import nl.fontys.s3.copacoproject.business.converters.CategoryConverter;
import nl.fontys.s3.copacoproject.domain.Category;
import nl.fontys.s3.copacoproject.persistence.CategoryRepository;
import nl.fontys.s3.copacoproject.persistence.entity.CategoryEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryManagerImpl implements CategoryManager {
    private final CategoryRepository categoryRepository;
    @Override
    public Category findCategoryById(long id) {
        return CategoryConverter.convertFromEntityToBase(categoryRepository.findById(id).orElse(null));
    }
    @Override
    public CategoryEntity findCategoryByName(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName);
    }
}
