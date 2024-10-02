package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.GetAllComponentsResponse;
import nl.fontys.s3.copacoproject.business.dto.GetComponentsByCategoryResponse;
import nl.fontys.s3.copacoproject.domain.Component;

import java.util.List;

public interface ComponentManager {
    GetAllComponentsResponse getAllComponents();
    GetComponentsByCategoryResponse getComponentsByCategory(String category);
}
