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
    private LoginResult loginResult;
    private SignupResult signupResult;

    private boolean loginPerforming = false;
    private boolean signupPerforming = false;

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

    public void setLoginResultListener(LoginResult listener) {
        loginResult = listener;
    }

    public void setSignupResultListener(SignupResult listener) {
        signupResult = listener;
    }

    public void login(final String login, String password) {
        loginPerforming = true;
        api.login(new LoginRequest(login, password)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                boolean result = response.code() == 200;
                int message;
                switch (response.code()) {
                    case 200:
                        message = R.string.success_login;
                        break;
                    case 403:
                        message = R.string.invalid_login;
                        break;
                    case 404:
                        message = R.string.user_not_found;
                        break;
                    default:
                        message = R.string.unknown_error;
                        break;
                }
                if (loginResult != null) {
                    loginResult.onResult(result, message);
                }
                loginPerforming = false;
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (loginResult != null) {
                    loginResult.onResult(false, R.string.network_error);
                }
                loginPerforming = false;
            }
        });
    }

    public void signup(String name, String email, String phone, String password) {
        signupPerforming = true;
        api.signup(new SignupRequest(name, email, phone, password)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                boolean result = response.code() == 200;
                int message;
                switch (response.code()) {
                    case 200:
                        message = R.string.success_signup;
                        break;
                    case 400:
                        message = R.string.bad_parameters;
                        break;
                    case 409:
                        message = R.string.email_taken;
                        break;
                    default:
                        message = R.string.unknown_error;
                        break;
                }
                if (signupResult != null) {
                    signupResult.onResult(result, message);
                }
                signupPerforming = false;
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (signupResult != null) {
                    signupResult.onResult(false, R.string.network_error);
                }
                signupPerforming = false;
            }
        });
    }

    public boolean isLoginPerforming() {
        return loginPerforming;
    }

    public boolean isSignupPerforming() {
        return signupPerforming;
    }

    public interface LoginResult {
        void onResult(boolean success, int message);
    }

    public interface SignupResult {
        void onResult(boolean success, int message);
    }
}
