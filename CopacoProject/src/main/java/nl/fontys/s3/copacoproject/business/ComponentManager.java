package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.GetAllComponentsResponse;
import nl.fontys.s3.copacoproject.business.dto.GetComponentsByCategoryResponse;
import nl.fontys.s3.copacoproject.domain.Component;


public interface ComponentManager {
    GetAllComponentsResponse getAllComponents();
    //GetComponentsByCategoryResponse getComponentsByCategory(String category);
    Component GetComponentById(Long id);
}
