package ptit.oop.assetmanagement.controllers;

import ptit.oop.assetmanagement.dtos.ReturningRequestDto;
import ptit.oop.assetmanagement.dtos.ResponseObject;
import ptit.oop.assetmanagement.dtos.request.ResponseReturningRequest;
import ptit.oop.assetmanagement.security.UserDetailsImpl;
import ptit.oop.assetmanagement.services.ReturningRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/api/v1/returnings")
@PreAuthorize("hasAuthority('Admin')")
public class ReturningRequestController {
    private final Logger logger = LoggerFactory.getLogger(ReturningRequestController.class);
    private final ReturningRequestService returnService;

    public ReturningRequestController(ReturningRequestService returnService) {
        this.returnService = returnService;
    }

    @GetMapping
    public ResponseEntity<ResponseObject> getReturningRequests(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "date", required = false) String date,
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
                .message("Get list of returning requests successfully")
                .data(this.returnService.getAll(admin, keyword, state, date, pageable))
                .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getReturningRequest(@PathVariable Integer id) {
        return null;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public ResponseEntity<ResponseObject> createReturningRequest(@RequestBody ReturningRequestDto returningRequestDto) {
        Authentication authentication = SecurityContextHolder
            .getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            throw new AccessDeniedException("Unauthorized");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity
            .ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("User is created successfully")
                .data(returnService.create(returningRequestDto.getAssignment().getId(), userDetails))
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseObject> respondReturningRequest(@PathVariable Integer id, @RequestBody ResponseReturningRequest responseReturningRequest) {
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
                        .data(this.returnService.respond(id, responseReturningRequest.getState(), user))
                        .build());
    }
}
