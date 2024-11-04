package nl.fontys.s3.copacoproject.business.dto.userDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nl.fontys.s3.copacoproject.domain.User;

import java.util.List;
@Builder
@Getter
@Setter
public class GetAllUsersResponse {
    private List<User> users;
}
