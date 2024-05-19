package ptit.oop.assetmanagement.controllers;

import ptit.oop.assetmanagement.config.Constants;
import ptit.oop.assetmanagement.dtos.ResponseObject;
import ptit.oop.assetmanagement.dtos.UserDto;
import ptit.oop.assetmanagement.dtos.request.ChangePasswordRequest;
import ptit.oop.assetmanagement.dtos.request.LoginRequest;
import ptit.oop.assetmanagement.dtos.response.LoginResponse;
import ptit.oop.assetmanagement.exceptions.BadRequestException;
import ptit.oop.assetmanagement.mappers.LocationMapper;
import ptit.oop.assetmanagement.security.UserDetailsImpl;
import ptit.oop.assetmanagement.services.UserService;
import ptit.oop.assetmanagement.utils.GeneratingStringUtils;
import ptit.oop.assetmanagement.utils.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	private final JwtUtils jwtUtils;
	private final AuthenticationManager authenticationManager;
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final GeneratingStringUtils generatingStringUtils;

	public AuthController(JwtUtils jwtUtils, AuthenticationManager authenticationManager, UserService userService, PasswordEncoder passwordEncoder, GeneratingStringUtils generatingStringUtils) {
		this.jwtUtils = jwtUtils;
		this.authenticationManager = authenticationManager;
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
		this.generatingStringUtils = generatingStringUtils;
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseObject> login(@RequestBody LoginRequest loginRequest) {
		//TODO: implement refresh token
		ResponseObject obj;
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtUtils.generateJwtToken(authentication);

			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

			List<String> roles = userDetails.getAuthorities().stream()
					.map(GrantedAuthority::getAuthority)
					.collect(Collectors.toList());

			UserDto userDto = UserDto.builder()
					.username(userDetails.getUsername())
					.type(roles.get(0))
					.lastLogin(userDetails.getLastLogin())
					.location(LocationMapper.toDto(userDetails.getLocationEntity()))
					.build();

			obj = ResponseObject.builder()
					.status(HttpStatus.OK)
					.message("Login successfully!")
					.data(LoginResponse.builder()
							.accessToken(jwt)
							.userDto(userDto)
							.build())
					.build();

//			Sign login date to db.
			this.userService.changeLoginRecord(userDetails.getUsername());
		} catch (Exception e) {
			e.printStackTrace();
			obj = ResponseObject.builder().message("Login failed.").status(HttpStatus.UNAUTHORIZED).build();
			return new ResponseEntity<>(obj, HttpStatus.UNAUTHORIZED);
		}
		return ResponseEntity.ok(obj);
	}

	@PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
	@PatchMapping("/password")
	public ResponseEntity<ResponseObject> changePassword(@RequestBody ChangePasswordRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (Objects.isNull(authentication)) {
			throw new AccessDeniedException("Unauthorized");
		}
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		String oldPassword = Objects.equals(request.getCurrentPassword(), "") ? generatingStringUtils.generatePassword(userDetails.getUsername(), userDetails.getDob()) : request.getCurrentPassword();

		if (!passwordEncoder.matches(oldPassword, userDetails.getPassword())) {
			throw new BadRequestException("Password is incorrect");
		}

		if (!Pattern.matches(Constants.PASSWORD_REGEX, request.getNewPassword())) {
			throw new BadRequestException("Password must contain at least eight characters, including at least one number and includes both lower and uppercase letters and special characters");
		}

		if (request.getCurrentPassword().equals(request.getNewPassword())) {
			throw new BadRequestException("Can't reuse current password!");
		}

		return ResponseEntity.ok(
				ResponseObject.builder()
						.status(HttpStatus.OK)
						.message("Your password has been changed successfully")
						.data(this.userService.changePassword(userDetails.getUsername(), request.getNewPassword()))
						.build());
	}

}
