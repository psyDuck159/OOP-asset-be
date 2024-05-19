package ptit.oop.assetmanagement.services;

import ptit.oop.assetmanagement.dtos.UserDto;
import ptit.oop.assetmanagement.dtos.response.PageResponse;
import ptit.oop.assetmanagement.entities.UserEntity;
import ptit.oop.assetmanagement.exceptions.BadRequestException;
import ptit.oop.assetmanagement.exceptions.NotFoundException;
import ptit.oop.assetmanagement.mappers.UserMapper;
import ptit.oop.assetmanagement.repositories.UserRepository;
import ptit.oop.assetmanagement.security.UserDetailsImpl;
import ptit.oop.assetmanagement.utils.GeneratingStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    @Value("${staff.code.prefix}")
    private String staffCodePrefix;
    @Value("${staff.code.number.length}")
    private String staffCodeNumberLength;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GeneratingStringUtils generatingStringUtils;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, GeneratingStringUtils generatingStringUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.generatingStringUtils = generatingStringUtils;
    }

    public UserDto create(UserEntity entity, UserDetailsImpl adminDetails) {
        entity.setStaffCode(generatingStringUtils.generateStaffCode());

        String username = generatingStringUtils.generateUsername(entity.getFirstName(), entity.getLastName());
        entity.setUsername(username);

        String password = generatingStringUtils.generatePassword(username, entity.getDob());
        entity.setPassword(passwordEncoder.encode(password));

        entity.setEnable(true);
        entity.setLocation(adminDetails.getLocationEntity());
        entity.setCreatedBy(adminDetails.getUsername());
        entity.setUpdatedBy(null);//to check whether the user changed their password for the first time or not.

        try {
            return UserMapper.toDto(userRepository.save(entity));
        } catch (NullPointerException e) {
            throw Objects.nonNull(e.getMessage()) ? new BadRequestException(e.getMessage()) : new BadRequestException(e);
        }
    }

    public PageResponse<Serializable> getAll(UserDetailsImpl admin, String keyword, String type, Pageable pageable) {
        keyword = Objects.isNull(keyword) ? "" : keyword;
        type = Objects.isNull(type) ? "" : type;

        Page<UserEntity> page = this.userRepository.getAll(admin.getLocationEntity().getId(), keyword.toLowerCase(), type, pageable);

        return PageResponse.builder()
            .currentPage(page.getNumber())
            .totalItems((int) page.getTotalElements())
            .totalPages(page.getTotalPages())
            .contents(UserMapper.toDtoList(page.getContent()))
            .build();
    }

    public UserDto getByUsername(String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication)) {
            throw new AccessDeniedException("Forbidden");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Optional<UserEntity> response = this.userRepository.findByUsernameAndLocation(username, userDetails.getLocationEntity());
        if (response.isPresent()) {
            return UserMapper.toDto(response);
        } else {
            return null;
        }
    }

    public String changePassword(String username, String password) {
        UserEntity user = this.userRepository.findById(username).orElseThrow(NotFoundException::new);

        user.setPassword(this.passwordEncoder.encode(password));

        return this.userRepository.save(user).getUsername();
    }

    public void changeLoginRecord(String username) {
        UserEntity user = this.userRepository.findById(username).orElseThrow(NotFoundException::new);

        user.setLastLogin(LocalDateTime.now());

        this.userRepository.save(user);
    }

    public UserDto update(String username, UserEntity entity, UserDetailsImpl admin) {
        UserEntity old = this.userRepository
                .findByUsernameAndLocation(username, admin.getLocationEntity())
                .orElseThrow(() -> new NotFoundException(String.format("User %s doesn't exist", username)));
        old.setDob(entity.getDob());
        old.setGender(entity.getGender());
        old.setJointDate(entity.getJointDate());
        old.setType(entity.getType());
        return UserMapper.toDto(userRepository.save(old));
    }

    public Boolean isEnable(String username) {
        UserEntity entity = this.userRepository.findById(username).orElseThrow(NotFoundException::new);

        return entity.isEnable();
    }
}
