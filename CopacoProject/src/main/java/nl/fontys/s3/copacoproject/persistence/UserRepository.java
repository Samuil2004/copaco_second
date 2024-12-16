package nl.fontys.s3.copacoproject.persistence;
import nl.fontys.s3.copacoproject.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findAll();
    Optional<UserEntity> findById(Long id);
    UserEntity findUserEntityById(Long id);
    //UserEntity save(UserEntity user);
    void deleteById(Long id);
    UserEntity findUserEntityByEmail(String email);
}
