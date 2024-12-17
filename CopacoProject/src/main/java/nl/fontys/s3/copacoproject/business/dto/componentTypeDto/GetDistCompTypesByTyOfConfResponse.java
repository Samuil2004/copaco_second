package nl.fontys.s3.copacoproject.business.dto.componentTypeDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Builder
@Getter
@Setter
public class GetDistCompTypesByTyOfConfResponse {
    //List<ComponentTypeEntity> distinctComponentTypesFromTypeOfConfiguration;
    Map<Long,String> distinctComponentTypesFromTypeOfConfiguration;
}
