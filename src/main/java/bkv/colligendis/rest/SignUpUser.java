package bkv.colligendis.rest;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpUser {
    private String email;
    private String username;
    private String password;

}
