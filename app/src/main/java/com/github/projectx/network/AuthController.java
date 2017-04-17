package com.github.projectx.network;

import android.content.Context;

import com.github.projectx.R;
import com.github.projectx.model.LoginRequest;
import com.github.projectx.model.SignupRequest;
import com.github.projectx.network.api.AuthAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by igor on 16.04.17.
 */

public class AuthController extends BaseController {

    private static AuthController instance = null;
    private final AuthAPI api;

    private AuthController(Context context) {
        super(context);
        api = retrofit.create(AuthAPI.class);
    }

    public static synchronized AuthController getInstance(Context context) {
        if (instance == null) {
            instance = new AuthController(context);
        }
        return instance;
    }

    public void login(String login, String password, final AuthController.LoginResult callback) {
        api.login(new LoginRequest(login, password)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        callback.onResult(true, R.string.success_login);
                        break;
                    case 403:
                        callback.onResult(false, R.string.invalid_login);
                        break;
                    case 404:
                        callback.onResult(false, R.string.user_not_found);
                        break;
                    default:
                        callback.onResult(false, R.string.unknown_error);
                        break;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onResult(false, R.string.network_error);
            }
        });
    }

    public void signup(String name, String email, String phone, String password, final AuthController.SignupResult callback) {
        api.signup(new SignupRequest(name, email, phone, password)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        callback.onResult(true, R.string.success_signup);
                        break;
                    case 400:
                        callback.onResult(false, R.string.bad_parameters);
                        break;
                    case 409:
                        callback.onResult(false, R.string.email_taken);
                        break;
                    default:
                        callback.onResult(false, R.string.unknown_error);
                        break;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onResult(false, R.string.network_error);
            }
        });
    }

    public interface LoginResult {
        void onResult(boolean success, int message);
    }

    public interface SignupResult {
        void onResult(boolean success, int message);
    }
}
