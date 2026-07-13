package gr.unipi.cloudplatform.service;

import gr.unipi.cloudplatform.model.entity.User;
import gr.unipi.cloudplatform.model.enums.UserRole;
import gr.unipi.cloudplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * FA-U01: Εγγραφή νέου χρήστη. Ο κωδικός κατακερματίζεται με BCrypt (strength 12).
     * Ο νέος λογαριασμός απαιτεί ενεργοποίηση από διαχειριστή πριν χρησιμοποιηθεί.
     */
    public User register(String username, String fullName, String email, String rawPassword,
                          String securityQuestion, String securityAnswer) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .username(username)
                .fullName(fullName)
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .securityQuestion(securityQuestion)
                .securityAnswerHash(passwordEncoder.encode(securityAnswer))
                .role(UserRole.REGULAR)
                .isActive(false)  // απαιτεί ενεργοποίηση από admin
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    /** FA-U02: Ο διαχειριστής ενεργοποιεί/απενεργοποιεί λογαριασμούς. */
    public User setActive(String userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setActive(active);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}
