package nl.fontys.s3.copacoproject.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.CustomProductManager;
import nl.fontys.s3.copacoproject.business.dto.custom_product_dto.*;
import nl.fontys.s3.copacoproject.configuration.security.auth.RequestAuthenticatedUserProvider;
import nl.fontys.s3.copacoproject.configuration.security.token.AccessToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/configurator")
@AllArgsConstructor
public class CustomProductController {

    private final CustomProductManager customProductManager;
    private final RequestAuthenticatedUserProvider requestAuthenticatedUserProvider;

    @PostMapping()
    @RolesAllowed({"CUSTOMER"})
    public ResponseEntity<CreateCustomProductResponse> createCustomProduct(@RequestBody @Validated CreateCustomProductRequest request) {
        AccessToken accessToken = requestAuthenticatedUserProvider.getAuthenticatedUserInRequest();

        if (accessToken == null || accessToken.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CreateCustomProductResponse response = customProductManager.createCustomProduct(request, accessToken.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    @RolesAllowed({"CUSTOMER"})
    public ResponseEntity<List<CustomProductResponse>> getCustomProductsOfUserByState(@PathVariable("userId") long userId,
                                                                                      @RequestParam("statusId") @Positive int statusId,
                                                                                      @RequestParam(value = "currentPage", defaultValue = "0") int currentPage,
                                                                                      @RequestParam(value = "itemsPerPage", defaultValue = "10") int itemsPerPage) {
        AccessToken accessToken = requestAuthenticatedUserProvider.getAuthenticatedUserInRequest();

        if (accessToken == null || accessToken.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<CustomProductResponse> response = customProductManager.getCustomProductsOfUserByState(userId, accessToken.getUserId(), currentPage, itemsPerPage, statusId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/countItems/{userId}")
    @RolesAllowed({"CUSTOMER"})
    public ResponseEntity<Integer> getNumberOfCustomProductsOfUserByState(@PathVariable("userId") long userId, @RequestParam("statusId") @NotNull @Positive int statusId) {
        AccessToken accessToken = requestAuthenticatedUserProvider.getAuthenticatedUserInRequest();

        if (accessToken == null || accessToken.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int numberOfItems = customProductManager.getNumberOfCustomProductsOfUserByStatus(userId, accessToken.getUserId(), statusId);
        return ResponseEntity.ok().body(numberOfItems);
    }

    @DeleteMapping("/{customProductId}")
    @RolesAllowed({"CUSTOMER"})
    public ResponseEntity<Void> deleteCustomProduct(@PathVariable("customProductId") long customProductId) {
        AccessToken accessToken = requestAuthenticatedUserProvider.getAuthenticatedUserInRequest();
        if (accessToken == null || accessToken.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        customProductManager.deleteCustomProduct(customProductId, accessToken.getUserId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{customProductId}")
    @RolesAllowed({"CUSTOMER"})
    public ResponseEntity<Void> updateCustomProduct(@PathVariable("customProductId") long id, @RequestBody UpdateCustomTemplateRequest request){
        AccessToken accessToken = requestAuthenticatedUserProvider.getAuthenticatedUserInRequest();
        if (accessToken == null || accessToken.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        customProductManager.updateCustomProduct(id, request, accessToken.getUserId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
