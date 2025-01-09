package nl.fontys.s3.copacoproject.business.dto.statistcs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RevenueInfoItem {
    private String configurationType;
    private int itemsSold;
    private double income;
}
