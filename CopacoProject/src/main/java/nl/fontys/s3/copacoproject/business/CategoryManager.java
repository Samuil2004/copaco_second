package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.domain.Category;
import nl.fontys.s3.copacoproject.persistence.entity.CategoryEntity;

import java.util.List;

public interface CategoryManager {
    List<Category> getAllCategories();
    Category findCategoryById(long id);
    CategoryEntity findCategoryByName(String categoryName);
}
