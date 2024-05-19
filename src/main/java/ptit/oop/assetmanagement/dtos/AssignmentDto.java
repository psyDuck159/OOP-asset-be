package ptit.oop.assetmanagement.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Builder
@Getter @Setter
public class AssignmentDto implements Serializable {
    private Integer id;
    private String state;
    private LocalDate assignedDate;
    private String note;
    private UserDto assignedTo;
    private UserDto assignedBy;
    private AssetDto asset;
    private LocalDate returnedDate;
    private List<ReturningRequestDto> returningRequests;
}
