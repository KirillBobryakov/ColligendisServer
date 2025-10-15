package bkv.colligendis.rest.auth;

import bkv.colligendis.database.entity.User;
import bkv.colligendis.database.service.users.UserService;
import bkv.colligendis.database.service.users.UserTokenService;
import bkv.colligendis.rest.ApiResponse;
import bkv.colligendis.rest.AuthenticationRequest;
import bkv.colligendis.rest.RegisterUserRequest;
import bkv.colligendis.rest.SignUpUser;
import bkv.colligendis.rest.dto.UserDTO;
import bkv.colligendis.rest.exceptions.ForbiddenException;
import bkv.colligendis.rest.exceptions.InvalidCredentialsException;
import bkv.colligendis.rest.exceptions.InvalidRefreshTokenException;
import bkv.colligendis.rest.exceptions.InvalidTokenException;
import bkv.colligendis.rest.exceptions.UnauthorizedException;
import bkv.colligendis.security.JwtTokenProvider;
import bkv.colligendis.security.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserTokenService userTokenService;
    private final RefreshTokenService refreshTokenService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<AuthResponse>> signin(@RequestBody AuthenticationRequest data) {
        try {
            // String username = "admin";
            String email = data.getEmail();
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(email, data.getPassword()));
            User user = (User) authentication.getPrincipal();
            String accessToken = jwtTokenProvider.createAccessToken(authentication);
            String refreshToken = jwtTokenProvider.createRefreshToken(authentication);
            String refreshJti = jwtTokenProvider.getJti(refreshToken);
            long accessTokenExpiresAt = jwtTokenProvider.getExpiration(accessToken).getTime();
            long refreshTokenExpiresAt = jwtTokenProvider.getExpiration(refreshToken).getTime();

            if (refreshJti != null) {
                refreshTokenService.storeInDB(email, accessToken, refreshToken, refreshJti, accessTokenExpiresAt,
                        refreshTokenExpiresAt);
            }
            AuthResponse authResponse = AuthResponse.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .accessTokenExpiresAt(accessTokenExpiresAt)
                    .refreshTokenExpiresAt(refreshTokenExpiresAt)
                    .build();

            return ResponseEntity.ok(new ApiResponse<>(authResponse, "Signin successful",
                    ApiResponse.Status.SUCCESS));
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email or password");
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Authentication failed: " + e.getMessage());
        }
    }

    @GetMapping(value = "/users/{email}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserInfo(@PathVariable(name = "email") String email) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated");
        }

        if (!(authentication.getPrincipal() instanceof User)) {
            throw new UnauthorizedException("Invalid authentication principal");
        }

        String emailFromAuth = ((User) authentication.getPrincipal()).getEmail();
        if (!emailFromAuth.equals(email)) {
            throw new ForbiddenException("Access denied - can only access own user information");
        }

        User user = userService.findByEmail(email);
        if (user == null) {
            throw new InvalidCredentialsException("User not found");
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setProfilePictureUrl(user.getProfilePictureUrl());

        return ResponseEntity.ok(
                new ApiResponse<>(userDTO, "User info fetched successfully", ApiResponse.Status.SUCCESS));
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<Object, Object>> signUp(@RequestBody SignUpUser signUpUser) {

        if (signUpUser.getEmail() == null || signUpUser.getEmail().isEmpty()
                || signUpUser.getUsername() == null || signUpUser.getUsername().isEmpty()
                || signUpUser.getPassword() == null || signUpUser.getPassword().isEmpty())
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        User user = userService.findByUsername(signUpUser.getUsername());
        // if(user != null){
        // return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        // }

        if (user == null) {
            user = new User();
            user.setUsername(signUpUser.getUsername());
            user.setEmail(signUpUser.getEmail());
            user.setPassword(this.passwordEncoder.encode(signUpUser.getPassword()));

            // Assign default USER role
            user.getRoles().add("USER");

            user = userService.save(user);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signUpUser.getUsername(), signUpUser.getPassword()));
        String token = jwtTokenProvider.createAccessToken(authentication);

        Map<Object, Object> model = new HashMap<>();
        model.put("message", signUpUser.getUsername());
        model.put("token", token);

        return ResponseEntity.ok(model);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterUserRequest request) {
        // Validate required fields
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()
                || request.getEmail() == null || request.getEmail().trim().isEmpty()
                || request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseEntity
                    .ok(new ApiResponse<>(null, "All fields are required", ApiResponse.Status.ERROR));
        }

        // Validate password length
        if (request.getPassword().length() < 6) {
            return ResponseEntity
                    .ok(new ApiResponse<>(null, "Password must be at least 6 characters long",
                            ApiResponse.Status.ERROR));
        }

        // Check if user already exists
        User existingUser = userService.findByUsername(request.getUsername().trim());
        if (existingUser != null) {
            return ResponseEntity
                    .ok(new ApiResponse<>(null, "User with this name already exists", ApiResponse.Status.ERROR));
        }

        // Check if email already exists
        User existingUserByEmail = userService.findByEmail(request.getEmail().trim());
        if (existingUserByEmail != null) {
            return ResponseEntity
                    .ok(new ApiResponse<>(null, "User with this email already exists", ApiResponse.Status.ERROR));
        }

        try {
            // Create new user
            User newUser = new User();
            newUser.setUsername(request.getUsername().trim());
            newUser.setEmail(request.getEmail().trim());
            newUser.setPassword(this.passwordEncoder.encode(request.getPassword()));

            // Assign default USER role
            newUser.getRoles().add("USER");

            // Save user
            User savedUser = userService.save(newUser);

            // Generate authentication token

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(savedUser.getEmail(), request.getPassword()));
            String accessToken = jwtTokenProvider.createAccessToken(authentication);
            String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

            String refreshJti = jwtTokenProvider.getJti(refreshToken);
            long accessTokenExpiresAt = jwtTokenProvider.getExpiration(accessToken).getTime();
            long refreshTokenExpiresAt = jwtTokenProvider.getExpiration(refreshToken).getTime();

            if (refreshJti != null) {
                refreshTokenService.storeInDB(savedUser.getEmail(), accessToken, refreshToken, refreshJti,
                        accessTokenExpiresAt,
                        refreshTokenExpiresAt);
            }

            AuthResponse authResponse = AuthResponse.builder()
                    .username(savedUser.getUsername())
                    .email(savedUser.getEmail())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .accessTokenExpiresAt(accessTokenExpiresAt)
                    .refreshTokenExpiresAt(refreshTokenExpiresAt)
                    .build();

            return ResponseEntity
                    .ok(new ApiResponse<>(authResponse, "User registered successfully", ApiResponse.Status.SUCCESS));
        } catch (Exception e) {
            return ResponseEntity
                    .ok(new ApiResponse<>(null, "Failed to register user: " + e.getMessage(),
                            ApiResponse.Status.ERROR));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refresh_token");
        String email = request.get("email");

        try {
            User user = userService.findByEmail(email);
            // Check refresh token on expiration
            if (!jwtTokenProvider.validateToken(refreshToken) || user.getUserToken() == null
                    || !user.getUserToken().getRefreshToken().equals(refreshToken)) {
                throw new InvalidRefreshTokenException("Refresh token is invalid or expired");
            }

            // Rotate: revoke old and issue new refresh
            Authentication authentication = jwtTokenProvider.createAuthenticationFromUser(user);
            String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
            String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);
            String newJti = jwtTokenProvider.getJti(newRefreshToken);
            long newAccessTokenExpiresAt = jwtTokenProvider.getExpiration(newAccessToken).getTime();
            long newRefreshTokenExpiresAt = jwtTokenProvider.getExpiration(newRefreshToken).getTime();
            refreshTokenService.rotate(email, newAccessToken, newRefreshToken, newJti,
                    newAccessTokenExpiresAt, newRefreshTokenExpiresAt);

            AuthResponse authResponse = AuthResponse.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .accessTokenExpiresAt(newAccessTokenExpiresAt)
                    .refreshTokenExpiresAt(newRefreshTokenExpiresAt)
                    .build();

            return ResponseEntity.ok(new ApiResponse<>(authResponse, "Token refreshed successfully",
                    ApiResponse.Status.SUCCESS));
        } catch (InvalidTokenException | ForbiddenException | InvalidRefreshTokenException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnauthorizedException("Failed to refresh token: " + ex.getMessage());
        }
    }

    @PostMapping("/revoke")
    public ResponseEntity<ApiResponse<Boolean>> revoke(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean isRevoked = userTokenService.deleteByUserEmail(email);
        if (isRevoked) {
            return ResponseEntity
                    .ok(new ApiResponse<>(isRevoked, "Token revoked successfully", ApiResponse.Status.SUCCESS));
        } else {
            return ResponseEntity
                    .ok(new ApiResponse<>(isRevoked, "Token not found", ApiResponse.Status.ERROR));
        }
    }

}
