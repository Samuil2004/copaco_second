package nl.fontys.s3.copacoproject.business.converters;

import nl.fontys.s3.copacoproject.domain.User;
import nl.fontys.s3.copacoproject.domain.enums.Role;
import nl.fontys.s3.copacoproject.persistence.entity.AddressEntity;
import nl.fontys.s3.copacoproject.persistence.entity.RoleEntity;
import nl.fontys.s3.copacoproject.persistence.entity.UserEntity;

public final class UserConverter {
    public static User convertFromEntityToBase(UserEntity userEntity, AddressEntity addressEntity) {
        return User.builder()
                .userId(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .role(Role.valueOf(userEntity.getRole().getRoleName()))
                .email(userEntity.getEmail())
                .address(AddressConverter.convertFromEntityToBase(addressEntity))
                .build();
    }

    public static UserEntity convertFromBaseToEntity(User user) {
        return UserEntity.builder()
                .id(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(RoleEntity.builder()
                        .roleName(user.getRole().toString()).build())
                .email(user.getEmail())
                .address(AddressConverter.convertFromBaseToEntity(user.getAddress()))
                .build();
    }
}
