package nl.fontys.s3.copacoproject.business.impl;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.CategoryManager;
import nl.fontys.s3.copacoproject.persistence.CategoryRepository;
import nl.fontys.s3.copacoproject.persistence.entity.CategoryEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryManagerImpl implements CategoryManager {
    private final CategoryRepository categoryRepository;
    @Override
    public CategoryEntity findCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }
    @Override
    public CategoryEntity findCategoryByName(String categoryName) {
        return categoryRepository.findByName(categoryName);
    }
}
