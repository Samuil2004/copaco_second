package nl.fontys.s3.copacoproject.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Template {
    private Integer templateId;
    private Category category;
    private String name;
    private String brand;
    private String Manufacturer;
    private String imageUrl;
}
