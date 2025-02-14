package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.auth.AuthRequest;
import nl.fontys.s3.copacoproject.business.dto.auth.AuthResponse;
import nl.fontys.s3.copacoproject.business.dto.user_dto.CreateUserRequest;
import nl.fontys.s3.copacoproject.business.dto.user_dto.CreateUserResponse;
import nl.fontys.s3.copacoproject.business.dto.user_dto.GetAllUsersResponse;
import nl.fontys.s3.copacoproject.domain.User;

import java.util.Optional;

public interface UserManager {
    GetAllUsersResponse getUsers();
    Optional<User> getUser(long id);
    CreateUserResponse createUser(CreateUserRequest request);
    void deleteUser(long id);

    AuthResponse login(AuthRequest request);
}
