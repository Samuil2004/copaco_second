package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.domain.Component;

import java.util.List;

public interface CompatibilityBetweenComponents {
    List<Component> automaticCompatibility(Long componentId,Long searchedComponentsType);
}
