package ptit.oop.assetmanagement.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserEntity extends BaseEntity{
    @Column(name = "first_name", length = 50)
    private String firstName;
    @Column(name = "last_name", length = 50)
    private String lastName;
    @Column(name = "dob")
    private LocalDate dob;
    @Column(name = "joint_date")
    private LocalDate jointDate;
    @Column(name = "gender", length = 50)
    private String gender;
    @Column(name = "type", length = 10)
    private String type;
    @Column(name = "staff_code", length = 6)
    private String staffCode;
    @Id
    private String username;
    private String password;
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    @Column(name = "enable")
    private boolean isEnable = true;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id")
    private LocationEntity location;
}
