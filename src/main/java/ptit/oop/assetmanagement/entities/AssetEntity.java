package ptit.oop.assetmanagement.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "assets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetEntity extends BaseEntity{
    @Id
    @Column(name = "asset_code", length = 8)
    private String assetCode;
    private String name;
    @Column(length = 500)
    private String specification;
    @Column(name = "installed_date")
    private LocalDate installedDate;
    private String state;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id")
    private LocationEntity location;
    @OneToMany(mappedBy = "asset", fetch = FetchType.LAZY)
    private List<AssignmentEntity> assignments;
}
