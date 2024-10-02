package nl.fontys.s3.copacoproject.business.dto.userDto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nl.fontys.s3.copacoproject.domain.Address;
import nl.fontys.s3.copacoproject.domain.enums.Role;
@Builder
@Getter
@Setter
public class UpdateUserRequest {
    private Long userId;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private Role role;
    @NotNull
    private Address address;
}
