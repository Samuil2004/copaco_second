package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.CustomProductEntity;
import nl.fontys.s3.copacoproject.persistence.entity.StatusEntity;
import nl.fontys.s3.copacoproject.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomProductRepository extends JpaRepository<CustomProductEntity, Long> {
    CustomProductEntity findById(long id);
    List<CustomProductEntity> findCustomProductEntitiesByStatusAndUserId(StatusEntity status, UserEntity userId);

    Page<CustomProductEntity> findCustomProductEntitiesByStatusAndUserId(StatusEntity status, UserEntity userId, Pageable pageable);

    int countCustomProductEntitiesByStatusAndUserId(StatusEntity status, UserEntity userId);
}
