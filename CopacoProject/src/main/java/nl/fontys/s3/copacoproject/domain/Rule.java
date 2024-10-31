package nl.fontys.s3.copacoproject.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Rule {
    private long id;

    private SpecificationType_ComponentType specificationToConsider1Id;

    private SpecificationType_ComponentType specificationToConsider2Id;
}
