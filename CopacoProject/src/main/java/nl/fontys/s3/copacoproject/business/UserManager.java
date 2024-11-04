package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.userDto.CreateUserRequest;
import nl.fontys.s3.copacoproject.business.dto.userDto.CreateUserResponse;
import nl.fontys.s3.copacoproject.business.dto.userDto.GetAllUsersResponse;
import nl.fontys.s3.copacoproject.business.dto.userDto.UpdateUserRequest;
import nl.fontys.s3.copacoproject.domain.User;

import java.util.Optional;

public interface UserManager {
    GetAllUsersResponse getUsers();
    Optional<User> getUser(long id);
    //CreateUserResponse createUser(CreateUserRequest request,Long AddressId);
    //void updateUser(UpdateUserRequest request) throws Exception;
    void deleteUser(long id);
}
