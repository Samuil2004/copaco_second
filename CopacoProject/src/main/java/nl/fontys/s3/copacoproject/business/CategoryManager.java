package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.persistence.entity.CategoryEntity;

public interface CategoryManager {
    CategoryEntity findCategoryById(Long id);
    CategoryEntity findCategoryByName(String categoryName);
}
