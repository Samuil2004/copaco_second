package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.CreateComponentTypeRequest;
import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.CreateComponentTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.GetAllComponentTypeResponse;
import nl.fontys.s3.copacoproject.domain.ComponentType;

import java.util.Optional;

public interface ComponentTypeManager {
    GetAllComponentTypeResponse getAllComponentTypes();
    Optional<ComponentType> getComponentTypeById(long id);
    CreateComponentTypeResponse createComponentType(CreateComponentTypeRequest request);
    /*void updateComponentType(UpdateComponentTypeRequest request);*/
    void deleteComponentType(long id);
}
