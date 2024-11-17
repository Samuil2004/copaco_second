package nl.fontys.s3.copacoproject.Controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.UserManager;
import nl.fontys.s3.copacoproject.business.dto.auth.AuthRequest;
import nl.fontys.s3.copacoproject.business.dto.auth.AuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final UserManager userManager;

    @PostMapping()
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest loginRequest) {
        AuthResponse loginResponse = userManager.login(loginRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Content-Type", "application/json")
                .body(loginResponse);
    }
}
