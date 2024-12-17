package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.GetComponentResponse;
import nl.fontys.s3.copacoproject.business.dto.component.SimpleComponentResponse;
import nl.fontys.s3.copacoproject.domain.Component;

import java.util.List;


public interface ComponentManager {
    List<GetComponentResponse> getAllComponents();
    //GetComponentsByCategoryResponse getComponentsByCategory(String category);
    //Component GetComponentById(Long id);
    List<Component> getAllComponentsByCategory(long categoryId);
    List<Component> getAllComponentFromComponentType(Long componentTypeId);
    List<SimpleComponentResponse> getComponentsByComponentTypeAndConfigurationType(Long componentTypeId, String configurationType, int currentPage);
    Integer getComponentCountByComponentTypeAndConfigurationType(Long componentTypeId, String configurationTypeId);

}
