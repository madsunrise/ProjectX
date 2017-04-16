package com.github.projectx.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.projectx.R;
import com.github.projectx.ServiceListAdapter;
import com.github.projectx.model.Service;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ivan on 16.04.17.
 */

public class FeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private ServiceListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.service_list_recycler);
        setUpRecyclerView();
        return view;
    }

    private void setUpRecyclerView() {
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
    }

    public void updateDataSet(List<Service> services) {
        adapter = new ServiceListAdapter(getContext(), services);
        recyclerView.swapAdapter(adapter, false);
    }
}
