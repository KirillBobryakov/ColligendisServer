package bkv.colligendis.rest;

import bkv.colligendis.database.entity.User;
import bkv.colligendis.database.service.users.UserService;
import bkv.colligendis.security.JwtTokenProvider;
import bkv.colligendis.services.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
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
    private final RefreshTokenService refreshTokenService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<Map<Object, Object>> signin(@RequestBody AuthenticationRequest data) {
        try {
            // String username = "admin";
            String email = data.getEmail();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, data.getPassword()));
            User user = userService.findByEmail(email);
            String token = jwtTokenProvider.createToken(email, user.getRoles());
            String refreshToken = jwtTokenProvider.createRefreshToken(email, user.getRoles());
            String refreshJti = jwtTokenProvider.getJti(refreshToken);
            if (refreshJti != null) {
                refreshTokenService.storeInDB(email, refreshJti,
                        jwtTokenProvider.getExpiration(refreshToken).getTime());
            }

            Map<Object, Object> model = new HashMap<>();
            model.put("name", user.getUsername());
            model.put("email", user.getEmail());
            model.put("access_token", token);
            model.put("refresh_token", refreshToken);
            model.put("expires_in", jwtTokenProvider.refreshValidityInMilliseconds);
            return ResponseEntity.ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email/password supplied");
        }
    }

    @GetMapping(value = "/users/{name}")
    public ResponseEntity<Map<Object, Object>> getUserInfo(@PathVariable(name = "name") String name) {
        Map<Object, Object> model = new HashMap<>();

        model.put("name", "somename");
        model.put("email", "someemail");
        model.put("created_at", "2025-02-09T21:55:20.000Z");
        model.put("image_url", "https://itemimg.com/i/331116207.0.208x208.jpg");

        return ResponseEntity.ok(model);
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
            user = userService.save(user);
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signUpUser.getUsername(), signUpUser.getPassword()));
        String token = jwtTokenProvider.createToken(signUpUser.getEmail(), user.getRoles());

        Map<Object, Object> model = new HashMap<>();
        model.put("message", signUpUser.getUsername());
        model.put("token", token);

        return ResponseEntity.ok(model);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<Object, Object>> register(@RequestBody RegisterUserRequest request) {
        // Validate required fields
        if (request.getName() == null || request.getName().trim().isEmpty()
                || request.getEmail() == null || request.getEmail().trim().isEmpty()
                || request.getPassword() == null || request.getPassword().isEmpty()
                || request.getPassword_confirmation() == null || request.getPassword_confirmation().isEmpty()) {
            Map<Object, Object> errorModel = new HashMap<>();
            errorModel.put("error", "All fields are required");
            errorModel.put("status", "error");
            return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
        }

        // Validate password confirmation
        if (!request.getPassword().equals(request.getPassword_confirmation())) {
            Map<Object, Object> errorModel = new HashMap<>();
            errorModel.put("error", "Password confirmation does not match");
            errorModel.put("status", "error");
            return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
        }

        // Validate password length
        if (request.getPassword().length() < 6) {
            Map<Object, Object> errorModel = new HashMap<>();
            errorModel.put("error", "Password must be at least 6 characters long");
            errorModel.put("status", "error");
            return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
        }

        // Check if user already exists
        User existingUser = userService.findByUsername(request.getName().trim());
        if (existingUser != null) {
            Map<Object, Object> errorModel = new HashMap<>();
            errorModel.put("error", "User with this name already exists");
            errorModel.put("status", "error");
            return new ResponseEntity<>(errorModel, HttpStatus.CONFLICT);
        }

        // Check if email already exists
        User existingUserByEmail = userService.findByEmail(request.getEmail().trim());
        if (existingUserByEmail != null) {
            Map<Object, Object> errorModel = new HashMap<>();
            errorModel.put("error", "User with this email already exists");
            errorModel.put("status", "error");
            return new ResponseEntity<>(errorModel, HttpStatus.CONFLICT);
        }

        try {
            // Create new user
            User newUser = new User();
            newUser.setUsername(request.getName().trim());
            newUser.setEmail(request.getEmail().trim());
            newUser.setPassword(this.passwordEncoder.encode(request.getPassword()));

            // Save user
            User savedUser = userService.save(newUser);

            // Generate authentication token
            String token = jwtTokenProvider.createToken(savedUser.getEmail(), savedUser.getRoles());
            String refreshToken = jwtTokenProvider.createRefreshToken(savedUser.getEmail(), savedUser.getRoles());

            String refreshJti = jwtTokenProvider.getJti(refreshToken);
            if (refreshJti != null) {
                refreshTokenService.storeInDB(savedUser.getEmail(), refreshJti,
                        jwtTokenProvider.getExpiration(refreshToken).getTime());
            }

            Map<Object, Object> model = new HashMap<>();
            model.put("message", "User registered successfully");
            model.put("status", "success");
            model.put("access_token", token);
            model.put("refresh_token", refreshToken);
            model.put("expires_in", jwtTokenProvider.refreshValidityInMilliseconds);

            return new ResponseEntity<>(model, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<Object, Object> errorModel = new HashMap<>();
            errorModel.put("error", "Failed to register user: " + e.getMessage());
            errorModel.put("status", "error");
            return new ResponseEntity<>(errorModel, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<Object, Object>> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refresh_token");
        String email = request.get("email");

        try {
            // validate signature and expiry
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            String jti = jwtTokenProvider.getJti(refreshToken);
            if (!refreshTokenService.isValid(email)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            var claims = jwtTokenProvider.parseClaims(refreshToken);
            @SuppressWarnings("unchecked")
            java.util.List<String> roles = (java.util.List<String>) claims.get("role");

            // rotate: revoke old and issue new refresh
            String newRefreshToken = jwtTokenProvider.createRefreshToken(email, roles);
            String newJti = jwtTokenProvider.getJti(newRefreshToken);
            refreshTokenService.rotate(email, jti, newJti,
                    jwtTokenProvider.getExpiration(newRefreshToken).getTime());

            // issue new access token
            String accessToken = jwtTokenProvider.createToken(email, roles);

            Map<Object, Object> model = new HashMap<>();
            model.put("access_token", accessToken);
            model.put("refresh_token", newRefreshToken);
            model.put("expires_in", jwtTokenProvider.refreshValidityInMilliseconds);
            return ResponseEntity.ok(model);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    // @PostMapping("/refresh")
    // public ResponseEntity<Map<Object, Object>>
    // refresh(@RequestHeader("Authorization") String authorizationHeader) {
    // if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer
    // ")) {
    // return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    // }
    // String refreshToken = authorizationHeader.substring(7);

    // try {
    // // validate signature and expiry
    // if (!jwtTokenProvider.validateToken(refreshToken)) {
    // return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    // }
    // String jti = jwtTokenProvider.getJti(refreshToken);
    // if (!refreshTokenService.isValid(jti)) {
    // return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    // }

    // var claims = jwtTokenProvider.parseClaims(refreshToken);
    // String username = (String) claims.get("username");
    // String email = (String) claims.get("email");
    // @SuppressWarnings("unchecked")
    // java.util.List<String> roles = (java.util.List<String>) claims.get("role");

    // // rotate: revoke old and issue new refresh
    // String newRefreshToken = jwtTokenProvider.createRefreshToken(username, email,
    // roles);
    // String newJti = jwtTokenProvider.getJti(newRefreshToken);
    // refreshTokenService.rotate(jti, newJti,
    // jwtTokenProvider.getExpiration(newRefreshToken).getTime());

    // // issue new access token
    // String accessToken = jwtTokenProvider.createToken(username, email, roles);

    // Map<Object, Object> model = new HashMap<>();
    // model.put("access_token", accessToken);
    // model.put("refresh_token", newRefreshToken);
    // model.put("expires_in", jwtTokenProvider.refreshValidityInMilliseconds);
    // return ResponseEntity.ok(model);
    // } catch (Exception ex) {
    // return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    // }
    // }

    @PostMapping("/revoke")
    public ResponseEntity<Map<Object, Object>> revoke(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        User user = userService.findByEmail(email);
        user.setRefreshTokenJti(null);
        user.setRefreshTokenExpiredAt(null);
        userService.save(user);

        return ResponseEntity.ok(null);
    }

    // @PostMapping("/revoke")
    // public ResponseEntity<Map<Object, Object>>
    // revoke(@RequestHeader("Authorization") String authorizationHeader) {
    // if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer
    // ")) {
    // return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    // }
    // String refreshToken = authorizationHeader.substring(7);
    // try {
    // if (!jwtTokenProvider.validateToken(refreshToken)) {
    // return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    // }
    // String jti = jwtTokenProvider.getJti(refreshToken);
    // refreshTokenService.revoke(jti);
    // Map<Object, Object> model = new HashMap<>();
    // model.put("message", "revoked");
    // return ResponseEntity.ok(model);
    // } catch (Exception ex) {
    // return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    // }
    // }

}
