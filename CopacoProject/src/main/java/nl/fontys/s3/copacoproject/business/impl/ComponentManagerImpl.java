package nl.fontys.s3.copacoproject.business.impl;

import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.ComponentManager;
import nl.fontys.s3.copacoproject.business.dto.GetAllComponentsResponse;
import nl.fontys.s3.copacoproject.business.dto.GetComponentsByCategoryResponse;
import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ComponentManagerImpl implements ComponentManager {

    private final ComponentRepository componentRepository;

    @Override
    public GetAllComponentsResponse getAllComponents() {
        return GetAllComponentsResponse.builder()
                .allComponents(componentRepository.getAllComponents().stream().map(ComponentConverter::convertEntityToNormal).toList()).build();
    }

    @Override
    public GetComponentsByCategoryResponse getComponentsByCategory(String category) {
        return GetComponentsByCategoryResponse.builder().allComponentsInCategory(componentRepository.getComponentsByType(category).stream().map(ComponentConverter::convertEntityToNormal).toList()).build();
    }
}
