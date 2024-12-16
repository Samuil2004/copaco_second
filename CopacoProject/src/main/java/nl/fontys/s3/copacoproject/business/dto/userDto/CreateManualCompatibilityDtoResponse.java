package nl.fontys.s3.copacoproject.business.dto.userDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nl.fontys.s3.copacoproject.persistence.entity.CompatibilityEntity;

@Builder
@Getter
@Setter
public class CreateManualCompatibilityDtoResponse {
    private CompatibilityEntity automaticCompatibility;
}
