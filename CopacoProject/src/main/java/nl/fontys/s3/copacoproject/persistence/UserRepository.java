package nl.fontys.s3.copacoproject.persistence;
import nl.fontys.s3.copacoproject.persistence.entity.UserEntity;

import java.util.List;
import java.util.Optional;


public interface UserRepository {
    List<UserEntity> findAll();
    Optional<UserEntity> findById(Long id);
    UserEntity save(UserEntity user);
    void deleteById(Long id);
}
