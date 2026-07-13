package gr.unipi.cloudplatform.dto.response;

import gr.unipi.cloudplatform.model.entity.User;

public record UserResponse(
        String id,
        String username,
        String fullName,
        String email,
        String role,
        boolean isActive
) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getFullName(),
                u.getEmail(), u.getRole().name(), u.isActive());
    }
}
