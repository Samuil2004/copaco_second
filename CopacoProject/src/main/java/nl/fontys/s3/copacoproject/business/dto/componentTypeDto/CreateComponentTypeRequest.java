package nl.fontys.s3.copacoproject.business.dto.componentTypeDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.domain.Category;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CreateComponentTypeRequest {
    @NotNull
    private Long componentTypeId;
    @NotNull
    private String componentTypeName;
    @NotNull
    private String componentTypeImageUrl;
    @NotNull
    private Category category;
    @NotNull
    private List<Long> specificationTypeIds;
    @NotNull
    private List<Long> compatibilityTypeListIds;
}
