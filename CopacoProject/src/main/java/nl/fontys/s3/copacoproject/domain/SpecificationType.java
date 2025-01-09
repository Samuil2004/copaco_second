package nl.fontys.s3.copacoproject.domain;

import jakarta.persistence.Entity;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SpecificationType {
    private Long specificationTypeId;
    private String specificationTypeName;
}
