package ptit.oop.assetmanagement.services;

import ptit.oop.assetmanagement.config.SystemStates;
import ptit.oop.assetmanagement.dtos.AssignmentDto;
import ptit.oop.assetmanagement.dtos.request.CreateAssignmentDto;
import ptit.oop.assetmanagement.dtos.response.PageResponse;
import ptit.oop.assetmanagement.entities.AssetEntity;
import ptit.oop.assetmanagement.entities.AssignmentEntity;
import ptit.oop.assetmanagement.entities.UserEntity;
import ptit.oop.assetmanagement.exceptions.BadRequestException;
import ptit.oop.assetmanagement.exceptions.NotFoundException;
import ptit.oop.assetmanagement.mappers.AssignmentMapper;
import ptit.oop.assetmanagement.repositories.AssetRepository;
import ptit.oop.assetmanagement.repositories.AssignmentRepository;
import ptit.oop.assetmanagement.repositories.UserRepository;
import ptit.oop.assetmanagement.security.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AssignmentService {
    private final Logger logger = LoggerFactory.getLogger(AssignmentService.class);
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;

    public AssignmentService(AssignmentRepository assignmentRepository, UserRepository userRepository, AssetRepository assetRepository) {
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
    }


    public PageResponse<Serializable> getWithSearchAndFilter(
            UserDetailsImpl admin, String keyword, String state, String assignedDate, Pageable pageable
    ) {
        keyword = Objects.isNull(keyword) ? "" : keyword;
        assignedDate = Objects.isNull(assignedDate) ? "" : assignedDate;

        List<String> states = Objects.isNull(state)
                ? Arrays.asList(SystemStates.AssignmentStates.WAITING_FOR_ACCEPTANCE.getState(),
                    SystemStates.AssignmentStates.ACCEPTED.getState(),
                    SystemStates.AssignmentStates.DECLINED.getState())
                : Arrays.stream(state.split(",")).collect(Collectors.toList());
        logger.info("keyword={}, state={}, assignedDate={}", keyword, state, assignedDate);
        Page<AssignmentEntity> page = assignmentRepository.getAssignmentsWithFilterAndSearch(
                admin.getLocationEntity().getId(),
                keyword,
                states,
                assignedDate,
                pageable);
        return PageResponse.builder()
                .currentPage(page.getNumber())
                .totalItems((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .contents(AssignmentMapper.toDtoList(page.getContent()))
                .build();
    }

    public AssignmentDto getAssignmentDetails(Integer id) {
        logger.info("assignment id={}", id);
        AssignmentEntity entity = assignmentRepository.getAssignmentDetails(id)
                .orElseThrow(() -> new NotFoundException(String.format("Cannot find assignment with id=%d", id)));
        return AssignmentMapper.toDetailDto(entity);
    }

    public PageResponse<Serializable> getMyAssignments(String assigneeUsername, Pageable pageable) {
        Page<AssignmentEntity> page = assignmentRepository.getMyAssignments(assigneeUsername, pageable);
        return PageResponse.builder()
                .currentPage(page.getNumber())
                .totalItems((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .contents(AssignmentMapper.toDtoList(page.getContent()))
                .build();
    }

    @Transactional
    public AssignmentDto create(CreateAssignmentDto assignment, UserDetailsImpl admin) {
        if (assignment.getAssignedDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Can not create an assignment in the past. Select another date");
        }

        UserEntity userEntity = this.userRepository.findById(assignment.getAssignedTo()).orElseThrow(NotFoundException::new);

        if (!userEntity.isEnable()) {
            throw new BadRequestException("Can not assign a disabled user");
        }

        AssetEntity assetEntity = this.assetRepository.findById(assignment.getAssetCode()).orElseThrow(NotFoundException::new);

        if (!assetEntity.getState().equals(SystemStates.AssetStates.AVAILABLE.getState())) {
            throw new BadRequestException("Can not assign an unavailable asset");
        }

        assetEntity.setState(SystemStates.AssetStates.ASSIGNED.getState());

        AssignmentEntity entity = AssignmentEntity.builder()
                .state(SystemStates.AssignmentStates.WAITING_FOR_ACCEPTANCE.getState())
                .assignedTo(userEntity)
                .assignedBy(this.userRepository.findById(admin.getUsername()).orElseThrow(NotFoundException::new))
                .assignedDate(assignment.getAssignedDate())
                .asset(assetEntity)
                .note(assignment.getNote())
                .build();

        entity.setCreatedBy(admin.getUsername());
        entity.setUpdatedAt(null);
        entity.setUpdatedBy(null);

        try {
            this.assetRepository.save(assetEntity);
            return AssignmentMapper.toDto(this.assignmentRepository.save(entity));
        } catch (NullPointerException e) {
            throw Objects.nonNull(e.getMessage()) ? new BadRequestException(e.getMessage()) : new BadRequestException(e);
        }
    }

    @Transactional
    public AssignmentDto respond(Integer id, String state, UserDetailsImpl user) {
        AssignmentEntity assignmentEntity = this.assignmentRepository.getAssignmentDetails(id).orElseThrow(NotFoundException::new);

        if (!assignmentEntity.getAssignedTo().getUsername().equalsIgnoreCase(user.getUsername())) {
            throw new AccessDeniedException("Forbidden");
        }

        if (!assignmentEntity.getState().equals(SystemStates.AssignmentStates.WAITING_FOR_ACCEPTANCE.getState())) {
            throw new BadRequestException("You can only respond to an assignment which is waiting for acceptance.");
        }

        if (!state.equals(SystemStates.AssignmentStates.ACCEPTED.getState())
                &&
                !state.equals(SystemStates.AssignmentStates.DECLINED.getState())) {
            throw new BadRequestException("Invalid state");
        }

        AssetEntity assetEntity = assignmentEntity.getAsset();
        assignmentEntity.setState(state);

        try {
            if (state.equals(SystemStates.AssignmentStates.DECLINED.getState())) {
                assetEntity.setState(SystemStates.AssetStates.AVAILABLE.getState());
                this.assetRepository.save(assetEntity);
            }

            return AssignmentMapper.toDto(this.assignmentRepository.save(assignmentEntity));
        } catch (NullPointerException e) {
            throw Objects.nonNull(e.getMessage()) ? new BadRequestException(e.getMessage()) : new BadRequestException(e);
        }
    }
}
