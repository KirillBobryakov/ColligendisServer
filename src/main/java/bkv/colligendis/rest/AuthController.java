package bkv.colligendis.rest;


import bkv.colligendis.database.entity.User;
import bkv.colligendis.security.JwtTokenProvider;
import bkv.colligendis.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity signin(@RequestBody AuthenticationRequest data) {
        try {
            String username = data.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            User user = userService.findByUsername(username);
            String token = jwtTokenProvider.createToken(username, user.getEmail(), this.userService.findByUsername(username).getRoles());
            Map<Object, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("token", token);
            return ResponseEntity.ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody SignUpUser signUpUser){

        if(signUpUser.getEmail() == null || signUpUser.getUsername() == null) return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        User user = userService.findByUsername(signUpUser.getUsername());
        if(user != null){
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        user = new User();
        user.setUsername(signUpUser.getUsername());
        user.setEmail(signUpUser.getEmail());
        user.setPassword(this.passwordEncoder.encode(signUpUser.getPassword()));

        user = userService.save(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
