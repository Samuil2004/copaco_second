package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.GetAllComponentTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.GetDistinctComponentTypesByTypeOfConfigurationRequest;
import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.GetDistinctComponentTypesByTypeOfConfigurationResponse;
import nl.fontys.s3.copacoproject.domain.ComponentType;

import java.util.List;
import java.util.Optional;

public interface ComponentTypeManager {
    GetAllComponentTypeResponse getAllComponentTypes();
    ComponentType getComponentTypeById(long id);
    List<ComponentType> getComponentTypesByCategory(long categoryId);
    //CreateComponentTypeResponse createComponentType(CreateComponentTypeRequest request);
    /*void updateComponentType(UpdateComponentTypeRequest request);*/
    //void deleteComponentType(long id);
    GetDistinctComponentTypesByTypeOfConfigurationResponse findDistinctComponentTypesByTypeOfConfiguration(GetDistinctComponentTypesByTypeOfConfigurationRequest request);
}
