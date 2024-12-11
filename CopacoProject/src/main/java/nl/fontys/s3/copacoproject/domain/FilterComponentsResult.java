package nl.fontys.s3.copacoproject.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class FilterComponentsResult {
    private List<ComponentEntity> components;
    private Boolean thereIsNextPage;
}
