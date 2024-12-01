package nl.fontys.s3.copacoproject.business.converters;

import nl.fontys.s3.copacoproject.domain.enums.Status;
import nl.fontys.s3.copacoproject.persistence.entity.StatusEntity;

public final class StatusConverter {
    private StatusConverter() {}

    public static StatusEntity convertFromBaseToEntity(Status status){
        return StatusEntity.builder()
                .id(status.getValue())
                .name(status.name())
                .build();
    }
}
