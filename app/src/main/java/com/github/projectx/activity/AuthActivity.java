package com.github.projectx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.github.projectx.R;
import com.github.projectx.network.AuthService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by igor on 16.04.17.
 */

public class AuthActivity extends AppCompatActivity implements AuthService.LoginResult {

    @BindView(R.id.login)
    TextInputEditText login;
    @BindView(R.id.password)
    TextInputEditText password;
    @BindView(R.id.btn_login)
    Button btnLogin;

    private AuthService service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        service = AuthService.instance(getApplicationContext());
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_login})
    public void login() {
        btnLogin.setEnabled(false);
        service.login(login.getText().toString(), password.getText().toString(), this);
    }

    @Override
    public void onResult(boolean type, boolean success) {
        btnLogin.setEnabled(true);
        if (!success) {
            Toast.makeText(this, "login failed", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
