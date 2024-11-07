package nl.fontys.s3.copacoproject.Controller;

import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.CategoryManager;
import nl.fontys.s3.copacoproject.domain.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
@AllArgsConstructor
public class CategoryController {

    private final CategoryManager categoryManager;

    @GetMapping()
    public ResponseEntity<List<Category>> getAllCategories() {
        try{
            return ResponseEntity.ok().body(categoryManager.getAllCategories());
        }
        catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
}
