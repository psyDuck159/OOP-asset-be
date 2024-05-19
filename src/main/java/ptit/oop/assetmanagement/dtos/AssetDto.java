package ptit.oop.assetmanagement.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Builder
@Getter @Setter
public class AssetDto implements Serializable {
    private String assetCode;
    private String name;
    private String specification;
    private LocalDate installedDate;
    private String state;
    private CategoryDto category;
    private LocationDto location;
    private List<AssignmentDto> assignments;
}
