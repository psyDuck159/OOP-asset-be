package ptit.oop.assetmanagement.dtos.response;

import ptit.oop.assetmanagement.dtos.UserDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class LoginResponse {
    private String accessToken;
    private UserDto userDto;
}
