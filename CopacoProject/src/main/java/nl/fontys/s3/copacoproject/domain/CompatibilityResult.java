package nl.fontys.s3.copacoproject.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import nl.fontys.s3.copacoproject.persistence.entity.SpecificationTypeEntity;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
public class CompatibilityResult {
    private List<ComponentEntity> compatibleComponents;
    private Map<SpecificationTypeEntity, List<String>> specificationsMap;
}
