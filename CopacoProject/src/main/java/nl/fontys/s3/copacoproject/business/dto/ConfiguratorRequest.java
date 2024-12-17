package nl.fontys.s3.copacoproject.business.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ConfiguratorRequest {
    private Long firstComponentId;
    private Long secondComponentId;
    private Long thirdComponentId;
    private Long fourthComponentId;
    private Long fifthComponentId;
    private Long sixthComponentId;
    private Long seventhComponentId;
    private Long searchedComponentTypeId;
    private Integer pageNumber;
    private String typeOfConfiguration;
}
