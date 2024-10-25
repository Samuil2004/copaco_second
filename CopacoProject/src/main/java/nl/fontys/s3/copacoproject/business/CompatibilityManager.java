package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.CreateAutomaticCompatibilityDtoRequest;
import nl.fontys.s3.copacoproject.business.dto.CreateAutomaticCompatibilityDtoResponse;
import nl.fontys.s3.copacoproject.business.dto.GetAutomaticCompatibilityByIdResponse;
import nl.fontys.s3.copacoproject.domain.AutomaticCompatibility;
import nl.fontys.s3.copacoproject.domain.CompatibilityType;
import nl.fontys.s3.copacoproject.persistence.entity.AutomaticCompatibilityEntity;

import java.util.List;

public interface CompatibilityManager {
    List<CompatibilityType> allCompatibilityTypes();
    CreateAutomaticCompatibilityDtoResponse createAutomaticCompatibility(CreateAutomaticCompatibilityDtoRequest createAutomaticCompatibilityDtoRequest);
    GetAutomaticCompatibilityByIdResponse allAutomaticCompatibilities(Long automaticCompatibilityId);
}
