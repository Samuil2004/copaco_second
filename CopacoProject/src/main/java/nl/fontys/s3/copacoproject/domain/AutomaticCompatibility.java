package nl.fontys.s3.copacoproject.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AutomaticCompatibility {
    private long id;
    private long component1Id;
    private long component2Id;
    private Rule rule;
}
