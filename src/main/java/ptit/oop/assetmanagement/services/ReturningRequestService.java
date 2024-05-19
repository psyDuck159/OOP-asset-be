package ptit.oop.assetmanagement.services;

import ptit.oop.assetmanagement.config.SystemStates;
import ptit.oop.assetmanagement.dtos.response.PageResponse;
import ptit.oop.assetmanagement.entities.AssetEntity;
import ptit.oop.assetmanagement.entities.AssignmentEntity;
import ptit.oop.assetmanagement.entities.UserEntity;
import ptit.oop.assetmanagement.exceptions.BadRequestException;
import ptit.oop.assetmanagement.exceptions.NotFoundException;
import ptit.oop.assetmanagement.mappers.ReturningRequestMapper;
import ptit.oop.assetmanagement.repositories.AssetRepository;
import ptit.oop.assetmanagement.repositories.AssignmentRepository;
import ptit.oop.assetmanagement.repositories.ReturningRequestRepository;
import ptit.oop.assetmanagement.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ptit.oop.assetmanagement.entities.ReturningRequestEntity;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ReturningRequestService {
	private final ReturningRequestRepository requestRepository;
	private final AssignmentRepository assignmentRepository;
	private final AssetRepository assetRepository;

	public ReturningRequestService(
			ReturningRequestRepository requestRepository,
			AssignmentRepository assignmentRepository, AssetRepository assetRepository
	) {
		this.requestRepository = requestRepository;
		this.assignmentRepository = assignmentRepository;
		this.assetRepository = assetRepository;
	}

	@Transactional
	public Object create(Integer assignmentId, UserDetailsImpl userDetails) {
		AssignmentEntity assignment = assignmentRepository.getAssignmentDetails(assignmentId)
				.orElseThrow(() -> new NotFoundException("Can not find assignment id = " + assignmentId));
		if (!assignment.getState().equals(SystemStates.AssignmentStates.ACCEPTED.getState())) {
			throw new BadRequestException("This assignment has not accepted");
		}

		assignment.setState(SystemStates.AssignmentStates.RETURNING.getState());
		assignmentRepository.save(assignment);

		ReturningRequestEntity request = new ReturningRequestEntity();
		request.setAssignment(assignment);
		request.setRequestedBy(UserEntity.builder().username(userDetails.getUsername()).build());
		request.setState(SystemStates.ReturnStates.WAITING_FOR_RETURNING.getState());

		try {
			return ReturningRequestMapper.toDto(this.requestRepository.save(request));
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw Objects.nonNull(e.getMessage()) ? new BadRequestException(e.getMessage()) : new BadRequestException(e);
		}
	}

	public PageResponse<Serializable> getAll(UserDetailsImpl admin, String keyword, String state, String date, Pageable pageable) {
		keyword = Objects.isNull(keyword) ? "" : keyword;
		date = Objects.isNull(date) ? "" : date;
		List<String> states = Objects.isNull(state)
				? Arrays.asList(SystemStates.ReturnStates.WAITING_FOR_RETURNING.getState(),
				SystemStates.ReturnStates.COMPLETED.getState())
				: Arrays.stream(state.split(",")).collect(Collectors.toList());

		Page<ReturningRequestEntity> page = this.requestRepository.getAll(
				admin.getLocationEntity().getId(),
				keyword,
				states,
				date,
				pageable);
		return PageResponse.builder()
				.currentPage(page.getNumber())
				.totalItems((int) page.getTotalElements())
				.totalPages(page.getTotalPages())
				.contents(ReturningRequestMapper.toDtoList(page.getContent()))
				.build();
	}

	@Transactional
	public Object respond(Integer id, String state, UserDetailsImpl user) {
		ReturningRequestEntity returningRequestEntity = requestRepository.findById(id).orElseThrow(NotFoundException::new);
		AssignmentEntity assignmentEntity = returningRequestEntity.getAssignment();

		if (!SystemStates.ReturnStates.WAITING_FOR_RETURNING.getState().equals(returningRequestEntity.getState())) {
			throw new BadRequestException("You can only respond to an returning request which is waiting for returning.");
		}
		if (!SystemStates.ReturnStates.COMPLETED.getState().equals(state)
				&& !SystemStates.ReturnStates.CANCELED.getState().equals(state)) {
			throw new BadRequestException("Invalid state");
		}
		// change state
		if (SystemStates.ReturnStates.COMPLETED.getState().equals(state)) {
			assignmentEntity.setState(SystemStates.AssignmentStates.CLOSED.getState());
			assignmentEntity.setReturnedDate(LocalDate.now());
			assignmentRepository.save(assignmentEntity);
			AssetEntity asset = assignmentEntity.getAsset();
			asset.setState(SystemStates.AssetStates.AVAILABLE.getState());
			assetRepository.save(asset);
		} else {
			assignmentEntity.setState(SystemStates.AssignmentStates.ACCEPTED.getState());
			assignmentRepository.save(assignmentEntity);
		}
		returningRequestEntity.setState(state);
		returningRequestEntity.setAcceptedBy(UserEntity.builder().username(user.getUsername()).build());
		ReturningRequestEntity save = requestRepository.save(returningRequestEntity);
		return ReturningRequestMapper.toDto(save);
	}
}
