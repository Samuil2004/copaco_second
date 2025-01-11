package nl.fontys.s3.copacoproject.controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "Get total number of custom products by category",
            description = """
                    Retrieves the total number of custom products in a specific category with a given status.
                    For the graph we need the status to be FINISHED, so that we can see how many products have been ordered
                    It can also be used in the bar graph, to see how many DRAFTS are created by users""",
            tags = {"Statistics"}
    )
    @RolesAllowed({"ADMIN"})
    @GetMapping("/countTotalRevenue/{categoryId}/{status}")
    public ResponseEntity<Integer> getTotalNumberOfFinishedProductsByStatus(
            @PathVariable Long categoryId,
            @PathVariable @Pattern(regexp = "[a-zA-Z]+", message = "Status must only contain letters") String status) {
        int totalNumberOfProducts = customProductManager.getTotalNumberOfCustomProductsByStatus(categoryId, status);

        return ResponseEntity.ok().body(totalNumberOfProducts);
    }

    @Operation(
            summary = "Get number of items and earnings from a configuration type ",
            description = """
                    Retrieves a list of objects that hold the configuration type, number of items sold \
                    and the earnings form that configuration.\s
                    Can be used in the pie graph to display in each slice details about selected specification\s
                    EXAMPLE: {PC, 300 items sold, earned 6000$}""",
            tags = {"Statistics"}
    )
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

    @Operation(
            summary = "Get total income",
            description = "Retrieves the total income gained form all the categories and configuration types",
            tags = {"Statistics"}
    )
    @GetMapping("/income/total")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Double> getTotalIncome(){
        double value = customProductManager.getTotalIncome();
        return ResponseEntity.ok().body(value);
    }

    @Operation(
            summary = "Get income by configuration type",
            description = "Retrieves the income gained form selling products from a specific configuration type",
            tags = {"Statistics"}
    )
    @GetMapping("/income/{configurationType}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Double> getIncomeByConfigurationType(@PathVariable String configurationType){
        double value = customProductManager.getIncomeByConfigurationType(configurationType);
        return ResponseEntity.ok().body(value);
    }

    @Operation(
            summary = "Get average income per configuration type",
            description = "Retrieves the average sum spent by a customer when buying a custom product",
            tags = {"Statistics"}
    )
    @RolesAllowed({"ADMIN"})
    @GetMapping("/income/average")
    public ResponseEntity<Double> getAverageIncome(){
        double value = customProductManager.getAverageOrderPrice();
        return ResponseEntity.ok().body(value);
    }
}