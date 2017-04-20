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

public class AuthActivity extends AppCompatActivity implements AuthController.LoginListener {

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
        setContentView(R.layout.auth_activity);
        ButterKnife.bind(this);

        controller = AuthController.getInstance(getApplicationContext());
        controller.setLoginResultListener(this);

        btnLogin.setEnabled(!controller.isLoginPerforming());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.setLoginResultListener(null);
    }

    @OnClick(R.id.btn_login)
    public void login() {
        btnLogin.setEnabled(false);
        controller.login(login.getText().toString(), password.getText().toString());
    }

    @OnClick(R.id.btn_register)
    public void signup() {
        startActivityForResult(new Intent(this, SignupActivity.class), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onResult(boolean success, int message) {
        btnLogin.setEnabled(true);
        if (success) {
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
