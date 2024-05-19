package ptit.oop.assetmanagement.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Constants {
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String JWT_HEADER = "Authorization";

    public static final String DATE_FORMAT = "dd-MM-yyyy";

    public static final String DATE_TIME_FORMAT ="HH:mm:ss dd-MM-yyyy";

    public static final String PASSWORD_REGEX = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,15}$";
}
