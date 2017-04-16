package com.github.projectx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.github.projectx.R;
import com.github.projectx.network.AuthController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by igor on 16.04.17.
 */

public class AuthActivity extends AppCompatActivity implements AuthController.LoginResult {

    @BindView(R.id.login)
    TextInputEditText login;
    @BindView(R.id.password)
    TextInputEditText password;
    @BindView(R.id.btn_login)
    Button btnLogin;

    private AuthController controller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        controller = AuthController.getInstance(getApplicationContext());
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_login)
    public void login() {
        btnLogin.setEnabled(false);
        controller.login(login.getText().toString(), password.getText().toString(), this);
    }

    @OnClick(R.id.btn_register)
    public void signup() {
        startActivity(new Intent(this, SignupActivity.class));
    }

    @Override
    public void onResult(boolean success, int message) {
        btnLogin.setEnabled(true);
        if (success) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
