package nl.fontys.s3.copacoproject.business.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CreateManualCompatibilityDtoRequest {

    @NotNull(message = "Component id can not be null")
    @Min(value = 1,message = "Component id must be at least 1")
    private Long componentType1Id;

    @NotNull(message = "Component id can not be null")
    @Min(value = 1,message = "Component id must be at least 1")
    private Long componentType2Id;

    @NotNull(message = "Specification id can not be null")
    @Min(value = 1,message = "Specification id must be at least 1")
    private Long specificationToConsiderId_from_component1;

    @NotNull(message = "Specification id can not be null")
    @Min(value = 1,message = "Specification id must be at least 1")
    private Long specificationToConsiderId_from_component2;

    @NotBlank(message = "Value can not be blank")
    private String valueForTheFirstSpecification;

    @NotNull(message = "At least one value should be provided")
    private List<@NotBlank(message = "Value can not be blank") String> valuesForTheSecondSpecification;


}
