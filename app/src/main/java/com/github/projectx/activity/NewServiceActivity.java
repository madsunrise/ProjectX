package com.github.projectx.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.github.projectx.R;
import com.github.projectx.model.NewServiceRequest;
import com.github.projectx.network.ServiceController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by igor on 17.04.17.
 */

public class NewServiceActivity extends AppCompatActivity implements ServiceController.ServiceEditCallback {
    @BindView(R.id.name)
    public EditText name;
    @BindView(R.id.description)
    public EditText description;
    @BindView(R.id.price)
    public EditText price;
    private ServiceController serviceController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_service_activity);
        ButterKnife.bind(this);
        serviceController = ServiceController.getInstance(getApplicationContext());
        serviceController.setServiceEditCallback(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceController.setServiceEditCallback(null);
    }

    @Override
    public void onRequestComplete(boolean success) {
        if (!success) {
            Toast.makeText(this, R.string.error_occured, Toast.LENGTH_SHORT).show();
            return;
        }
        finish();
    }

    @OnClick(R.id.save)
    public void postService() {
        String name = this.name.getText().toString();
        String description = this.description.getText().toString();
        String price = this.price.getText().toString();
        if (name.isEmpty() || description.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        serviceController.sendNewService(new NewServiceRequest(name, description, Integer.valueOf(price)));
    }
}
