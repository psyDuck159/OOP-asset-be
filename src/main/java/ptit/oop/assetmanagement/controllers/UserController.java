package ptit.oop.assetmanagement.controllers;

import ptit.oop.assetmanagement.dtos.ResponseObject;
import ptit.oop.assetmanagement.dtos.UserDto;
import ptit.oop.assetmanagement.dtos.request.CreateUserRequest;
import ptit.oop.assetmanagement.dtos.response.PageResponse;
import ptit.oop.assetmanagement.entities.UserEntity;
import ptit.oop.assetmanagement.exceptions.BadRequestException;
import ptit.oop.assetmanagement.mappers.UserMapper;
import ptit.oop.assetmanagement.security.UserDetailsImpl;
import ptit.oop.assetmanagement.services.AssignmentService;
import ptit.oop.assetmanagement.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;

@RestController
@RequestMapping("api/v1/users")
@PreAuthorize("hasAuthority('Admin')")
@Validated
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final AssignmentService assignmentService;

    public UserController(UserService userService, AssignmentService assignmentService) {
        this.userService = userService;
        this.assignmentService = assignmentService;
    }

    @Value("${staff.minAge}")
    private int minAge;

    @GetMapping
    public ResponseEntity<ResponseObject> getUsers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "type", required = false) String type,
            @PageableDefault Pageable pageable
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            throw new AccessDeniedException("Unauthorized");
        }
        UserDetailsImpl admin = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Get list of user successfully!")
                        .data(this.userService.getAll(admin, keyword, type, pageable))
                        .build()
        );
    }

    @GetMapping("/{username}")
    public ResponseEntity<ResponseObject> getUser(@PathVariable String username) {
        UserDto response = this.userService.getByUsername(username);
        if (response != null) {
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Get user with username=" + username + " successfully!")
                            .data(response)
                            .build()
            );
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    @GetMapping("/{username}/assignments")
    public ResponseEntity<ResponseObject> getUserAssignments(@PathVariable String username, @PageableDefault Pageable pageable) {
        PageResponse<Serializable> response = assignmentService.getMyAssignments(username, pageable);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Get assignment list of " + username + " successfully!")
                        .data(response)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ResponseObject> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        logger.info("CreateRequest: dob = {}, jointDate = {}", createUserRequest.getDob(), createUserRequest.getJointDate());
        LocalDate dob = createUserRequest.getDob();
        LocalDate jointDate = createUserRequest.getJointDate();
        logger.info("Local var: dob={}, jointDate={}", dob, jointDate);

        if (dob.isAfter(LocalDate.now().minusYears(minAge))) {
            throw new BadRequestException(String.format("User is under %d. Please select a different date", minAge));
        }
        if (dob.isAfter(jointDate)) {
            throw new BadRequestException("Joined date is not later than Date of Birth. Please select a different date");
        }
        if (jointDate.getDayOfWeek() == DayOfWeek.SATURDAY || jointDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new BadRequestException("Joined date is Saturday or Sunday. Please select a different date");
        }
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            throw new AccessDeniedException("Unauthorized");
        }
        UserEntity newUser = UserMapper.toEntity(createUserRequest);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity
                .ok(ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("User is created successfully")
                        .data(userService.create(newUser, userDetails))
                        .build());
    }

    @PutMapping("/{username}")
    public ResponseEntity<ResponseObject> updateUser(@PathVariable String username, @RequestBody CreateUserRequest updatedUserRequest) {
        LocalDate dob = updatedUserRequest.getDob();
        LocalDate jointDate = updatedUserRequest.getJointDate();
        if (dob.isAfter(LocalDate.now().minusYears(minAge))) {
            throw new BadRequestException(String.format("User is under %d. Please select a different date", minAge));
        }
        if (dob.isAfter(jointDate)) {
            throw new BadRequestException("Joined date is not later than Date of Birth. Please select a different date");
        }
        if (jointDate.getDayOfWeek() == DayOfWeek.SATURDAY || jointDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new BadRequestException("Joined date is Saturday or Sunday. Please select a different date");
        }
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            throw new AccessDeniedException("Unauthorized");
        }
        UserEntity updatedUser = UserMapper.toEntity(updatedUserRequest);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity
                .ok(ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("User is updated successfully")
                        .data(userService.update(username, updatedUser, userDetails))
                        .build());
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<ResponseObject> disableUser(@PathVariable String username) {
        return null;
    }
}
