package nl.fontys.s3.copacoproject.Controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.CustomProductManager;
import nl.fontys.s3.copacoproject.business.dto.customProductDto.CreateCustomProductRequest;
import nl.fontys.s3.copacoproject.business.dto.customProductDto.CreateCustomProductResponse;
import nl.fontys.s3.copacoproject.business.dto.customProductDto.CustomProductResponse;
import nl.fontys.s3.copacoproject.business.dto.customProductDto.GetCustomProductsByUserAndStatusRequest;
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
    public ResponseEntity<List<CustomProductResponse>> getCustomProductsOfUserByState(@PathVariable("userId") long userId, @RequestParam("statusId") @Positive int statusId) {
        AccessToken accessToken = requestAuthenticatedUserProvider.getAuthenticatedUserInRequest();

        if (accessToken == null || accessToken.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        GetCustomProductsByUserAndStatusRequest request = new GetCustomProductsByUserAndStatusRequest();
        request.setStatusId(statusId);

        List<CustomProductResponse> response = customProductManager.getCustomProductsOfUserByState(userId, accessToken.getUserId(), request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
