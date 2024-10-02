package nl.fontys.s3.copacoproject.business.dto;

import lombok.Builder;
import lombok.Getter;
import nl.fontys.s3.copacoproject.domain.Component;

import java.util.List;

@Builder
@Getter
public class GetAllComponentsResponse {
    private List<Component> allComponents;
}
