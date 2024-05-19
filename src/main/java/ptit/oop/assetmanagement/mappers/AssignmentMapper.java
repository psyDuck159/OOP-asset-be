package ptit.oop.assetmanagement.mappers;

import ptit.oop.assetmanagement.dtos.AssetDto;
import ptit.oop.assetmanagement.dtos.AssignmentDto;
import ptit.oop.assetmanagement.dtos.ReturningRequestDto;
import ptit.oop.assetmanagement.dtos.UserDto;
import ptit.oop.assetmanagement.entities.AssignmentEntity;
import ptit.oop.assetmanagement.entities.ReturningRequestEntity;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class AssignmentMapper {
	private AssignmentMapper() {
	}

	public static AssignmentDto toBriefDto(AssignmentEntity entity) {
		return AssignmentDto.builder()
				.id(entity.getId())
				.assignedTo(UserDto.builder()
						.firstName(entity.getAssignedTo().getFirstName())
						.lastName(entity.getAssignedTo().getLastName())
						.username(entity.getAssignedTo().getUsername())
						.build())
				.assignedDate(entity.getAssignedDate())
				.returnedDate(entity.getReturnedDate())
				.build();
	}

	public static List<AssignmentDto> toAssignmentHistory(List<AssignmentEntity> entities) {
		return entities.stream().map(AssignmentMapper::toBriefDto).collect(Collectors.toList());
	}

	public static AssignmentDto toDto(AssignmentEntity entity) {
		List<ReturningRequestEntity> requestEntities = entity.getReturningRequests();
		return AssignmentDto.builder()
				.id(entity.getId())
				.asset(AssetDto.builder()
						.assetCode(entity.getAsset().getAssetCode())
						.name(entity.getAsset().getName())
						.build())
				.assignedTo(UserDto.builder()
						.username(entity.getAssignedTo().getUsername())
						.build())
				.assignedBy(UserDto.builder()
						.username(entity.getAssignedBy().getUsername())
						.build())
				.assignedDate(entity.getAssignedDate())
				.state(entity.getState())
				.returningRequests(requestEntities == null ? null :
						requestEntities
						.stream().map(request -> ReturningRequestDto.builder()
								.id(request.getId())
								.state(request.getState()).build())
						.collect(Collectors.toList()))
				.build();
	}

	public static List<Serializable> toDtoList(List<AssignmentEntity> entities) {
		return entities.stream().map(AssignmentMapper::toDto).collect(Collectors.toList());
	}

	public static AssignmentDto toDetailDto(AssignmentEntity entity) {
		return AssignmentDto.builder()
				.id(entity.getId())
				.asset(AssetDto.builder()
						.assetCode(entity.getAsset().getAssetCode())
						.name(entity.getAsset().getName())
						.specification(entity.getAsset().getSpecification())
						.build())
				.assignedTo(UserDto.builder()
						.username(entity.getAssignedTo().getUsername())
						.build())
				.assignedBy(UserDto.builder()
						.username(entity.getAssignedBy().getUsername())
						.build())
				.assignedDate(entity.getAssignedDate())
				.state(entity.getState())
				.note(entity.getNote())
				.build();
	}
}
