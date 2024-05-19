package ptit.oop.assetmanagement.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class AssetRepositoryTests {
    @Autowired
    private AssetRepository assetRepository;
}
