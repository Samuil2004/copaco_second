package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.GetAutomaticCompatibilityResponse;
import nl.fontys.s3.copacoproject.business.dto.GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest;
import nl.fontys.s3.copacoproject.domain.Component;

import java.util.List;

public interface CompatibilityBetweenComponents {
    List<GetAutomaticCompatibilityResponse> automaticCompatibility(GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest request);
}
