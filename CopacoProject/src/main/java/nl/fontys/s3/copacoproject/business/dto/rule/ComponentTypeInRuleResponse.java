package nl.fontys.s3.copacoproject.business.dto.rule;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ComponentTypeInRuleResponse {
    private Long componentId;
    private String name;
    private Long specificationTypeId;
    private String specificationTypeConsidered;
    private String value;
}
