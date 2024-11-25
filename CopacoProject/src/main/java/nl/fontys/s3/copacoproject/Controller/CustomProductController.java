package nl.fontys.s3.copacoproject.Controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.CustomProductManager;
import nl.fontys.s3.copacoproject.business.dto.customProductDto.CreateCustomProductRequest;
import nl.fontys.s3.copacoproject.business.dto.customProductDto.CreateCustomProductResponse;
import nl.fontys.s3.copacoproject.configuration.security.auth.RequestAuthenticatedUserProvider;
import nl.fontys.s3.copacoproject.configuration.security.token.AccessToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

        CreateCustomProductResponse response = customProductManager.CreateCustomProduct(request, accessToken.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
