package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.GetAutomaticCompatibilityResponse;
import nl.fontys.s3.copacoproject.business.dto.ConfiguratorRequest;

import java.util.List;

public interface CompatibilityBetweenComponents {
    List<GetAutomaticCompatibilityResponse> automaticCompatibility(ConfiguratorRequest request);
}
