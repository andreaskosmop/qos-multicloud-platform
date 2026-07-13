package gr.unipi.cloudplatform.dto.response;

public record AuthResponse(
        String token,
        String username,
        String role
) {}
