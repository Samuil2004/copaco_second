package nl.fontys.s3.copacoproject.business.dto.custom_product_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateCustomTemplateRequest {
    private List<ComponentInCustomProductInput> componentsIncluded;
    private int statusId;
}
