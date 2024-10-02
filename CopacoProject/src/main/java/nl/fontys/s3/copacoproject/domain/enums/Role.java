package nl.fontys.s3.copacoproject.domain.enums;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN(0),
    CUSTOMER(1);

    private final int value;

    Role(int value) {
        this.value = value;
    }

    public static Role fromValue(int value) {
        for (Role role : values()) {
            if (role.value == value) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role value: " + value);
    }

}