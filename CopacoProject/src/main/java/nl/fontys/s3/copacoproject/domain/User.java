package nl.fontys.s3.copacoproject.domain;

import lombok.Builder;

@Builder
public class User
{
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private Address address;
}
