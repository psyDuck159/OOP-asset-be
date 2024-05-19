package ptit.oop.assetmanagement.services;

import ptit.oop.assetmanagement.repositories.ReturningRequestRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReturningRequestServiceTests {
    @Mock
    private ReturningRequestRepository returningRequestRepository;
    @InjectMocks
    private ReturningRequestService returningRequestService;
}
