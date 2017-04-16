package com.github.projectx.model;

/**
 * Created by igor on 16.04.17.
 */

public class SignupRequest {
    public final String name;
    public final String email;
    public final String phone;
    public final String password;

    public SignupRequest(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }
}
