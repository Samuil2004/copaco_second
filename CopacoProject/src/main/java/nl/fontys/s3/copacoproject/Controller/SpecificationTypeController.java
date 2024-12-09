package nl.fontys.s3.copacoproject.Controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
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
        try{
            return ResponseEntity.status(HttpStatus.OK).body(specificationTypeManager.getSpecificationTypesByComponentId(componentId));
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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
    public Long findIdByComponentTypeIdAndSpecificationTypeId(@PathVariable Long componentTypeId, @PathVariable Long specificationTypeId){
        Long response = specificationType_ComponentType.findIdByComponentTypeIdAndSpecificationTypeId(componentTypeId,specificationTypeId);
        return response;
    }

    @GetMapping("/getDistinctConfigurationTypes")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<GetDistinctConfigurationTypesResponse> getDistinctConfigurationTypes(){
        GetDistinctConfigurationTypesResponse response = specificationsManager.getDistinctConfigurationTypes();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/getDistinctConfigurationTypesInCategory")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<GetDistinctConfigurationTypesInCategoryResponse> getDistinctConfigurationTypesInCategory(
            @RequestParam("categoryId") Long categoryId) {
        GetDistinctConfigurationTypesInCategoryRequest request =  GetDistinctConfigurationTypesInCategoryRequest.builder().categoryId(categoryId).build();
        GetDistinctConfigurationTypesInCategoryResponse response = specificationsManager.getDistinctConfigurationTypesInCategory(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }





}
