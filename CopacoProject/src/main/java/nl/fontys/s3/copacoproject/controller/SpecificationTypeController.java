package nl.fontys.s3.copacoproject.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.SpecificationTypeManager;
import nl.fontys.s3.copacoproject.business.SpecificationType_ComponentType;
import nl.fontys.s3.copacoproject.business.SpecificationsManager;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.*;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specificationType")
@AllArgsConstructor
public class SpecificationTypeController {
    private final SpecificationTypeManager specificationTypeManager;
    private final SpecificationType_ComponentType specificationType_ComponentType;
    private final SpecificationsManager specificationsManager;

    @GetMapping
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<GetAllSpecificationTypeResponse> getAllSpecificationTypes(){
        GetAllSpecificationTypeResponse response = specificationTypeManager.getAllSpecificationType();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("componentId/{componentId}")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<List<SpecificationType>> getSpecificationTypesByComponentId(@PathVariable long componentId){
        return ResponseEntity.status(HttpStatus.OK).body(specificationTypeManager.getSpecificationTypesByComponentId(componentId));
    }

    @GetMapping("componentTypeId/{componentTypeId}")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<GetSpecificationTypeByComponentTypeResponse> getSpecificationTypesByComponentTypeId(@PathVariable Long componentTypeId,
                                                                                          @RequestParam(value = "itemsPerPage", defaultValue = "10") int itemsPerPage,
                                                                                          @RequestParam(value = "currentPage", defaultValue = "1") @Min(value = 1, message = "Page numbering starts from 1") int currentPage){
        return ResponseEntity.status(HttpStatus.OK).body(specificationTypeManager.getSpecificationTypesByComponentTypeId(componentTypeId, currentPage, itemsPerPage));
    }

    @GetMapping("values/{specificationTypeId}")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<List<String>> getSpecificationTypeValuesByComponentTypeId(
            @PathVariable Long specificationTypeId,
            @RequestParam Long componentTypeId,
            @RequestParam (defaultValue = "1") @Min(1) int currentPage,
            @RequestParam (defaultValue = "10", required = false) int itemsPerPage){
        return ResponseEntity.status(HttpStatus.OK).body(specificationsManager.getSpecificationValuesOfSpecificationTypeByComponentType(componentTypeId, specificationTypeId, currentPage, itemsPerPage));
    }

    @PostMapping
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<CreateSpecificationTypeResponse> createSpecificationType(@RequestBody CreateSpecificationTypeRequest request){
        CreateSpecificationTypeResponse response = specificationTypeManager.createSpecificationType(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("id/{id}")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<SpecificationType> getSpecificationById(@PathVariable Long id){
        SpecificationType specificationType = specificationTypeManager.getSpecificationType(id);
        if (specificationType == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else
            return new ResponseEntity<>(specificationType, HttpStatus.OK);
    }

    //Authentication disabled for this one
    @GetMapping("findIdByComponentTypeIdAndSpecificationTypeId/{componentTypeId}/{specificationTypeId}")
    @RolesAllowed({"ADMIN"})
    public Long findIdByComponentTypeIdAndSpecificationTypeId(@PathVariable Long componentTypeId, @PathVariable Long specificationTypeId){
        return specificationType_ComponentType.findIdByComponentTypeIdAndSpecificationTypeId(componentTypeId,specificationTypeId);
    }

    @GetMapping("/getDistinctConfigurationTypes")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<GetConfigurationTypesResponse> getDistinctConfigurationTypes(){
        GetConfigurationTypesResponse response = specificationsManager.getDistinctConfigurationTypes();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/getDistinctConfigurationTypesInCategory")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<GetConfTypesInCategResponse> getDistinctConfigurationTypesInCategory(
            @RequestParam("categoryId") Long categoryId) {
        GetConfigTypesInCategRequest request =  GetConfigTypesInCategRequest.builder().categoryId(categoryId).build();
        GetConfTypesInCategResponse response = specificationsManager.getDistinctConfigurationTypesInCategory(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
