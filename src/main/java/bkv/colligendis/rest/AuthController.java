package bkv.colligendis.rest;


import bkv.colligendis.database.entity.User;
import bkv.colligendis.security.JwtTokenProvider;
import bkv.colligendis.services.UserService;
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

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<Map<Object, Object>> signin(@RequestBody AuthenticationRequest data) {
        try {
            String username = "admin";
//            String username = data.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            User user = userService.findByUsername(username);
            String token = jwtTokenProvider.createToken(username, user.getEmail(), this.userService.findByUsername(username).getRoles());
            Map<Object, Object> model = new HashMap<>();
            model.put("message", username);
            model.put("token", token);
            return ResponseEntity.ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
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
    public ResponseEntity<Map<Object, Object>> signUp(@RequestBody SignUpUser signUpUser){

        if (signUpUser.getEmail() == null || signUpUser.getEmail().isEmpty()
                || signUpUser.getUsername() == null || signUpUser.getUsername().isEmpty()
                || signUpUser.getPassword() == null || signUpUser.getPassword().isEmpty())
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        User user = userService.findByUsername(signUpUser.getUsername());
//        if(user != null){
//            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
//        }

        if(user == null){
            user = new User();
            user.setUsername(signUpUser.getUsername());
            user.setEmail(signUpUser.getEmail());
            user.setPassword(this.passwordEncoder.encode(signUpUser.getPassword()));
            user = userService.save(user);
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signUpUser.getUsername(), signUpUser.getPassword()));
        String token = jwtTokenProvider.createToken(signUpUser.getUsername(), user.getEmail(), user.getRoles());

        Map<Object, Object> model = new HashMap<>();
        model.put("message", signUpUser.getUsername());
        model.put("token", token);


        return ResponseEntity.ok(model);
    }
}
