package nl.fontys.s3.copacoproject.business.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nl.fontys.s3.copacoproject.persistence.entity.AutomaticCompatibilityEntity;

@Builder
@Getter
@Setter
public class CreateAutomaticCompatibilityDtoResponse {
    private AutomaticCompatibilityEntity automaticCompatibility;
}
