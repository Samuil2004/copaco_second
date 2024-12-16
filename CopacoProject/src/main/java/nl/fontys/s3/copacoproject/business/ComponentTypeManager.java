package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.ComponentTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.GetAllComponentTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.GetDistinctComponentTypesByTypeOfConfigurationRequest;
import nl.fontys.s3.copacoproject.domain.ComponentType;

import java.util.List;

public interface ComponentTypeManager {
    GetAllComponentTypeResponse getAllComponentTypes();
    ComponentType getComponentTypeById(long id);
    List<ComponentType> getComponentTypesByCategory(long categoryId);
    //CreateComponentTypeResponse createComponentType(CreateComponentTypeRequest request);
    /*void updateComponentType(UpdateComponentTypeRequest request);*/
    //void deleteComponentType(long id);
    List<ComponentTypeResponse> findDistinctComponentTypesByTypeOfConfiguration(GetDistinctComponentTypesByTypeOfConfigurationRequest request);
    List<ComponentTypeResponse> getComponentTypesByTemplateId(Long templateId);
}
