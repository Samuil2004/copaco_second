//package nl.fontys.s3.copacoproject.persistence.impl;
//
//import nl.fontys.s3.copacoproject.persistence.UserRepository;
//import nl.fontys.s3.copacoproject.persistence.entity.UserEntity;
//import org.springframework.stereotype.Repository;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public class FakeUserRepositoryImpl implements UserRepository {
//    private static long NEXT_ID = 1;
//    private final List<UserEntity> savedUsers;
//    public FakeUserRepositoryImpl() {
//        savedUsers = new ArrayList<>();
//    }
//
//    @Override
//    public List<UserEntity> findAll(){
//        return Collections.unmodifiableList(this.savedUsers);
//    }
//
//    @Override
//    public Optional<UserEntity> findById(Long id){
//        return this.savedUsers
//                .stream()
//                .filter(userEntity -> userEntity.getId() == id)
//                .findFirst();
//    }
//    @Override
//    public UserEntity save(UserEntity user) {
//        if (user != null) {
//            user.setId(NEXT_ID++);
//            savedUsers.add(user);
//            return user;
//        } else {
//            return null;
//        }
//    }
//    @Override
//    public void deleteById(Long id) {
//        this.savedUsers.removeIf(userEntity -> userEntity.getId() == id);
//    }
//
//}
