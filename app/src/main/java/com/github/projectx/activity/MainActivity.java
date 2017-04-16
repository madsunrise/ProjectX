package com.github.projectx.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.projectx.R;
import com.github.projectx.ServiceListAdapter;
import com.github.projectx.model.Service;
import com.github.projectx.network.NetworkService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NetworkService.ServiceListCallback {

    private final NetworkService networkService = NetworkService.getInstance();
    private ServiceListAdapter adapter;
    @BindView(R.id.service_list_recycler)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        networkService.setServiceListCallback(this);
        ButterKnife.bind(this);


        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new ServiceListAdapter(this, new ArrayList<Service>()));

        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkService.queryForServiceList(null, null, 1, 15);
    }

    @Override
    public void onDataLoaded(List<Service> services) {
        Log.d(TAG, "Service list has been loaded");
        adapter = new ServiceListAdapter(this, services);
        recyclerView.swapAdapter(adapter, false);
    }

    @Override
    public void dataLoadingFailed() {
        Log.d(TAG, "Loading service list from network failed!");
        Toast.makeText(this, R.string.error_occured, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkService.setServiceListCallback(null);
    }

    private static final String TAG = MainActivity.class.getSimpleName();
}
