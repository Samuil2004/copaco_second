package nl.fontys.s3.copacoproject.business.dto.custom_product_dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateCustomProductRequest {
    @NotNull
    private long templateId;
    @NotNull
    private long userId;
    @NotNull
    private List<ComponentInCustomProductInput> componentsIncluded;
    @NotNull
    private int statusId;
}
