package nl.fontys.s3.copacoproject.persistence.entity.supportingEntities;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class SpecificationTypeAndValuesForIt {
    private Long specification2Id;
    private String valueOfSecondSpecification;

    // Constructor matching the query result
    public SpecificationTypeAndValuesForIt(Long specification2Id, String valueOfSecondSpecification) {
        this.specification2Id = specification2Id;
        this.valueOfSecondSpecification = valueOfSecondSpecification;
    }
}