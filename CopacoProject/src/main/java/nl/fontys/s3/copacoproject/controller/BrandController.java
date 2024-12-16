package nl.fontys.s3.copacoproject.controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.BrandManager;
import nl.fontys.s3.copacoproject.domain.Brand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brand")
@AllArgsConstructor
public class BrandController {

    private final BrandManager brandManager;

    @GetMapping()
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public ResponseEntity<List<Brand>> getAllBrands() {
        return ResponseEntity.ok().body(brandManager.getAllBrands());
    }
}
