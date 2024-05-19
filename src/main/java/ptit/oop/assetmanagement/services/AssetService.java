package ptit.oop.assetmanagement.services;

import ptit.oop.assetmanagement.config.SystemStates;
import ptit.oop.assetmanagement.dtos.AssetDto;
import ptit.oop.assetmanagement.dtos.request.CreateAssetDto;
import ptit.oop.assetmanagement.dtos.response.PageResponse;
import ptit.oop.assetmanagement.entities.AssetEntity;
import ptit.oop.assetmanagement.entities.CategoryEntity;
import ptit.oop.assetmanagement.exceptions.BadRequestException;
import ptit.oop.assetmanagement.exceptions.NotFoundException;
import ptit.oop.assetmanagement.mappers.AssetMapper;
import ptit.oop.assetmanagement.repositories.AssetRepository;
import ptit.oop.assetmanagement.repositories.CategoryRepository;
import ptit.oop.assetmanagement.security.UserDetailsImpl;
import ptit.oop.assetmanagement.utils.GeneratingStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AssetService {
	private final Logger logger = LoggerFactory.getLogger(AssetService.class);
	private final AssetRepository assetRepository;
	private final CategoryRepository categoryRepository;
	private final GeneratingStringUtils generator;

	public AssetService(AssetRepository assetRepository, CategoryRepository categoryRepository, GeneratingStringUtils generator) {
		this.assetRepository = assetRepository;
		this.categoryRepository = categoryRepository;
		this.generator = generator;
	}

	public AssetDto create(CreateAssetDto asset, UserDetailsImpl admin) {

		AssetEntity entity = AssetEntity.builder()
				.assetCode(this.generator.generateAssetCode(asset.getCategory()))
				.name(asset.getName().trim())
				.specification(asset.getSpecification().trim())
				.installedDate(asset.getInstalledDate())
				.state(asset.getState())
				.category(this.categoryRepository.findById(asset.getCategory()).orElse(new CategoryEntity()))
				.location(admin.getLocationEntity())
				.build();

		entity.setCreatedBy(admin.getUsername());
		entity.setUpdatedAt(null);
		entity.setUpdatedBy(null);

		try {
			return AssetMapper.toDto(this.assetRepository.save(entity));
		} catch (NullPointerException e) {
			throw Objects.nonNull(e.getMessage()) ? new BadRequestException(e.getMessage()) : new BadRequestException(e);
		}
	}

    public PageResponse<Serializable> getAllDefault(UserDetailsImpl admin, Pageable pageable) {
        logger.info("Get all default asset ");
        Page<AssetEntity> page = assetRepository.getAllDefault(admin.getLocationEntity().getId(), pageable);
        return PageResponse.builder()
                .currentPage(page.getNumber())
                .totalItems((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .contents(AssetMapper.toDtoList(page.getContent()))
                .build();
    }

    public PageResponse<Serializable> getWithFilterAndSearch(
            UserDetailsImpl admin, String keyword, String category, String state, Pageable pageable
    ) {
        keyword = (keyword == null) ? "" : keyword;
        List<String> categories = (category == null)
				? categoryRepository.findAll().stream().map(CategoryEntity::getPrefix).collect(Collectors.toList())
				: Arrays.stream(category.split(",")).collect(Collectors.toList());

		List<String> states = state == null
				? Arrays.asList(SystemStates.AssetStates.AVAILABLE.getState(),
					SystemStates.AssetStates.NOT_AVAILABLE.getState(),
					SystemStates.AssetStates.ASSIGNED.getState())
				: Arrays.stream(state.split(",")).collect(Collectors.toList());

        logger.info("keyword={}, category={}, state={}", keyword, category, state);
        Page<AssetEntity> page = assetRepository.getWithFilterAndSearch(
                admin.getLocationEntity().getId(),
                keyword,
				categories,
				states,
				pageable);
        return PageResponse.builder()
                .currentPage(page.getNumber())
                .totalItems((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .contents(AssetMapper.toDtoList(page.getContent()))
                .build();
    }

    public AssetDto getAssetDetails(String assetCode) {
		logger.info("assetCode = {}", assetCode);
		AssetEntity entity = assetRepository.getByAssetCode(assetCode)
                .orElseThrow(() -> new NotFoundException(String.format("Cannot find asset with code=%s", assetCode)));
        // TODO: assignment history of asset
		logger.info("RETURN asset details");
		return AssetMapper.toDetailedDto(entity);
    }

	public Boolean isAvailable(String assetCode) {
		AssetEntity entity = this.assetRepository.findById(assetCode).orElseThrow(NotFoundException::new);

		return entity.getState().equals("Available");
	}

}
