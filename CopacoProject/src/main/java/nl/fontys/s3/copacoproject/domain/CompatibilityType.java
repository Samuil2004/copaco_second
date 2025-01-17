package nl.fontys.s3.copacoproject.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
@Setter
public class CompatibilityType {
    private Long id;

    private String typeOfCompatibility;
}
