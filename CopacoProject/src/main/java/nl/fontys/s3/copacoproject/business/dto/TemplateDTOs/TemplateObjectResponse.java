package nl.fontys.s3.copacoproject.business.dto.TemplateDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.fontys.s3.copacoproject.domain.Brand;
import nl.fontys.s3.copacoproject.domain.Category;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TemplateObjectResponse {
    private long templateId;
    private Category category;
    private String name;
    private Brand brand;
    private String configurationType;
    private String imageUrl;
    private List<String> components;
}
