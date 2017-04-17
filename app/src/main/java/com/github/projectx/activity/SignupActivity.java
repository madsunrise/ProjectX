package com.github.projectx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class SignupActivity extends AppCompatActivity implements AuthController.SignupResult {

    @BindView(R.id.name)
    TextInputEditText name;
    @BindView(R.id.email)
    TextInputEditText email;
    @BindView(R.id.phone)
    TextInputEditText phone;
    @BindView(R.id.password)
    TextInputEditText password;
    @BindView(R.id.btn_signup)
    Button btnSignup;

    private AuthController controller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        controller = AuthController.getInstance(getApplicationContext());
        controller.setSignupResultListener(this);

        btnSignup.setEnabled(!controller.isSignupPerforming());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.setSignupResultListener(null);
        Log.d("3423", "onDestroy");
    }

    @OnClick(R.id.btn_signup)
    public void signup() {
        btnSignup.setEnabled(false);
        controller.signup(name.getText().toString(),
                email.getText().toString(),
                phone.getText().toString(),
                password.getText().toString());
    }

    @Override
    public void onResult(boolean success, int message) {
        btnSignup.setEnabled(true);
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
