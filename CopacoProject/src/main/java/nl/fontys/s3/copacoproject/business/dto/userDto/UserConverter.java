package nl.fontys.s3.copacoproject.business.dto.userDto;

import nl.fontys.s3.copacoproject.business.AddressManager;
import nl.fontys.s3.copacoproject.domain.User;
import nl.fontys.s3.copacoproject.domain.enums.Role;
import nl.fontys.s3.copacoproject.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class UserConverter {
    static AddressManager addressManager;
    @Autowired
    private UserConverter(AddressManager addressManager) {
        UserConverter.addressManager = addressManager;

    }
    public static User convert (UserEntity userEntity){
        return User.builder()
                .userId(userEntity.getUserId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .role(Role.values()[userEntity.getRole()])
                .address(addressManager.getAddressById(userEntity.getAddress()).orElseThrow())
                .build();
    }

}

