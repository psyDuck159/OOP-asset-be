package ptit.oop.assetmanagement.services;

import ptit.oop.assetmanagement.repositories.AssignmentRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AssignmentServiceTests {
    @Mock
    private AssignmentRepository assignmentRepository;
    @InjectMocks
    private AssignmentService assignmentService;
}
