package nl.fontys.s3.copacoproject.controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.ComponentTypeManager;
import nl.fontys.s3.copacoproject.business.dto.component_type_dto.ComponentTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.component_type_dto.GetAllComponentTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.component_type_dto.GetDistCompTypesByTyOfConfRequest;
import nl.fontys.s3.copacoproject.domain.ComponentType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/componentType")
@AllArgsConstructor
public class ComponentTypeController {
    private ComponentTypeManager componentTypeManager;
    @GetMapping
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<GetAllComponentTypeResponse> getAllComponentTypes(){
        GetAllComponentTypeResponse response = componentTypeManager.getAllComponentTypes();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<ComponentType> getComponentTypeById(@PathVariable Long id){
        ComponentType componentType = componentTypeManager.getComponentTypeById(id);

            return new ResponseEntity<>(componentType, HttpStatus.OK);

    }

    @GetMapping("/category/{id}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<List<ComponentType>> getComponentTypeByCategory(@PathVariable Long id){
        List<ComponentType> componentType = componentTypeManager.getComponentTypesByCategory(id);

        return ResponseEntity.status(HttpStatus.OK).body(componentType);
    }

    @GetMapping("/getDistinctComponentTypesFromConfigurationType")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<List<ComponentTypeResponse>> getDistinctComponentTypesFromConfigurationType(
            @RequestParam("configurationType") String configurationType) {
        GetDistCompTypesByTyOfConfRequest request =  GetDistCompTypesByTyOfConfRequest.builder().typeOfConfiguration(configurationType).build();
        List<ComponentTypeResponse> response = componentTypeManager.findDistinctComponentTypesByTypeOfConfiguration(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
