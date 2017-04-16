package com.github.projectx.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.github.projectx.R;
import com.github.projectx.ServiceListAdapter;
import com.github.projectx.model.Service;
import com.github.projectx.network.NetworkController;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NetworkController.ServiceListCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.service_list_recycler)
    RecyclerView recyclerView;
    private NetworkController networkController;
    private ServiceListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        networkController = NetworkController.getInstance(getApplicationContext());
        networkController.setServiceListCallback(this);
        ButterKnife.bind(this);


        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new ServiceListAdapter(this, new ArrayList<Service>()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkController.queryForServiceList(null, null, 1, 15);
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
        networkController.setServiceListCallback(null);
    }
}
