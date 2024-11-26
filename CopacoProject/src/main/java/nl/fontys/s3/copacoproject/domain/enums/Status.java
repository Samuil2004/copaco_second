package nl.fontys.s3.copacoproject.domain.enums;

import lombok.Getter;

@Getter
public enum Status {
    DRAFT(1),
    FINISHED(2);


    private final int value;

    Status(int value) {
        this.value = value;
    }

    public static Status fromValue(int value) {
        for (Status status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status value: " + value);
    }
}
