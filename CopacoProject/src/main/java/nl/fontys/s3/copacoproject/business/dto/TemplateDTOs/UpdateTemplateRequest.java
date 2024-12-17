package nl.fontys.s3.copacoproject.business.dto.TemplateDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateTemplateRequest {
    @NotNull
    private long categoryId;
    @NotBlank
    private String name;
    @NotBlank
    private MultipartFile image;
    @NotNull
    private List<Long> componentTypes;
}
