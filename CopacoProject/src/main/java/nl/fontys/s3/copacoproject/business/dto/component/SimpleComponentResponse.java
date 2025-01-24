package nl.fontys.s3.copacoproject.business.dto.component;

import lombok.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class SimpleComponentResponse {
    private Long componentId;
    private String componentName;
    private String componentImageUrl;
    private Double componentPrice;
    private boolean thereIsNextPage;
}
