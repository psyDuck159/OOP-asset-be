package ptit.oop.assetmanagement.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
@Builder
public class ReturningRequestDto implements Serializable {
	private Integer id;
	private AssignmentDto assignment;
	private UserDto requestedBy;
	private UserDto acceptedBy;
	private String state;
}
