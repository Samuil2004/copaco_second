package nl.fontys.s3.copacoproject.business.dto;

import lombok.Builder;
import lombok.Getter;
import jakarta.validation.constraints.NotBlank;


@Builder
@Getter
public class GetComponentsByCategoryRequest {
    @NotBlank
    private String category;
}
