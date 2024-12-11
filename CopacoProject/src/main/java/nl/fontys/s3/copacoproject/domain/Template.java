package nl.fontys.s3.copacoproject.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Template {
    private long templateId;
    private Category category;
    private String name;
    private Brand brand;
    private String configurationType;
    private String imageUrl;
    private Map<ComponentType, Integer> components; //integer for order of importance
}
