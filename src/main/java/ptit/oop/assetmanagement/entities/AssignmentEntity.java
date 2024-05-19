package ptit.oop.assetmanagement.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "assignments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String state;
    @Column(name = "assigned_date")
    private LocalDate assignedDate;
    @Column(name = "returned_date")
    private LocalDate returnedDate;
    @Column(length = 500)
    private String note;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_to")
    private UserEntity assignedTo;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_by")
    private UserEntity assignedBy;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset")
    private AssetEntity asset;
    @OneToMany(mappedBy = "assignment", fetch = FetchType.LAZY)
    private List<ReturningRequestEntity> returningRequests;
}
