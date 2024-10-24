package nl.fontys.s3.copacoproject.Controller;

import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.CompatibilityManager;
import nl.fontys.s3.copacoproject.domain.CompatibilityType;
import nl.fontys.s3.copacoproject.domain.ComponentType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/compatibility")
@AllArgsConstructor
public class CompatibilityController {
    private final CompatibilityManager compatibilityManager;
    @GetMapping("")
    public ResponseEntity<List<CompatibilityType>> getAllCompatibilityTypes(){
        List<CompatibilityType> allCompatibilityTypes = compatibilityManager.allCompatibilityTypes();

        return new ResponseEntity<>(allCompatibilityTypes, HttpStatus.OK);

    }
}
