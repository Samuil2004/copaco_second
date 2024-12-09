package nl.fontys.s3.copacoproject.business.dto.componentTypeDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeEntity;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
public class GetDistinctComponentTypesByTypeOfConfigurationResponse {
    //List<ComponentTypeEntity> distinctComponentTypesFromTypeOfConfiguration;
    Map<Long,String> distinctComponentTypesFromTypeOfConfiguration;
}
