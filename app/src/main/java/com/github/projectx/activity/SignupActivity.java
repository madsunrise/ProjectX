package com.github.projectx.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class SignupActivity extends AppCompatActivity implements AuthController.SignupListener {

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
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private AuthController controller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        ButterKnife.bind(this);

        controller = AuthController.getInstance(getApplicationContext());
        controller.setSignupResultListener(this);

        btnSignup.setEnabled(!controller.isSignupPerforming());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.setSignupResultListener(null);
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
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
