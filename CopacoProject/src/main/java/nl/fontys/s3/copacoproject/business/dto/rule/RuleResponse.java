package nl.fontys.s3.copacoproject.business.dto.rule;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RuleResponse {
    private Long ruleId;
    private ComponentTypeInRuleResponse componentType1;
    private ComponentTypeInRuleResponse componentType2;
    private String configurationType;
}
