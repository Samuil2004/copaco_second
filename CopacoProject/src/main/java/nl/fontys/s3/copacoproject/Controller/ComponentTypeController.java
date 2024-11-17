package nl.fontys.s3.copacoproject.Controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.ComponentTypeManager;
import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.CreateComponentTypeRequest;
import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.CreateComponentTypeResponse;
import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.GetAllComponentTypeResponse;
import nl.fontys.s3.copacoproject.domain.ComponentType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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


}
