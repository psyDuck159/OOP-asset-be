package ptit.oop.assetmanagement.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter @Setter
@Builder
public class CreateAssignmentDto {
	private String assignedTo;
	private String assetCode;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@FutureOrPresent
	private LocalDate assignedDate;
	private String note;
}
