package nl.fontys.s3.copacoproject.Controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.ComponentTypeManager;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectExistsAlreadyException;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.TemplateManager;
import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.CreateTemplateRequest;
import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.TemplateObjectResponse;
import nl.fontys.s3.copacoproject.business.dto.TemplateDTOs.UpdateTemplateRequest;
import nl.fontys.s3.copacoproject.business.dto.componentTypeDto.ComponentTypeResponse;
import nl.fontys.s3.copacoproject.domain.Template;
import org.springframework.http.HttpStatus;
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
    private final ComponentTypeManager componentTypeManager;

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
    public ResponseEntity<TemplateObjectResponse> getTemplateById(@PathVariable("id") long id) {
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
    public ResponseEntity<List<TemplateObjectResponse>> getFilteredTemplates(
            @RequestParam(value = "itemsPerPage", defaultValue = "10") int itemsPerPage,
            @RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "configurationType", required = false) String configurationType) {

        List<TemplateObjectResponse> filteredTemplates = templateManager.getFilteredTemplates(itemsPerPage, currentPage, categoryId, configurationType);
        return ResponseEntity.ok(filteredTemplates);
    }

    @GetMapping("/countItems")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<Integer> getNumberOfTemplates(@RequestParam(value = "categoryId", required = false) Long categoryId,
                                                        @RequestParam(value = "configurationType", required = false) String configurationType) {
        int numberOfTemplates = templateManager.getNumberOfTemplates(categoryId, configurationType);
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

    @GetMapping("/{templateId}/componentTypes")
    @RolesAllowed({"ADMIN","CUSTOMER"})
    public ResponseEntity<List<ComponentTypeResponse>> getComponentTypesByTemplateId (@PathVariable Long templateId){
        List<ComponentTypeResponse> componentTypes = componentTypeManager.getComponentTypesByTemplateId(templateId);
        return ResponseEntity.status(HttpStatus.OK).body(componentTypes);
    }
}
