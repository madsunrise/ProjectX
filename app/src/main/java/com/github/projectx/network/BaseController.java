package com.github.projectx.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.github.projectx.model.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.github.projectx.network.BaseController.CookieHelper.KEY1;
import static com.github.projectx.network.BaseController.CookieHelper.KEY2;

/**
 * Created by igor on 17.04.17.
 */

public abstract class BaseController {

    public static final String BASE_URL = "http://212.109.192.197:8081/v1/";

    static Retrofit retrofit;

    BaseController(Context context) {
        if (retrofit == null) {
            init(context);
        }
    }

    public static boolean isAuthorized(Context context) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.contains(KEY1) && sp.contains(KEY2);
    }

    public static void resetAuth(Context context) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor e = sp.edit();
        e.remove(KEY1);
        e.remove(KEY2);
        e.apply();
    }

    private void init(Context context) {
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

        Gson gson = new GsonBuilder().registerTypeAdapter(Service.class, new Service.ServiceDeserializer()).create();


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
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
}
