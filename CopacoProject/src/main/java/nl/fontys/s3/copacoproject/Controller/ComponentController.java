package nl.fontys.s3.copacoproject.Controller;

import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.ComponentManager;
import nl.fontys.s3.copacoproject.business.dto.GetAllComponentsResponse;
import nl.fontys.s3.copacoproject.business.dto.GetComponentResponse;
import nl.fontys.s3.copacoproject.business.dto.GetComponentsByCategoryResponse;
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
//    @GetMapping("/{category}")
//    public ResponseEntity<GetComponentsByCategoryResponse> getComponentsInCategory(@PathVariable("category") String category) {
//
//        GetComponentsByCategoryResponse response = componentManager.getComponentsByCategory(category);
//        return ResponseEntity.ok(response);
//    }

}
