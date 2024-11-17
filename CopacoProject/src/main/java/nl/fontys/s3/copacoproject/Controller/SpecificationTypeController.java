package nl.fontys.s3.copacoproject.Controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.SpecificationTypeManager;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.CreateSpecificationTypeRequest;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.CreateSpecificationTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.GetAllSpecificationTypeResponse;
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


}
