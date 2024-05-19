package ptit.oop.assetmanagement.dtos.request;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Getter
@Builder
// check date of birth is after joined date
//@ScriptAssert(lang = "javascript", script = "_this.dob.before(_this.jointDate)",
//        message = "Joined date is not later than Date of Birth. Please select a different date")
public class CreateUserRequest {
    @Pattern(regexp = "^[a-zA-Z ]{1,50}$", message = "Only spaces and alphabetic characters are used in the first name. " +
            "The length of the first name ranges between one and fifty characters.")
    private String firstName;
    @Pattern(regexp = "^[a-zA-Z ]{1,50}$", message = "Only spaces and alphabetic characters are used in the last name. " +
            "The length of the last name ranges between one and fifty characters.")
    private String lastName;
    @Past()
    private LocalDate dob;
    private String gender;
    private LocalDate jointDate;
    private String type;
}
