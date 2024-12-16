package nl.fontys.s3.copacoproject.business.dto.componentTypeDto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetDistinctComponentTypesByTypeOfConfigurationRequest {
    private String typeOfConfiguration;
}
