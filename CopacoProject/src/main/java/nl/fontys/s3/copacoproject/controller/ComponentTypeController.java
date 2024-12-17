package nl.fontys.s3.copacoproject.Controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.ComponentTypeManager;
import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.ComponentTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.GetAllComponentTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.GetDistinctComponentTypesByTypeOfConfigurationRequest;
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
//    @PostMapping
//    public ResponseEntity<CreateComponentTypeResponse> createComponentType(@RequestBody CreateComponentTypeRequest request){
//        CreateComponentTypeResponse response = componentTypeManager.createComponentType(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
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
        GetDistinctComponentTypesByTypeOfConfigurationRequest request =  GetDistinctComponentTypesByTypeOfConfigurationRequest.builder().typeOfConfiguration(configurationType).build();
        List<ComponentTypeResponse> response = componentTypeManager.findDistinctComponentTypesByTypeOfConfiguration(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
