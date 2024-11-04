package nl.fontys.s3.copacoproject.Controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.SpecificationTypeManager;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.CreateSpecificationTypeRequest;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.CreateSpecificationTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.specificationTypeDto.GetAllSpecificationTypeResponse;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/specificationType")
@AllArgsConstructor
public class SpecificationTypeController {
    private final SpecificationTypeManager specificationTypeManager;
    @GetMapping
    public ResponseEntity<GetAllSpecificationTypeResponse> getAllSpecificationTypes(){
        GetAllSpecificationTypeResponse response = specificationTypeManager.getAllSpecificationType();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping
    public ResponseEntity<CreateSpecificationTypeResponse> createSpecificationType(@RequestBody CreateSpecificationTypeRequest request){
        CreateSpecificationTypeResponse response = specificationTypeManager.createSpecificationType(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<SpecificationType> getSpecificationById(@PathVariable Long id){
        SpecificationType specificationType = specificationTypeManager.getSpecificationType(id);
        if (specificationType == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else
            return new ResponseEntity<>(specificationType, HttpStatus.OK);
    }


}
