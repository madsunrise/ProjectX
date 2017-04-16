package com.github.projectx.model;

/**
 * Created by igor on 16.04.17.
 */

public class LoginRequest {

    public final String login;
    public final String password;

    public LoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
