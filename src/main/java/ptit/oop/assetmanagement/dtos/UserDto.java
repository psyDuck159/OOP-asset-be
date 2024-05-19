package ptit.oop.assetmanagement.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@Builder
public class UserDto implements Serializable {
    private String username;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private LocalDate jointDate;
    private LocalDateTime lastLogin;
    private String gender;
    private String type;
    private String staffCode;
    private LocationDto location;
    private Boolean isEnable;
}
