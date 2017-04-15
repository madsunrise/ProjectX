package com.github.projectx.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.projectx.R;
import com.github.projectx.model.Service;
import com.github.projectx.network.NetworkService;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NetworkService.ServiceListCallback {

    private final NetworkService networkService = NetworkService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkService.setServiceListCallback(this);
    }

    @Override
    public void onDataLoaded(List<Service> services) {
        Log.d(TAG, "Loaded");
    }

    @Override
    public void dataLoadingFailed() {
        Log.d(TAG, "Loading data failed");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkService.setServiceListCallback(null);
    }

    private static final String TAG = MainActivity.class.getSimpleName();
}
