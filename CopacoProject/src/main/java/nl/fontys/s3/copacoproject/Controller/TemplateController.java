package nl.fontys.s3.copacoproject.Controller;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.TemplateManager;
import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.CreateTemplateRequest;
import nl.fontys.s3.copacoproject.domain.Template;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.List;

@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
public class TemplateController {
    private final TemplateManager templateManager;

    @PostMapping()
    public ResponseEntity<Void> createTemplate(@RequestBody @Validated CreateTemplateRequest request) {
        try {
            templateManager.createTemplate(request);
            return ResponseEntity.ok().build();
        }
        catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Template> getTemplateById(@PathVariable("id") long id) {
        try{
            return ResponseEntity.ok(templateManager.getTemplateById(id));
        }
        catch (IndexOutOfBoundsException e) {
            return ResponseEntity.badRequest().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Template>> getTemplatesByName(@PathVariable("name") String name) {
        try{
            return ResponseEntity.ok(templateManager.getTemplatesByName(name));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping()
    public ResponseEntity<List<Template>> getTemplates() {
        try{
            return ResponseEntity.ok(templateManager.getTemplates());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable long id) {
        try{
            templateManager.deleteTemplate(id);
            return ResponseEntity.ok().build();
        }
        catch(InvalidParameterException e) {
            return ResponseEntity.badRequest().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
