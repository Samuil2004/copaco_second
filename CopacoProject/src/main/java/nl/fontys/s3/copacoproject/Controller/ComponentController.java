package nl.fontys.s3.copacoproject.Controller;

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
    public ResponseEntity<List<GetComponentResponse>> getAllComponents() {

        List<GetComponentResponse> response = componentManager.getAllComponents();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{categoryId}")
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

}
