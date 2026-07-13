package gr.unipi.cloudplatform.controller;

import gr.unipi.cloudplatform.dto.response.UserResponse;
import gr.unipi.cloudplatform.model.entity.User;
import gr.unipi.cloudplatform.repository.UserRepository;
import gr.unipi.cloudplatform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FA-U08 (Προβολή προφίλ), FA-U09 (Αναζήτηση - admin),
 * FA-U02 (Ενεργοποίηση/Απενεργοποίηση - admin), FA-U10 (Διαγραφή).
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@RequestAttribute("username") String username) {
        User user = (User) userService.loadUserByUsername(username);
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> listUsers() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{username}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> setStatus(@PathVariable String username,
                                                   @RequestParam boolean active) {
        User user = (User) userService.loadUserByUsername(username);
        User updated = userService.setActive(user.getId(), active);
        return ResponseEntity.ok(UserResponse.from(updated));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        User user = (User) userService.loadUserByUsername(username);
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }
}
