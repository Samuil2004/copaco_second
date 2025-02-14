package nl.fontys.s3.copacoproject.business.impl;


import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.exception.InvalidCredentialsException;
import nl.fontys.s3.copacoproject.business.UserManager;
import nl.fontys.s3.copacoproject.business.dto.auth.AuthRequest;
import nl.fontys.s3.copacoproject.business.dto.auth.AuthResponse;
import nl.fontys.s3.copacoproject.business.dto.user_dto.*;
import nl.fontys.s3.copacoproject.configuration.security.token.AccessTokenEncoder;
import nl.fontys.s3.copacoproject.configuration.security.token.impl.AccessTokenImpl;
import nl.fontys.s3.copacoproject.domain.User;
import nl.fontys.s3.copacoproject.domain.enums.Role;
import nl.fontys.s3.copacoproject.persistence.UserRepository;
import nl.fontys.s3.copacoproject.persistence.entity.RoleEntity;
import nl.fontys.s3.copacoproject.persistence.entity.UserEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserManagerImpl implements UserManager {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final AccessTokenEncoder accessTokenEncoder;

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
    public CreateUserResponse createUser(CreateUserRequest request) {

        UserEntity savedUser = saveNewUser(request);

        return CreateUserResponse.builder().userId(savedUser.getId()).build();
    }
    private UserEntity saveNewUser(CreateUserRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Role role = Role.valueOf(request.getRole());

        UserEntity newUser = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(encodedPassword)
                .role(RoleEntity.builder().roleName("CUSTOMER").id(4).build())
//                .role()
                .build();
        return userRepository.save(newUser);
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        UserEntity user = userRepository.findUserEntityByEmail(request.getEmail());
        if (user == null) {
            throw new InvalidCredentialsException();
        }

        if (!matchesPassword(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = generateAccessToken(user);
        return AuthResponse.builder().accessToken(accessToken).build();
    }

    private boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    private String generateAccessToken(UserEntity user) {
        Long userId = (user.getId() != null) ? user.getId() : null;

        List<String> roles = new ArrayList<>();
        roles.add(user.getRole().getRoleName());

        return accessTokenEncoder.encode(
                new AccessTokenImpl(user.getEmail(), userId, roles));
    }
}
