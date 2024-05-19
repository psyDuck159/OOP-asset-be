package ptit.oop.assetmanagement.services;

import ptit.oop.assetmanagement.repositories.UserRepository;
import ptit.oop.assetmanagement.utils.GeneratingStringUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private GeneratingStringUtils generatingStringUtils;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SecurityContext securityContext;
    @InjectMocks
    private UserService userService;

}
