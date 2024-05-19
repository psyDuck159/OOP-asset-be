package ptit.oop.assetmanagement.services;

import ptit.oop.assetmanagement.repositories.AssetRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AssetServiceTests {
    @Mock
    private AssetRepository assetRepository;
    @InjectMocks
    private AssetService assetService;
}
