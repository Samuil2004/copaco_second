package nl.fontys.s3.copacoproject.persistence.entity.supportingEntities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class SpecificationTypeAndValuesForIt {
    private Long specificationId;
    private String valuesToBeConsideredForThisSpecification;
}
