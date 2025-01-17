    package nl.fontys.s3.copacoproject.business.impl;

    import lombok.RequiredArgsConstructor;
    import nl.fontys.s3.copacoproject.business.CategoryManager;
    import nl.fontys.s3.copacoproject.business.exception.ObjectNotFound;
    import nl.fontys.s3.copacoproject.business.converters.CategoryConverter;
    import nl.fontys.s3.copacoproject.domain.Category;
    import nl.fontys.s3.copacoproject.persistence.CategoryRepository;
    import nl.fontys.s3.copacoproject.persistence.entity.CategoryEntity;
    import org.springframework.stereotype.Service;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Objects;

    @Service
    @RequiredArgsConstructor
    public class CategoryManagerImpl implements CategoryManager {
        private final CategoryRepository categoryRepository;


        @Override
        public List<Category> getAllCategories() {
            List<CategoryEntity> categoryEntities = categoryRepository.findAll();
            List<Category> categories = new ArrayList<>();
            if(!categoryEntities.isEmpty()){
                for(CategoryEntity categoryEntity : categoryEntities) {
                    categories.add(CategoryConverter.convertFromEntityToBase(categoryEntity));
                }
            }
            return categories;
        }

        @Override
        public Category findCategoryById(long id) {
            if(!categoryRepository.existsById(id)) {
                throw new ObjectNotFound("There is no category with this id");
            }
            return CategoryConverter.convertFromEntityToBase(Objects.requireNonNull(categoryRepository.findById(id).orElse(null)));
        }
        @Override
        public CategoryEntity findCategoryByName(String categoryName) {
            return categoryRepository.findByCategoryName(categoryName);
        }
    }
