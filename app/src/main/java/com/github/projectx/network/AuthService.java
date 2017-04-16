package com.github.projectx.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.github.projectx.R;
import com.github.projectx.model.LoginRequest;
import com.github.projectx.model.SignupRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.github.projectx.network.AuthService.CookieHelper.KEY1;
import static com.github.projectx.network.AuthService.CookieHelper.KEY2;

/**
 * Created by igor on 16.04.17.
 */

public class AuthService {

    private static AuthService instance = null;

    public static AuthService instance(Context context) {
        if (instance == null) {
            instance = new AuthService(context);
        }
        return instance;
    }

    private final AuthAPI api;

    private AuthService(Context context) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                SharedPreferences.Editor e = sp.edit();
                for (Cookie c : cookies) {
                    if (c.name().equals(KEY1) || c.name().equals(KEY2)) {
                        CookieHelper.store(c, e);
                    }
                }
                e.apply();
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = new ArrayList<>();
                if (sp.contains(KEY1)) {
                    cookies.add(CookieHelper.get(KEY1, sp));
                }
                if (sp.contains(KEY2)) {
                    cookies.add(CookieHelper.get(KEY2, sp));
                }
                return cookies;
            }
        }).addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();

        api = retrofit.create(AuthAPI.class);
    }

    public void login(String login, String password, final LoginResult callback) {
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

    public void signup(String name, String email, String phone, String password, final SignupResult callback) {
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

    static class CookieHelper {
        static final String KEY1 = "token";
        static final String KEY2 = "session_id";

        static void store(Cookie cookie, SharedPreferences.Editor e) {
            e.putString(cookie.name(), cookie.value());
            e.putLong(cookie.name() + "_expiresAt", cookie.expiresAt());
            e.putString(cookie.name() + "_domain", cookie.domain());
            e.putString(cookie.name() + "_path", cookie.path());
            e.putBoolean(cookie.name() + "_secure", cookie.secure());
            e.putBoolean(cookie.name() + "_httpOnly", cookie.httpOnly());
            e.putBoolean(cookie.name() + "_hostOnly", cookie.hostOnly());
        }

        static Cookie get(String name, SharedPreferences sp) {
            if (!sp.contains(name)) return null;
            Cookie.Builder cb = new Cookie.Builder().name(name)
                    .value(sp.getString(name, ""))
                    .expiresAt(sp.getLong(name + "_expiresAt", 0))
                    .domain(sp.getString(name + "_domain", ""))
                    .path(sp.getString(name + "_path", ""));
            if (sp.getBoolean(name + "_secure", false)) {
                cb.secure();
            }
            if (sp.getBoolean(name + "_httpOnly", false)) {
                cb.httpOnly();
            }
            if (sp.getBoolean(name + "_hostOnly", false)) {
                cb.httpOnly();
            }
            return cb.build();
        }
    }

    public interface LoginResult {
        void onResult(boolean success, int message);
    }

    public interface SignupResult {
        void onResult(boolean success, int message);
    }
}
