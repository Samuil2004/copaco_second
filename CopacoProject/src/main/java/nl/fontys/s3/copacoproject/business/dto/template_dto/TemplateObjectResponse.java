package nl.fontys.s3.copacoproject.business.dto.template_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private String configurationType;
    private byte[] image;
    private List<String> components;
    private boolean active;
    private boolean thereIsNextPage;
}
