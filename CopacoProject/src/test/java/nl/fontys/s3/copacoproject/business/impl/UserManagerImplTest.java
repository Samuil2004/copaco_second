package nl.fontys.s3.copacoproject.business.impl;

import nl.fontys.s3.copacoproject.business.Exceptions.InvalidCredentialsException;
import nl.fontys.s3.copacoproject.business.dto.auth.AuthRequest;
import nl.fontys.s3.copacoproject.business.dto.auth.AuthResponse;
import nl.fontys.s3.copacoproject.business.dto.userDto.CreateUserRequest;
import nl.fontys.s3.copacoproject.business.dto.userDto.CreateUserResponse;
import nl.fontys.s3.copacoproject.configuration.security.token.AccessTokenEncoder;
import nl.fontys.s3.copacoproject.configuration.security.token.impl.AccessTokenImpl;
import nl.fontys.s3.copacoproject.domain.User;
import nl.fontys.s3.copacoproject.persistence.UserRepository;
import nl.fontys.s3.copacoproject.persistence.entity.AddressEntity;
import nl.fontys.s3.copacoproject.persistence.entity.RoleEntity;
import nl.fontys.s3.copacoproject.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserManagerImplTest {

    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private PasswordEncoder mockPasswordEncoder;
    @Mock
    private AccessTokenEncoder mockAccessTokenEncoder;

    private UserManagerImpl userManagerImplUnderTest;

    @BeforeEach
    void setUp() {

        // Initialize UserManagerImpl with UserConverter
        userManagerImplUnderTest = new UserManagerImpl(
                mockUserRepository,
                mockPasswordEncoder,
                mockAccessTokenEncoder
        );
    }

//    @Test
//    void testGetUser() {
//        // Mock UserEntity returned by the repository
//        UserEntity userEntity = UserEntity.builder()
//                .id(1L)
//                .firstName("John")
//                .lastName("Doe")
//                .email("john.doe@example.com")
//                .role(RoleEntity.builder()
//                        .id(0L)
//                        .roleName("ADMIN")
//                        .build())
//                .address(AddressEntity.builder()
//                        .id(100L)
//                        .build())
//                .build();
//        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(userEntity));
//
//        // Mock AddressEntity (if needed in the converter)
//        AddressEntity addressEntity = AddressEntity.builder().id(100L).build();
//
//        // Mock User conversion
//        User mockUser = User.builder()
//                .userId(1L)
//                .firstName("John")
//                .lastName("Doe")
//                .email("john.doe@example.com")
//                .role(Role.ADMIN)
//                .build();
//
//        // Stub the converter method
//        when(mockUserConverter.convertFromEntityToBase(userEntity, addressEntity)).thenReturn(mockUser);
//
//        // Act
//        Optional<User> result = userManagerImplUnderTest.getUser(1L);
//
//        // Assert
//        assertThat(result).isPresent();
//        assertThat(result.get().getFirstName()).isEqualTo("John");
//        assertThat(result.get().getLastName()).isEqualTo("Doe");
//        assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");
//
//        // Verify interactions
//        verify(mockUserRepository).findById(1L);
//        verify(mockUserConverter).convertFromEntityToBase(userEntity, addressEntity);
//    }


    @Test
    void testGetUser_UserRepositoryReturnsAbsent() {
        // Setup
        when(mockUserRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        final Optional<User> result = userManagerImplUnderTest.getUser(0L);

        // Verify the results
        assertThat(result).isEmpty();
    }

    @Test
    void testCreateUser() {
        // Setup
        final CreateUserRequest request = CreateUserRequest.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .password("password")
                .role("ADMIN")
                .build();

        when(mockPasswordEncoder.encode("password")).thenReturn("encodedPassword");

        // Mock save behavior
        when(mockUserRepository.save(argThat(user ->
                user.getFirstName().equals("firstName") &&
                        user.getLastName().equals("lastName") &&
                        user.getEmail().equals("email") &&
                        user.getPassword().equals("encodedPassword") &&
                        user.getRole().getRoleName().equals("ADMIN")
        ))).thenReturn(UserEntity.builder()
                .id(1L) // Simulate saved user with assigned ID
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .password("encodedPassword")
                .role(RoleEntity.builder().id(0L).roleName("ADMIN").build())
                .build());

        // Run the test
        final CreateUserResponse result = userManagerImplUnderTest.createUser(request);

        // Verify the results
        assertThat(result.getUserId()).isEqualTo(1L); // Verify returned ID is as expected
        verify(mockUserRepository).save(any(UserEntity.class));
        verify(mockPasswordEncoder).encode("password");
    }


    @Test
    void testDeleteUser() {
        // Setup
        // Run the test
        userManagerImplUnderTest.deleteUser(0L);

        // Verify the results
        verify(mockUserRepository).deleteById(0L);
    }

    @Test
    void testLogin() {
        // Setup
        final AuthRequest request = new AuthRequest("email", "rawPassword");
        final AuthResponse expectedResult = AuthResponse.builder()
                .accessToken("accessToken")
                .build();

        // Configure UserRepository.findUserEntityByEmail(...).
        final UserEntity userEntity = UserEntity.builder()
                .id(0L)
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .password("encodedPassword")
                .role(RoleEntity.builder()
                        .id(0L)
                        .roleName("ADMIN")
                        .build())
                .address(AddressEntity.builder()
                        .id(0L)
                        .build())
                .build();
        when(mockUserRepository.findUserEntityByEmail("email")).thenReturn(userEntity);

        when(mockPasswordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(mockAccessTokenEncoder.encode(any(AccessTokenImpl.class))).thenReturn("accessToken");

        // Run the test
        final AuthResponse result = userManagerImplUnderTest.login(request);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testLogin_UserRepositoryReturnsNull() {
        // Setup
        final AuthRequest request = new AuthRequest("email", "rawPassword");
        when(mockUserRepository.findUserEntityByEmail("email")).thenReturn(null);

        // Run the test
        assertThatThrownBy(() -> userManagerImplUnderTest.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void testLogin_PasswordEncoderReturnsFalse() {
        // Setup
        final AuthRequest request = new AuthRequest("email", "rawPassword");

        // Configure UserRepository.findUserEntityByEmail(...).
        final UserEntity userEntity = UserEntity.builder()
                .id(0L)
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .password("encodedPassword")
                .role(RoleEntity.builder()
                        .id(0L)
                        .roleName("roleName")
                        .build())
                .address(AddressEntity.builder()
                        .id(0L)
                        .build())
                .build();
        when(mockUserRepository.findUserEntityByEmail("email")).thenReturn(userEntity);

        when(mockPasswordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> userManagerImplUnderTest.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
