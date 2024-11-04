package nl.fontys.s3.copacoproject.domain;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComponentType
{
    private Long componentTypeId;
    private String componentTypeName;
    private String componentTypeImageUrl;
    private Category category;
    private List<SpecificationType> specificationTypeList;
    //private List<CompatibilityType> compatibilityTypeList;

    @Override
    public String toString() {
        return "ComponentType{name='" + componentTypeName + "', id=" + componentTypeId + "}"; // Customize as needed
    }

}
