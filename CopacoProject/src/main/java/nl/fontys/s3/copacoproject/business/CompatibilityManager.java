package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.CreateAutomaticCompatibilityDtoRequest;
import nl.fontys.s3.copacoproject.business.dto.CreateAutomaticCompatibilityDtoResponse;
import nl.fontys.s3.copacoproject.domain.CompatibilityType;

import java.util.List;

public interface CompatibilityManager {
    List<CompatibilityType> allCompatibilityTypes();
    CreateAutomaticCompatibilityDtoResponse createAutomaticCompatibility(CreateAutomaticCompatibilityDtoRequest createAutomaticCompatibilityDtoRequest);
}
