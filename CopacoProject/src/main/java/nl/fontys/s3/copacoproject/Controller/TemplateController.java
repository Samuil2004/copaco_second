package nl.fontys.s3.copacoproject.Controller;

import jakarta.annotation.security.RolesAllowed;
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
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Void> createTemplate(@RequestBody @Validated CreateTemplateRequest request) {
        try {
            templateManager.createTemplate(request);
            return ResponseEntity.ok().build();
        }
        catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<Template> getTemplateById(@PathVariable("id") long id) {
        try{
            return ResponseEntity.ok(templateManager.getTemplateById(id));
        }
        catch(ObjectNotFound e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name/{name}")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<List<Template>> getTemplatesByName(@PathVariable("name") String name) {
        try{
            return ResponseEntity.ok(templateManager.getTemplatesByName(name));
        }
        catch(ObjectNotFound e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping()
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<List<Template>> getTemplates() {
        return ResponseEntity.ok(templateManager.getTemplates());
    }

    @GetMapping("/filtered")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<List<Template>> getFilteredTemplates(
            @RequestParam(value = "itemsPerPage", defaultValue = "10") int itemsPerPage,
            @RequestParam(value = "currentPage", defaultValue = "0") int currentPage,
            @RequestParam(value = "categoryId", defaultValue = "0") long categoryId) {

        List<Template> filteredTemplates = templateManager.getFilteredTemplates(itemsPerPage, currentPage, categoryId);
        return ResponseEntity.ok(filteredTemplates);
    }

    @GetMapping("/countItems")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<Integer> getNumberOfTemplates(@RequestParam(value = "categoryId", defaultValue = "0") long categoryId) {
        int numberOfTemplates = templateManager.getNumberOfTemplates(categoryId);
        return ResponseEntity.ok().body(numberOfTemplates);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Void> deleteTemplate(@PathVariable long id) {
        try{
            templateManager.deleteTemplate(id);
            return ResponseEntity.ok().build();
        }
        catch(ObjectNotFound e) {
            return ResponseEntity.notFound().build();
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
