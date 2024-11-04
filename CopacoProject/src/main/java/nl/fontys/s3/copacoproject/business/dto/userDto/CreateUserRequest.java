package nl.fontys.s3.copacoproject.business.dto.userDto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import nl.fontys.s3.copacoproject.business.dto.addressDto.CreateNewAddressRequest;
import nl.fontys.s3.copacoproject.domain.enums.Role;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
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
    private CreateNewAddressRequest address;
}
