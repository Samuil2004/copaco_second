package nl.fontys.s3.copacoproject.Controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.ComponentManager;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.dto.GetComponentResponse;
import nl.fontys.s3.copacoproject.domain.Component;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/components")
@AllArgsConstructor
public class ComponentController {

    private final ComponentManager componentManager;


    @GetMapping("")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<List<GetComponentResponse>> getAllComponents() {

        List<GetComponentResponse> response = componentManager.getAllComponents();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{categoryId}")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<List<Component>> getComponentsInCategory(@PathVariable("categoryId") long categoryId) {
        try{
            return ResponseEntity.ok(componentManager.getAllComponentsByCategory(categoryId));
        }
        catch(ObjectNotFound e){
            return ResponseEntity.notFound().build();
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/findByComponentTypeId/{componentTypeId}")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<List<Component>> getComponentsFromComponentType(@PathVariable("componentTypeId") Long componentTypeId) {
        try{
            return ResponseEntity.ok(componentManager.getAllComponentFromComponentType(componentTypeId));
        }
        catch(ObjectNotFound e){
            return ResponseEntity.notFound().build();
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

}
