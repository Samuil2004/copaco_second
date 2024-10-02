package nl.fontys.s3.copacoproject.persistence.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserEntity {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private int role;
    private Long address;
}
