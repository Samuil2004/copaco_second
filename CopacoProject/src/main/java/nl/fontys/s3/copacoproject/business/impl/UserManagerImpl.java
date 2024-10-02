package nl.fontys.s3.copacoproject.business.impl;


import nl.fontys.s3.copacoproject.business.UserManager;
import nl.fontys.s3.copacoproject.business.dto.userDto.*;
import nl.fontys.s3.copacoproject.domain.User;
import nl.fontys.s3.copacoproject.persistence.UserRepository;
import nl.fontys.s3.copacoproject.persistence.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserManagerImpl implements UserManager {
    private final UserRepository userRepository;
    private final UserConverter userConverter;


    public UserManagerImpl(UserRepository userRepo,UserConverter userConverter) {
        this.userConverter = userConverter;
        this.userRepository = userRepo;
    }
    @Override
    public GetAllUsersResponse getUsers() {
        List<UserEntity> result = userRepository.findAll();
        List<User> users = result.stream().map(UserConverter::convert).toList();
        return GetAllUsersResponse.builder().users(users).build();
    }

    @Override
    public Optional<User> getUser(long id) {
        return userRepository.findById(id).map(UserConverter::convert);
    }
    @Override
    public CreateUserResponse createUser(CreateUserRequest request,Long addressId) {

        UserEntity savedUser = saveNewUser(request,addressId);

        return CreateUserResponse.builder().userId(savedUser.getUserId()).build();

    }
    private UserEntity saveNewUser(CreateUserRequest request,Long addressId) {
        UserEntity newUser = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole().getValue())
                .address(addressId)
                .build();
        return userRepository.save(newUser);
    }
    @Override
    public void updateUser(UpdateUserRequest request) throws Exception {
        Optional<UserEntity> userEntityOptional = userRepository.findById(request.getUserId());
        if (userEntityOptional.isEmpty()) {
            throw new Exception("User not found");
        }
        UserEntity userEntity = userEntityOptional.get();
        updateFields(request, userEntity);
    }
    private void updateFields(UpdateUserRequest request, UserEntity userEntity) {
        userEntity.setFirstName(request.getFirstName());
        userEntity.setLastName(request.getLastName());
        userEntity.setEmail(request.getEmail());
        userEntity.setPassword(request.getPassword());
        userEntity.setRole(Integer.parseInt(request.getRole().toString()));
        userRepository.save(userEntity);

    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }
}
