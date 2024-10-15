package nl.fontys.s3.copacoproject.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Template {
    private long templateId;
    private Category category;
    private String name;
    private String brand;
    private String Manufacturer;
    private String imageUrl;
    private List<ComponentType> components;
}
