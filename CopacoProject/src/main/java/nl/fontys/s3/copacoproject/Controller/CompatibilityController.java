package nl.fontys.s3.copacoproject.Controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.CompatibilityBetweenComponents;
import nl.fontys.s3.copacoproject.business.CompatibilityManager;
import nl.fontys.s3.copacoproject.business.dto.*;
import nl.fontys.s3.copacoproject.domain.CompatibilityType;
import nl.fontys.s3.copacoproject.domain.Component;
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
    @PostMapping("/automatic")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Void> createAutomaticCompatibility(@RequestBody @Valid CreateAutomaticCompatibilityDtoRequest request) {
        CreateAutomaticCompatibilityDtoResponse response = compatibilityManager.createAutomaticCompatibility(request);
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

    @GetMapping("/getAllComponentsFromAGivenComponentTypeAndSpecification")
    public ResponseEntity<List<GetAutomaticCompatibilityResponse>> getAllComponentsFromAGivenComponentTypeAndSpecification(
            @RequestParam("firstComponentId") Long firstComponentId,
            @RequestParam(value = "secondComponentId", required = false) Long secondComponentId,
            @RequestParam(value = "thirdComponentId", required = false) Long thirdComponentId,
            @RequestParam(value = "fourthComponentId", required = false) Long fourthComponentId,
            @RequestParam(value = "fifthComponentId", required = false) Long fifthComponentId,
            @RequestParam(value = "sixthComponentId", required = false) Long sixthComponentId,
            @RequestParam(value = "seventhComponentId", required = false) Long seventhComponentId,
            @RequestParam("searchedComponentsType") Long searchedComponentsType,
            @RequestParam("pageNumber") Integer pageNumber,
            @RequestParam("typeOfConfiguration") String typeOfConfiguration



            ){
        GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest request = GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest.builder()
                .firstComponentId(firstComponentId)
                .secondComponentId(secondComponentId)
                .thirdComponentId(thirdComponentId)
                .fourthComponentId(fourthComponentId)
                .fifthComponentId(fifthComponentId)
                .sixthComponentId(sixthComponentId)
                .seventhComponentId(seventhComponentId)
                .searchedComponentTypeId(searchedComponentsType)
                .pageNumber(pageNumber)
                .typeOfConfiguration(typeOfConfiguration)
                .build();
        List<GetAutomaticCompatibilityResponse> automaticCompatibility = compatibilityBetweenComponents.automaticCompatibility(request);

        return new ResponseEntity<>(automaticCompatibility, HttpStatus.OK);

    }


}
