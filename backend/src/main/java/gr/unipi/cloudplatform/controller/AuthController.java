package gr.unipi.cloudplatform.controller;

import gr.unipi.cloudplatform.dto.request.LoginRequest;
import gr.unipi.cloudplatform.dto.request.RegisterRequest;
import gr.unipi.cloudplatform.dto.response.AuthResponse;
import gr.unipi.cloudplatform.model.entity.User;
import gr.unipi.cloudplatform.security.JwtUtils;
import gr.unipi.cloudplatform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * FA-U01 (Εγγραφή), FA-U03 (Σύνδεση).
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        User user = userService.register(req.username(), req.fullName(), req.email(),
                req.password(), req.securityQuestion(), req.securityAnswer());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new gr.unipi.cloudplatform.dto.response.UserResponse(
                        user.getId(), user.getUsername(), user.getFullName(),
                        user.getEmail(), user.getRole().name(), user.isActive()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));

        UserDetails userDetails = userService.loadUserByUsername(req.username());
        String token = jwtUtils.generateToken(userDetails);
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return ResponseEntity.ok(new AuthResponse(token, req.username(), role));
    }
}
