package nl.fontys.s3.copacoproject.business.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ConfiguratorRequest {
    @NotNull(message = "Component IDs list must not be null.")
    @NotEmpty(message = "Component IDs list must not be empty.")
    @Valid
    private List<@NotNull(message = "Component ID must not be null.")
    @Min(value = 1, message = "Component ID must be positive.") Long> componentIds;


    @NotNull(message = "Searched component type ID must not be null.")
    @PositiveOrZero(message = "Searched component type ID must not be negative.")
    private Long searchedComponentTypeId;

    @NotNull(message = "Page number must not be null.")
    @PositiveOrZero(message = "Searched page must not be negative.")
    private Integer pageNumber;

    @NotBlank(message = "Type of configuration must not be null.")
    private String typeOfConfiguration;
}
