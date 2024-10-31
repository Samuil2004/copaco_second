package nl.fontys.s3.copacoproject.domain;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpecificationType_ComponentType {
    private long id;
    private ComponentType componentType;
    private SpecificationType specificationType;
}
