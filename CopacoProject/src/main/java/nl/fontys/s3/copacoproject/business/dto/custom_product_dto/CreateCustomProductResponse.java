package nl.fontys.s3.copacoproject.business.dto.custom_product_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateCustomProductResponse {
    private long createdProductId;
}
