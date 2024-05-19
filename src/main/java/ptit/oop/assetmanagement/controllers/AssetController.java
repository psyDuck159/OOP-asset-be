package ptit.oop.assetmanagement.controllers;

import ptit.oop.assetmanagement.dtos.AssetDto;
import ptit.oop.assetmanagement.dtos.ResponseObject;
import ptit.oop.assetmanagement.dtos.request.CreateAssetDto;
import ptit.oop.assetmanagement.dtos.response.PageResponse;
import ptit.oop.assetmanagement.exceptions.BadRequestException;
import ptit.oop.assetmanagement.security.UserDetailsImpl;
import ptit.oop.assetmanagement.services.AssetService;
import ptit.oop.assetmanagement.services.CategoryService;
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

import java.io.Serializable;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/assets")
@PreAuthorize("hasAuthority('Admin')")
public class AssetController {
    private final Logger logger = LoggerFactory.getLogger(AssetController.class);
    private final AssetService assetService;
    private final CategoryService categoryService;

    public AssetController(AssetService assetService, CategoryService categoryService) {
        this.assetService = assetService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ResponseObject> getAssets(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "state", required = false) String state,
            @PageableDefault Pageable pageable
            ) {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            throw new AccessDeniedException("Unauthorized");
        }
        UserDetailsImpl admin = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("keyword={}, category={}, state={}", keyword, category, state);

        PageResponse<Serializable> page = (keyword != null || category != null || state != null)
                ? assetService.getWithFilterAndSearch(admin, keyword, category, state, pageable)
                : assetService.getAllDefault(admin, pageable);

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Get list of asset successfully")
                        .data(page)
                        .build()
        );
    }

    @GetMapping("/{assetCode}")
    public ResponseEntity<ResponseObject> getAsset(@PathVariable String assetCode) {
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message(String.format("Get asset details with asset code = %s successfully", assetCode))
                        .data(assetService.getAssetDetails(assetCode))
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ResponseObject> createAsset(@RequestBody CreateAssetDto asset) {
        if (!this.categoryService.checkExistenceByPrefix(asset.getCategory())) {
            throw new BadRequestException("Category is not existed!");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication)) {
            throw new AccessDeniedException("Unauthorized");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseObject.builder()
                    .status(HttpStatus.CREATED)
                    .message("New asset created successfully!")
                    .data(this.assetService.create(asset, userDetails))
                    .build()
            );
    }

    @PutMapping("/{assetCode}")
    public ResponseEntity<ResponseObject> updateAsset(@PathVariable String assetCode, @RequestBody AssetDto assetDto) {
        return null;
    }

    @DeleteMapping("/{assetCode}")
    public ResponseEntity<ResponseObject> deleteAsset(@PathVariable String assetCode) {
        return null;
    }

}
