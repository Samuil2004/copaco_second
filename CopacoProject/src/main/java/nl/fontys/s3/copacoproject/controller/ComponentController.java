package nl.fontys.s3.copacoproject.controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.ComponentManager;
import nl.fontys.s3.copacoproject.business.exception.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.dto.GetComponentResponse;
import nl.fontys.s3.copacoproject.business.dto.component.SimpleComponentResponse;
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

    @GetMapping("/filtered")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<List<SimpleComponentResponse>> getComponentsByComponentTypeAndConfigurationType(
            @RequestParam(name = "componentTypeId") Long componentTypeId,
            @RequestParam(name = "configurationType") String configurationType,
            @RequestParam(name = "currentPage") int currentPage
    ) {
        return ResponseEntity.ok(componentManager.getComponentsByComponentTypeAndConfigurationType(componentTypeId, configurationType, currentPage));
    }
    @GetMapping("/countOfFiltered")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<Integer> getNumberOfComponentsByComponentTypeAndConfigurationType(
            @RequestParam(name = "componentTypeId") Long componentTypeId,
            @RequestParam(name = "configurationType") String configurationType){
        return ResponseEntity.ok(componentManager.getComponentCountByComponentTypeAndConfigurationType(componentTypeId, configurationType));
    }

}
