package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.domain.CompatibilityType;

import java.util.List;

public interface CompatibilityManager {
    List<CompatibilityType> allCompatibilityTypes();
}
