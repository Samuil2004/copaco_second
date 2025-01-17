package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.component_type_dto.ComponentTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.component_type_dto.GetAllComponentTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.component_type_dto.GetDistCompTypesByTyOfConfRequest;
import nl.fontys.s3.copacoproject.domain.ComponentType;

import java.util.List;

public interface ComponentTypeManager {
    GetAllComponentTypeResponse getAllComponentTypes();
    ComponentType getComponentTypeById(long id);
    List<ComponentType> getComponentTypesByCategory(long categoryId);
    List<ComponentTypeResponse> findDistinctComponentTypesByTypeOfConfiguration(GetDistCompTypesByTyOfConfRequest request);
    List<ComponentTypeResponse> getComponentTypesByTemplateId(Long templateId);
}
