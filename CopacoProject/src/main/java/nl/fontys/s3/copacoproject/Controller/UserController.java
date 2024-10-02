package nl.fontys.s3.copacoproject.Controller;

import nl.fontys.s3.copacoproject.business.AddressManager;
import nl.fontys.s3.copacoproject.business.UserManager;
import nl.fontys.s3.copacoproject.business.dto.addressDto.CreateNewAddressResponse;
import nl.fontys.s3.copacoproject.business.dto.userDto.CreateUserRequest;
import nl.fontys.s3.copacoproject.business.dto.userDto.CreateUserResponse;
import nl.fontys.s3.copacoproject.business.dto.userDto.GetAllUsersResponse;
import nl.fontys.s3.copacoproject.business.dto.userDto.UpdateUserRequest;
import nl.fontys.s3.copacoproject.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserManager userManager;
    private final AddressManager addressManager;
    @Autowired
    public UserController(UserManager userManager, AddressManager addressManager) {
        this.userManager = userManager;
        this.addressManager = addressManager;

    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userManager.getUser(id);
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping
    public ResponseEntity<GetAllUsersResponse> getAllUsers() {
        GetAllUsersResponse response = userManager.getUsers();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Validated CreateUserRequest user) {
        CreateNewAddressResponse savedAddress = addressManager.createNewAddress(user.getAddress());
        CreateUserResponse userResponse = userManager.createUser(user, savedAddress.getAddressId());
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
   @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userManager.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable long id,
                                           @RequestBody @Validated UpdateUserRequest user) throws Exception {
        user.setUserId(id);
        userManager.updateUser(user);
        return ResponseEntity.noContent().build();
    }



}
