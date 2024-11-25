package nl.fontys.s3.copacoproject.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.fontys.s3.copacoproject.domain.enums.Status;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomProduct {
    private long customProductId;
    private Template template;
    private User user;
    private List<Component> componentsIncluded;
    private Status status;
}
