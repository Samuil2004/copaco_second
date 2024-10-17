package nl.fontys.s3.copacoproject.business.converters;

import nl.fontys.s3.copacoproject.domain.Category;
import nl.fontys.s3.copacoproject.persistence.entity.CategoryEntity;

final class CategoryConverter {
    public static Category convertFromBaseToEntity(CategoryEntity categoryEntity) {
        return Category.builder()
                .categoryId(categoryEntity.getId())
                .categoryName(categoryEntity.getCategoryName())
                .build();
    }

    public static CategoryEntity convertFromEntity(Category category) {
        return CategoryEntity.builder()
                .id(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .build();
    }
}
