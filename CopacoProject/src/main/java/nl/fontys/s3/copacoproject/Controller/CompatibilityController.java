package nl.fontys.s3.copacoproject.Controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.CompatibilityBetweenComponents;
import nl.fontys.s3.copacoproject.business.CompatibilityManager;
import nl.fontys.s3.copacoproject.business.dto.CreateAutomaticCompatibilityDtoRequest;
import nl.fontys.s3.copacoproject.business.dto.CreateAutomaticCompatibilityDtoResponse;
import nl.fontys.s3.copacoproject.business.dto.GetAutomaticCompatibilityByIdResponse;
import nl.fontys.s3.copacoproject.domain.AutomaticCompatibility;
import nl.fontys.s3.copacoproject.domain.CompatibilityType;
import nl.fontys.s3.copacoproject.domain.Component;
import nl.fontys.s3.copacoproject.domain.ComponentType;
import nl.fontys.s3.copacoproject.persistence.entity.AutomaticCompatibilityEntity;
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
    @GetMapping("")
    public ResponseEntity<List<CompatibilityType>> getAllCompatibilityTypes(){
        List<CompatibilityType> allCompatibilityTypes = compatibilityManager.allCompatibilityTypes();

        return new ResponseEntity<>(allCompatibilityTypes, HttpStatus.OK);

    }
    @PostMapping("/automatic")
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
    public ResponseEntity<List<Component>> getAllComponentsFromAGivenComponentTypeAndSpecification(
            @RequestParam("componentId") Long componentId,
            @RequestParam("searchedComponentsType") Long searchedComponentsType
    ){
        List<Component> automaticCompatibility = compatibilityBetweenComponents.automaticCompatibility(componentId,searchedComponentsType);

        return new ResponseEntity<>(automaticCompatibility, HttpStatus.OK);

    }


}
