package bkv.colligendis.rest;


import bkv.colligendis.database.entity.User;
import bkv.colligendis.security.JwtTokenProvider;
import bkv.colligendis.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @GetMapping(value = "/users")
    public ResponseEntity<List<User>> getAll() {
        List<User> users = userService.findAll();
        return users != null && !users.isEmpty()
                ? new ResponseEntity<>(users, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

//    @GetMapping(value = "/users/{id}")
//    public ResponseEntity<User> getUserById(@PathVariable(name = "id") long id){
//
//        User user = userService.findById(id);
//        return user != null
//                ? new ResponseEntity<>(user, HttpStatus.OK)
//                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }

    @GetMapping(value = "/users/{username}")
    public ResponseEntity<User> getUserByName(HttpServletRequest request, @PathVariable(name = "username") String username){

        String token = jwtTokenProvider.resolveToken(request);

        boolean isValid = jwtTokenProvider.validateToken(token);
        Authentication authentication = jwtTokenProvider.getAuthentication(token);


        User user = userService.findByUsername(username);
        return user != null
                ? ResponseEntity.ok(user)
                : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "signup")
    public ResponseEntity<User> signUp(@RequestBody SignUpUser signUpUser){

        if(signUpUser.getEmail() == null) return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        User user = userService.findByEmail(signUpUser.getEmail());
        if(user != null){
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        user = new User();
        user.setUsername(signUpUser.getUsername());
        user.setEmail(signUpUser.getEmail());
        user.setPassword(DigestUtils.sha256Hex(signUpUser.getPassword()));

        user = userService.save(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(value = "login")
    public ResponseEntity<String> login(@RequestBody LoginUser loginUser){

        if(loginUser.getEmail() == null) return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        User user = userService.findByEmail(loginUser.getEmail());
        if(user == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        if(user.getPassword().equals(DigestUtils.sha256Hex(loginUser.getPassword()))){
//            user.setIsExpired(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            String token = "";
//            Algorithm algorithm = Algorithm.HMAC256("qwerty");
//            String token = JWT.create()
//                    .withClaim("authorized", true)
//                    .withClaim("userId", user.getId())
//                    .withClaim("userName", user.getName())
//                    .withClaim("email", user.getEmail())
////                    .withClaim("exp", user.getAuth_expired())
//                    .withExpiresAt(new Date(user.getAuth_expired()))
//                    .sign(algorithm);
            return new ResponseEntity<>(token, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
