package gr.unipi.cloudplatform.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 20) String username,
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 10)
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$",
                message = "Password must contain uppercase, lowercase, digit and special character")
        String password,
        @NotBlank String securityQuestion,
        @NotBlank String securityAnswer
) {}
