package ptit.oop.assetmanagement.controllers;

import ptit.oop.assetmanagement.dtos.AssignmentDto;
import ptit.oop.assetmanagement.dtos.ResponseObject;
import ptit.oop.assetmanagement.dtos.request.CreateAssignmentDto;
import ptit.oop.assetmanagement.dtos.request.ResponseAssignmentRequest;
import ptit.oop.assetmanagement.security.UserDetailsImpl;
import ptit.oop.assetmanagement.services.AssetService;
import ptit.oop.assetmanagement.services.AssignmentService;
import ptit.oop.assetmanagement.services.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/assignments")
@PreAuthorize("hasAuthority('Admin')")
public class AssignmentController {
    private final AssignmentService assignmentService;
    private final AssetService assetService;
    private final UserService userService;

    public AssignmentController(AssignmentService assignmentService, AssetService assetService, UserService userService) {
        this.assignmentService = assignmentService;
        this.assetService = assetService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public ResponseEntity<ResponseObject> getAssignments(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "assignedDate", required = false) String assignedDate,
            @PageableDefault Pageable pageable
    ) {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            throw new AccessDeniedException("Unauthorized");
        }
        UserDetailsImpl admin = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Get assignment list successfully!")
                        .data(assignmentService.getWithSearchAndFilter(admin, keyword, state, assignedDate, pageable))
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public ResponseEntity<ResponseObject> getAssignment(@PathVariable Integer id) {
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message(String.format("Get assignment with id=%d successfully", id))
                        .data(assignmentService.getAssignmentDetails(id))
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ResponseObject> createAssignment(@RequestBody CreateAssignmentDto assignment) {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            throw new AccessDeniedException("Unauthorized");
        }
        UserDetailsImpl admin = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseObject.builder()
                        .status(HttpStatus.CREATED)
                        .message("Created new assignment successfully!")
                        .data(this.assignmentService.create(assignment, admin))
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateAssignment(@PathVariable Integer id, @RequestBody AssignmentDto assignmentDto) {
        return null;
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public ResponseEntity<ResponseObject> respondAssignment(@PathVariable Integer id, @RequestBody ResponseAssignmentRequest responseAssignmentRequest) {
        Authentication authentication = SecurityContextHolder
            .getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            throw new AccessDeniedException("Unauthorized");
        }
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(
            ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Respond to assignment successfully!")
                .data(this.assignmentService.respond(id, responseAssignmentRequest.getState(), user))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteAssignment(@PathVariable Integer id) {
        return null;
    }
}
