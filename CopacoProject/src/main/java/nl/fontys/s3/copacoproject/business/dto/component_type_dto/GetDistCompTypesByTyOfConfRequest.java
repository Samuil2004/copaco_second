package nl.fontys.s3.copacoproject.business.dto.component_type_dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetDistCompTypesByTyOfConfRequest {
    private String typeOfConfiguration;
}
