package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.CustomProductEntity;
import nl.fontys.s3.copacoproject.persistence.entity.StatusEntity;
import nl.fontys.s3.copacoproject.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface CustomProductRepository extends JpaRepository<CustomProductEntity, Long> {
    CustomProductEntity findById(long id);

    Page<CustomProductEntity> findCustomProductEntitiesByStatusAndUserId(StatusEntity status, UserEntity userId, Pageable pageable);

    int countCustomProductEntitiesByStatusAndUserId(StatusEntity status, UserEntity userId);

    @Query("""
    SELECT COUNT(*) AS itemsInConfigType
    FROM CustomProductEntity cp
    JOIN TemplateEntity t ON cp.template = t
    WHERE t.category.id = :categoryId
    AND cp.status = :status
    """)
    int countCustomProductEntitiesByStatusAndCategoryId(Long categoryId, StatusEntity status);

    @Query("""
    SELECT COUNT(*)
    FROM CustomProductEntity cp
    JOIN TemplateEntity t ON cp.template = t
    WHERE t.configurationType= :configurationType
    AND cp.status = :status
    """)
    int countCustomProductEntitiesByConfigurationTypeAndStatus(String configurationType, StatusEntity status);

    @Query("""
    SELECT COALESCE(SUM(c.componentPrice),0)
    FROM CustomProductEntity cp
    JOIN AssemblingEntity a ON cp = a.customProductId
    JOIN ComponentEntity c ON c=a.componentId
    WHERE cp.status.id=1
    """)
    double sumTotalIncome();

    @Query("""
    SELECT COALESCE(SUM(c.componentPrice),0)
    FROM CustomProductEntity cp
    JOIN AssemblingEntity a ON cp = a.customProductId
    JOIN ComponentEntity c ON c=a.componentId
    WHERE cp.status.id=1 AND cp.template.configurationType= :configurationType
    """)
    double sumIncomeByConfigurationType(String configurationType);

    @Query("""
    SELECT COALESCE(AVG(c.componentPrice),0)
    FROM CustomProductEntity cp
    JOIN AssemblingEntity a ON cp = a.customProductId
    JOIN ComponentEntity c ON c=a.componentId
    WHERE cp.status.id=1
    """)
    double calculateAverageFinishedProductPrice();
}
