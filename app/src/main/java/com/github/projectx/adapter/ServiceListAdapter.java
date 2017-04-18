package com.github.projectx.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.projectx.R;
import com.github.projectx.fragment.FeedFragment;
import com.github.projectx.model.Service;
import com.github.projectx.network.BaseController;

import java.util.Collections;
import java.util.List;

/**
 * Created by ivan on 16.04.17.
 */

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.MyViewHolder> {

    private final List<Service> serviceList;
    private final FeedFragment feedFragment;

    public ServiceListAdapter(List<Service> serviceList, FeedFragment fragment) {
        this.serviceList = Collections.unmodifiableList(serviceList);
        feedFragment = fragment;
    }

    public List<Service> getServiceList() {
        return serviceList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_list_item, parent, false);
        itemView.setOnClickListener(feedFragment);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.name.setText(service.getName());

        int price = service.getPrice();
        String priceText = price + " \u20BD / усл.";
        holder.price.setText(priceText);

        holder.rating.setText(String.valueOf(service.getRating()));

        List<String> photos = service.getPhotos();
        if (!photos.isEmpty()) {
            String mainPhotoURL = BaseController.BASE_URL + photos.get(0);
            Glide.with(feedFragment).load(mainPhotoURL).centerCrop().into(holder.photo);
        }
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView price;
        TextView rating;
        ImageView photo;

        MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.service_name);
            price = (TextView) view.findViewById(R.id.service_price);
            rating = (TextView) view.findViewById(R.id.service_rating);
            photo = (ImageView) view.findViewById(R.id.service_photo);
        }
    }
}

