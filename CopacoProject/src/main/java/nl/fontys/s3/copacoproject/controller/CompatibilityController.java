package nl.fontys.s3.copacoproject.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.CompatibilityBetweenComponents;
import nl.fontys.s3.copacoproject.business.CompatibilityManager;
import nl.fontys.s3.copacoproject.business.dto.*;
import nl.fontys.s3.copacoproject.business.dto.rule.RuleResponse;
import nl.fontys.s3.copacoproject.business.dto.rule.UpdateRuleRequest;
import nl.fontys.s3.copacoproject.domain.CompatibilityType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compatibility")
@AllArgsConstructor
public class CompatibilityController {
    private final CompatibilityManager compatibilityManager;
    private final CompatibilityBetweenComponents compatibilityBetweenComponents;

    @GetMapping()
    public ResponseEntity<List<CompatibilityType>> getAllCompatibilityTypes(){
        List<CompatibilityType> allCompatibilityTypes = compatibilityManager.allCompatibilityTypes();

        return new ResponseEntity<>(allCompatibilityTypes, HttpStatus.OK);

    }
    @PostMapping("/createAutomaticCompatibility")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Void> createAutomaticCompatibility(@RequestBody @Valid CreateAutomaticCompatibilityDtoRequest request) {
        compatibilityManager.createAutomaticCompatibility(request);

        return ResponseEntity.noContent().build();

    }

    @PostMapping("/createManualCompatibility")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Void> createManualCompatibility(@RequestBody @Valid CreateManualCompatibilityDtoRequest request) {
        compatibilityManager.createManualCompatibility(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/automaticCompatibility/{automaticCompatibilityId}")
    public ResponseEntity<GetAutomaticCompatibilityByIdResponse> automaticCompatibilityByCompatibilityId(@PathVariable("automaticCompatibilityId") Long automaticCompatibilityId){
        GetAutomaticCompatibilityByIdResponse automaticCompatibility = compatibilityManager.automaticCompatibilityByCompatibilityId(automaticCompatibilityId);

        return new ResponseEntity<>(automaticCompatibility, HttpStatus.OK);
    }

    @GetMapping("/allAutomaticCompatibilitiesByGivenComponentTypeId/{componentTypeId}")
    public ResponseEntity<List<GetAutomaticCompatibilityByIdResponse>> getAllAutomaticCompatibilitiesForAComponentType(@PathVariable("componentTypeId") Long automaticCompatibilityId){
        List<GetAutomaticCompatibilityByIdResponse> automaticCompatibility = compatibilityManager.allCompatibilitiesForComponentTypeByComponentTypeId(automaticCompatibilityId);
        return new ResponseEntity<>(automaticCompatibility, HttpStatus.OK);
    }

    @GetMapping("/filteredRules")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<List<RuleResponse>> getRulesByCategoryAndConfigurationType(
            @RequestParam (value = "configurationType", required = false) String configurationType,
            @RequestParam (value = "currentPage", defaultValue = "1", required = false) @Min(1) int currentPage,
            @RequestParam (value = "itemsPerPage", defaultValue = "10") int itemsPerPage){
        List<RuleResponse> rules = compatibilityManager.getRulesByCategoryAndConfigurationType(configurationType, currentPage, itemsPerPage);
        return new ResponseEntity<>(rules, HttpStatus.OK);
    }

    @GetMapping("/getRuleById")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<RuleResponse> getRuleById(
            @RequestParam (value = "ruleId") Long ruleId){
        RuleResponse foundRule = compatibilityManager.getRuleById(ruleId);
        return new ResponseEntity<>(foundRule, HttpStatus.OK);
    }

    @DeleteMapping("/deleteById")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Void> deleteRuleById(
            @RequestParam (value = "ruleId") Long ruleId){
        compatibilityManager.deleteRuleById(ruleId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateRuleByid")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<RuleResponse> updateRuleById(
            @RequestBody @Valid UpdateRuleRequest request){
        RuleResponse updatedRule = compatibilityManager.updateRuleById(request);
        return new ResponseEntity<>(updatedRule, HttpStatus.OK);
    }

//    @GetMapping("/configurator2")
//    public ResponseEntity<List<GetAutomaticCompatibilityResponse>> getAllComponentsFromAGivenComponentTypeAndSpecification(
//            @RequestParam("firstComponentId") Long firstComponentId,
//            @RequestParam(value = "secondComponentId", required = false) @NotNull @Min(value = 0,message="The id can not be negative") Long secondComponentId,
//            @RequestParam(value = "thirdComponentId", required = false) Long thirdComponentId,
//            @RequestParam(value = "fourthComponentId", required = false) Long fourthComponentId,
//            @RequestParam(value = "fifthComponentId", required = false) Long fifthComponentId,
//            @RequestParam(value = "sixthComponentId", required = false) Long sixthComponentId,
//            @RequestParam(value = "seventhComponentId", required = false) Long seventhComponentId,
//            @RequestParam("searchedComponentsType") Long searchedComponentsType,
//            @RequestParam("pageNumber") Integer pageNumber,
//            @RequestParam("typeOfConfiguration") String typeOfConfiguration
//            ){
//        ConfiguratorRequest request = ConfiguratorRequest.builder()
//                .firstComponentId(firstComponentId)
//                .secondComponentId(secondComponentId)
//                .thirdComponentId(thirdComponentId)
//                .fourthComponentId(fourthComponentId)
//                .fifthComponentId(fifthComponentId)
//                .sixthComponentId(sixthComponentId)
//                .seventhComponentId(seventhComponentId)
//                .searchedComponentTypeId(searchedComponentsType)
//                .pageNumber(pageNumber)
//                .typeOfConfiguration(typeOfConfiguration)
//                .build();
//        List<GetAutomaticCompatibilityResponse> automaticCompatibility = compatibilityBetweenComponents.automaticCompatibility(request);
//
//        return new ResponseEntity<>(automaticCompatibility, HttpStatus.OK);
//
//    }
@PostMapping("/configurator")
public ResponseEntity<List<GetAutomaticCompatibilityResponse>> getAllComponentsFromAGivenComponentTypeAndSpecification(
        @Valid @RequestBody ConfiguratorRequest request
){
    List<GetAutomaticCompatibilityResponse> automaticCompatibility = compatibilityBetweenComponents.automaticCompatibility(request);

    return new ResponseEntity<>(automaticCompatibility, HttpStatus.OK);
}


}
