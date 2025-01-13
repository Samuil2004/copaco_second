package nl.fontys.s3.copacoproject.controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.UserManager;
import nl.fontys.s3.copacoproject.business.dto.user_dto.CreateUserRequest;
import nl.fontys.s3.copacoproject.business.dto.user_dto.CreateUserResponse;
import nl.fontys.s3.copacoproject.business.dto.user_dto.GetAllUsersResponse;
import nl.fontys.s3.copacoproject.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserManager userManager;

    @GetMapping("/{id}")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userManager.getUser(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<GetAllUsersResponse> getAllUsers() {
        GetAllUsersResponse response = userManager.getUsers();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping()
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Validated CreateUserRequest user) {
        //CreateNewAddressResponse savedAddress = addressManager.createNewAddress(user.getAddress());
        CreateUserResponse userResponse = userManager.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

   @DeleteMapping("/{id}")
   @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userManager.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
