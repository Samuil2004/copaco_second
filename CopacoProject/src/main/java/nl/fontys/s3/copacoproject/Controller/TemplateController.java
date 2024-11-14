package nl.fontys.s3.copacoproject.Controller;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectExistsAlreadyException;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.TemplateManager;
import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.CreateTemplateRequest;
import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.UpdateTemplateRequest;
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
    public Template getTemplateById(@PathVariable("id") long id) {
        return templateManager.getTemplateById(id);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Template>> getTemplatesByName(@PathVariable("name") String name) {
        try{
            return ResponseEntity.ok(templateManager.getTemplatesByName(name));
        }
        catch(ObjectNotFound e){
            return ResponseEntity.notFound().build();
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
        catch(ObjectNotFound e) {
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTemplate(@PathVariable long id, @RequestBody @Validated UpdateTemplateRequest request) {
        try{
            templateManager.updateTemplate(id, request);
            return ResponseEntity.ok().build();
        }
        catch(ObjectNotFound e){
            return ResponseEntity.notFound().build();
        }
        catch(InvalidParameterException | ObjectExistsAlreadyException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
