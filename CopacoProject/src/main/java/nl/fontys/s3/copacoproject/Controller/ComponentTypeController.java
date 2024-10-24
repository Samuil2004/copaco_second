package nl.fontys.s3.copacoproject.Controller;

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
@RequiredArgsConstructor
public class ComponentTypeController {
    private ComponentTypeManager componentTypeManager;
    @GetMapping
    public ResponseEntity<GetAllComponentTypeResponse> getAllComponentTypes(){
        GetAllComponentTypeResponse response = componentTypeManager.getAllComponentTypes();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping
    public ResponseEntity<CreateComponentTypeResponse> createComponentType(@RequestBody CreateComponentTypeRequest request){
        CreateComponentTypeResponse response = componentTypeManager.createComponentType(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @RequestMapping("/{id}")
    public ResponseEntity<ComponentType> getComponentTypeById(@PathVariable Long id){
        Optional<ComponentType> componentType = componentTypeManager.getComponentTypeById(id);
        if(componentType == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else
            return new ResponseEntity<>(componentType.get(), HttpStatus.OK);

    }


}
