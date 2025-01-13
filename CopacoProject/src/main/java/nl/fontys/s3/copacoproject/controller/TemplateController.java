package nl.fontys.s3.copacoproject.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.ComponentTypeManager;
import nl.fontys.s3.copacoproject.business.exception.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.TemplateManager;
import nl.fontys.s3.copacoproject.business.dto.template_dto.CreateTemplateRequest;
import nl.fontys.s3.copacoproject.business.dto.template_dto.TemplateObjectResponse;
import nl.fontys.s3.copacoproject.business.dto.template_dto.UpdateTemplateRequest;
import nl.fontys.s3.copacoproject.business.dto.component_type_dto.ComponentTypeResponse;
import nl.fontys.s3.copacoproject.domain.Template;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
public class TemplateController {
    private final TemplateManager templateManager;
    private final ComponentTypeManager componentTypeManager;

@PostMapping(value = "")
@RolesAllowed({"ADMIN"})
public ResponseEntity<Void> createTemplate(
        @RequestParam(value = "file", required = false) MultipartFile file,
        @RequestParam(value = "categoryId") int categoryId,
        @RequestParam(value = "configurationType") String configurationType,
        @RequestParam(value = "name") String name,
        @RequestParam(value = "componentTypes") List<Long> componentTypes){

    try {
        CreateTemplateRequest request1 = CreateTemplateRequest.builder()
            .categoryId(categoryId)
                    .configurationType(configurationType)
                            .name(name)
                                    .componentTypes(componentTypes)
                                            .build();
        templateManager.createTemplate(request1, file);
        return ResponseEntity.ok().build();
    } catch (IOException e) {
        return ResponseEntity.internalServerError().build();
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
            @RequestParam(value = "currentPage", defaultValue = "1") @Min(1) int currentPage,
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

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<String> updateTemplate(
            @PathVariable long id,
            @RequestPart("request") @Validated UpdateTemplateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try{
            templateManager.updateTemplate(id, request, file);
            return ResponseEntity.ok().build();
        }
        catch(IOException e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{templateId}/componentTypes")
    @RolesAllowed({"ADMIN","CUSTOMER"})
    public ResponseEntity<List<ComponentTypeResponse>> getComponentTypesByTemplateId (@PathVariable Long templateId){
        List<ComponentTypeResponse> componentTypes = componentTypeManager.getComponentTypesByTemplateId(templateId);
        return ResponseEntity.status(HttpStatus.OK).body(componentTypes);
    }
}
