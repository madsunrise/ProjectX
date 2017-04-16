package com.github.projectx.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by igor on 16.04.17.
 */

public class Login {

    public final String login;
    public final String password;

    public Login(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
