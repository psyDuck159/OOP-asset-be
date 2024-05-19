package ptit.oop.assetmanagement.utils;

import ptit.oop.assetmanagement.entities.AssetEntity;
import ptit.oop.assetmanagement.entities.UserEntity;
import ptit.oop.assetmanagement.repositories.AssetRepository;
import ptit.oop.assetmanagement.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

@Component
public class GeneratingStringUtils {
    private final Logger logger = LoggerFactory.getLogger(GeneratingStringUtils.class);
    @Value("${staff.code.prefix}")
    private String staffCodePrefix;
    @Value("${staff.code.number.length}")
    private String staffCodeNumberLength;

    @Value("6")
    private String assetCodeNumberLength;

    private final UserRepository userRepository;
    private final AssetRepository assetRepository;

    public GeneratingStringUtils(UserRepository userRepository, AssetRepository assetRepository) {
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
    }

    public String generateStaffCode() {
        Optional<UserEntity> optUser = userRepository.findFirstByOrderByStaffCodeDesc();
        String number = optUser.isPresent()
                ? optUser.get().getStaffCode().replace(staffCodePrefix, "")
                : "0";
        int max = Integer.parseInt(number);
        String formatCode = "%0" + staffCodeNumberLength + "d";
        return staffCodePrefix + String.format(formatCode, max + 1); //"SD" + ..(%04d, 78) == SD0078
    }

    public String generateUsername(String firstName, String lastName) {
        String username = convertToUsername(firstName, lastName);

        Long numberUname = userRepository.countByUsernameStartsWith(username);
        logger.info("{} username starts with {}", numberUname, username);
        return username + (numberUname == 0 ? "" : numberUname);
    }

    public String convertToUsername(String firstName, String lastName) {
        StringBuilder usernameBuilder =
                new StringBuilder(firstName.trim().replaceAll("\\s+", "").toLowerCase());
        Arrays.stream(lastName.trim().split("\\s+"))
                .map(String::toLowerCase)
                .forEach(word -> usernameBuilder.append(word.charAt(0)));
        return usernameBuilder.toString();
    }

    public String generatePassword(String username, LocalDate dob) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        return String.format("%s@%s", username, dob.format(formatter));
    }

    public String generateAssetCode(String prefix) {
        AssetEntity entity = this.assetRepository.findFirstByAssetCodeContainsOrderByAssetCodeDesc(prefix);

        String number = entity != null ? entity.getAssetCode().replaceAll("[^0-9]", "") : "0";

        return prefix + String.format("%0" + assetCodeNumberLength + "d", Integer.parseInt(number) + 1);
    }
}
