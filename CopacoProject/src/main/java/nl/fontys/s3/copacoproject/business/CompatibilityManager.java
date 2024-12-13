package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.CreateAutomaticCompatibilityDtoRequest;
import nl.fontys.s3.copacoproject.business.dto.CreateAutomaticCompatibilityDtoResponse;
import nl.fontys.s3.copacoproject.business.dto.CreateManualCompatibilityDtoRequest;
import nl.fontys.s3.copacoproject.business.dto.GetAutomaticCompatibilityByIdResponse;
import nl.fontys.s3.copacoproject.business.dto.userDto.CreateManualCompatibilityDtoResponse;
import nl.fontys.s3.copacoproject.domain.CompatibilityType;

import java.util.List;

public interface CompatibilityManager {
    //This method returns all compatibility types such as Manual and Automatic
    List<CompatibilityType> allCompatibilityTypes();
    //This method create a new automatic compatibility between two component types
    CreateAutomaticCompatibilityDtoResponse createAutomaticCompatibility(CreateAutomaticCompatibilityDtoRequest createAutomaticCompatibilityDtoRequest);
    CreateManualCompatibilityDtoResponse createManualCompatibility(CreateManualCompatibilityDtoRequest createManualCompatibilityDtoRequest);

    //This method returns an automatic compatibility by a given id of compatibility -> it should also work with rule id
    GetAutomaticCompatibilityByIdResponse automaticCompatibilityByCompatibilityId(Long automaticCompatibilityId);
    //This method returns a list of all compatibilities a component type has (with the other component types)
    List<GetAutomaticCompatibilityByIdResponse> allCompatibilitiesForComponentTypeByComponentTypeId(Long componentTypeId);
}
