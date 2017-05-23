package com.github.projectx.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.projectx.R;
import com.github.projectx.adapter.ServiceListAdapter;
import com.github.projectx.model.Service;
import com.github.projectx.network.ServiceController;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ivan on 16.04.17.
 */

public class FeedFragment extends Fragment implements ServiceController.ServiceListListener, View.OnClickListener {

    private static final String TAG = FeedFragment.class.getSimpleName();
    @BindView(R.id.service_list_recycler)
    RecyclerView recyclerView;
    private ServiceController serviceController;
    private ServiceListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceController = ServiceController.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_fragment, container, false);
        ButterKnife.bind(this, view);
        setUpRecyclerView();
        serviceController.setServiceListListener(this);
        loadAll();
        return view;
    }

    public void loadAll() {
        serviceController.requestServiceList(null, null, 1, 20);
    }

    public void loadMyServices() {
        serviceController.requestMyServices(1, 20);
    }

    @Override
    public void onDataLoaded(List<Service> services) {
        Log.d(TAG, "Service list has been loaded");
        adapter = new ServiceListAdapter(services, this);
        recyclerView.swapAdapter(adapter, false);
    }

    @Override
    public void dataLoadingFailed() {
        Log.d(TAG, "Loading service list from network failed!");
        Toast.makeText(getContext(), R.string.error_occured, Toast.LENGTH_SHORT).show();
    }

    private void setUpRecyclerView() {
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        recyclerView.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        serviceController.setServiceListListener(null);
    }

    @Override
    public void onClick(View v) {
        int itemPosition = recyclerView.getChildLayoutPosition(v);
        Service item = adapter.getServiceList().get(itemPosition);
        CHF chf = (CHF) getActivity();
        chf.openService(item);
    }

    public interface CHF {
        void openService(Service service);
    }
}
