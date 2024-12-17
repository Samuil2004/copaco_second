package nl.fontys.s3.copacoproject.business.dto.rule;

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
public class UpdateRuleRequest {

    @NotNull(message = "Rule id can not be null")
    @Min(value = 1,message = "Rule id must be at least 1")
    private Long ruleId;


    @NotBlank(message = "Value can not be blank")
    private String valueForTheFirstSpecification;

    @NotNull(message = "At least one value should be provided")
    private List<@NotBlank(message = "Value can not be blank") String> valuesForTheSecondSpecification;

    @NotBlank(message = "Configuration type cannot be blank")
    private String configurationType;

}
