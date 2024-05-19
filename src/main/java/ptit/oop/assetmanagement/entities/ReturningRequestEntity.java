package ptit.oop.assetmanagement.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "returning_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturningRequestEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String state;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assignment_id")
    private AssignmentEntity assignment;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requested_by")
    private UserEntity requestedBy;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "accepted_by")
    private UserEntity acceptedBy;
}
