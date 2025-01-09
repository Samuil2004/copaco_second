package nl.fontys.s3.copacoproject.business.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ConfiguratorRequest {
    @NotNull(message = "Component IDs list must not be null.")
    @NotEmpty(message = "Component IDs list must not be empty.")
    private List<Long> componentIds;
//    private Long firstComponentId;
//    private Long secondComponentId;
//    private Long thirdComponentId;
//    private Long fourthComponentId;
//    private Long fifthComponentId;
//    private Long sixthComponentId;
//    private Long seventhComponentId;

    @NotNull(message = "Searched component type ID must not be null.")
    @Min(value=0,message="Searched component type can not be negative")
    private Long searchedComponentTypeId;

    @NotNull(message = "Page number must not be null.")
    @Min(value=0,message="Page number can not be negative")
    private Integer pageNumber;

    @NotBlank(message = "Type of configuration must not be null.")
    private String typeOfConfiguration;
}
