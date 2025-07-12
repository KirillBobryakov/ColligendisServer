package bkv.colligendis.utils;

import bkv.colligendis.database.entity.User;

import lombok.Data;

@Data
public class AuthUser {

    private User user;
    private String tempAuthSHA;
}
