package nl.fontys.s3.copacoproject.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.CustomProductManager;
import nl.fontys.s3.copacoproject.business.SpecificationsManager;
import nl.fontys.s3.copacoproject.business.dto.specification_type_dto.GetConfTypesInCategResponse;
import nl.fontys.s3.copacoproject.business.dto.specification_type_dto.GetConfigTypesInCategRequest;
import nl.fontys.s3.copacoproject.business.dto.statistcs.RevenueInfoItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final CustomProductManager customProductManager;
    private final SpecificationsManager specificationsManager;

    @RolesAllowed({"ADMIN"})
    @GetMapping("/countTotalRevenue/{categoryId}/{status}")
    public ResponseEntity<Integer> getTotalNumberOfFinishedProductsByStatus(
            @PathVariable Long categoryId,
            @PathVariable @Pattern(regexp = "[a-zA-Z]+", message = "Status must only contain letters") String status) {
        int totalNumberOfProducts = customProductManager.getTotalNumberOfCustomProductsByStatus(categoryId, status);

        return ResponseEntity.ok().body(totalNumberOfProducts);
    }

    @RolesAllowed({"ADMIN"})
    @GetMapping("/countRevenuePerConfigurationType/{categoryId}/{status}")
    public ResponseEntity<List<RevenueInfoItem>> getTotalNumberOfFinishedProductsPerConfigurationType(
            @PathVariable Long categoryId,
            @PathVariable @Pattern(regexp = "[a-zA-Z]+", message = "Status must only contain letters") String status){
        GetConfigTypesInCategRequest getConfigTypesInCategRequest = GetConfigTypesInCategRequest.builder()
                .categoryId(categoryId).build();
        GetConfTypesInCategResponse configurationTypesList = specificationsManager.getDistinctConfigurationTypesInCategory(getConfigTypesInCategRequest);

        List<RevenueInfoItem> info = new ArrayList<>();
        for(String configurationType : configurationTypesList.getDistinctConfigurationTypesInCategory()){
            int totalNumberOfProducts = customProductManager.getTotalNumberOfProductsByConfigurationTypeAndStatus(configurationType, status);
            double income = customProductManager.getIncomeByConfigurationType(configurationType);
            info.add(RevenueInfoItem.builder()
                    .configurationType(configurationType)
                    .itemsSold(totalNumberOfProducts)
                    .income(income).build());
        }
        return ResponseEntity.ok().body(info);
    }

    @GetMapping("/income/total")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Double> getTotalIncome(){
        double value = customProductManager.getTotalIncome();
        return ResponseEntity.ok().body(value);
    }

    @GetMapping("/income/{configurationType}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Double> getIncomeByConfigurationType(@PathVariable String configurationType){
        double value = customProductManager.getIncomeByConfigurationType(configurationType);
        return ResponseEntity.ok().body(value);
    }

    @RolesAllowed({"ADMIN"})
    @GetMapping("/income/average")
    public ResponseEntity<Double> getAverageIncomePerConfigurationType(){
        double value = customProductManager.getAverageOrderPrice();
        return ResponseEntity.ok().body(value);
    }
}