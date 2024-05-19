package ptit.oop.assetmanagement.mappers;

import ptit.oop.assetmanagement.dtos.AssetDto;
import ptit.oop.assetmanagement.dtos.AssignmentDto;
import ptit.oop.assetmanagement.dtos.ReturningRequestDto;
import ptit.oop.assetmanagement.dtos.UserDto;
import ptit.oop.assetmanagement.entities.ReturningRequestEntity;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class ReturningRequestMapper {
	private ReturningRequestMapper() {}

	public static ReturningRequestDto toDto(ReturningRequestEntity entity) {
		return ReturningRequestDto.builder()
				.assignment(AssignmentDto.builder()
						.id(entity.getAssignment().getId())
						.asset(AssetDto.builder()
								.assetCode(entity.getAssignment().getAsset().getAssetCode())
								.name(entity.getAssignment().getAsset().getName())
								.build())
						.returnedDate(entity.getAssignment().getReturnedDate())
						.assignedDate(entity.getAssignment().getAssignedDate())
						.build())
				.requestedBy(UserDto.builder()
						.username(entity.getRequestedBy().getUsername())
						.build())
				.acceptedBy(UserDto.builder()
						.username(entity.getAcceptedBy() == null ? null : entity.getAcceptedBy().getUsername())
						.build())
				.id(entity.getId())
				.state(entity.getState())
				.build();
	}

	public static List<Serializable> toDtoList(List<ReturningRequestEntity> entities) {
		return entities.stream().map(ReturningRequestMapper::toDto).collect(Collectors.toList());
	}
}
