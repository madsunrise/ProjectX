package com.github.projectx;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.projectx.model.Service;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ivan on 16.04.17.
 */

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.MyViewHolder> {

    private final Context context;
    private final List<Service> serviceList;

    public ServiceListAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        TextView description;

        TextView price;

        TextView rating;

        MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.service_name);
            description = (TextView) view.findViewById(R.id.service_description);
            price = (TextView) view.findViewById(R.id.service_price);
            rating = (TextView) view.findViewById(R.id.service_rating);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.name.setText(service.getName());
        holder.description.setText(service.getDescription());
        holder.price.setText(String.valueOf(service.getPrice()));
        holder.rating.setText(String.valueOf(service.getRating()));
    }


    @Override
    public int getItemCount() {
        return serviceList.size();
    }
}

