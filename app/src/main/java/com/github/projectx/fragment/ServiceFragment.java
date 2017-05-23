package com.github.projectx.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.projectx.R;
import com.github.projectx.model.Service;
import com.github.projectx.network.ServiceController;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by igor on 17.04.17.
 */

public class ServiceFragment extends Fragment implements ServiceController.ServiceInfoListener {

    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.container)
    LinearLayout container;

    private ServiceController controller;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service_fragment, container, false);
        ButterKnife.bind(this, view);
        long id = getArguments().getLong("id");
        name.setVisibility(View.INVISIBLE);
        description.setVisibility(View.INVISIBLE);
        price.setVisibility(View.INVISIBLE);

        controller = ServiceController.getInstance(getContext().getApplicationContext());
        controller.setServiceInfoListener(this);
        controller.requestServiceInfo(id);
        return view;
    }

    @Override
    public void onDataLoaded(Service service) {
        controller.setServiceInfoListener(null);
        progress.setVisibility(View.GONE);
        name.setVisibility(View.VISIBLE);
        description.setVisibility(View.VISIBLE);
        price.setVisibility(View.VISIBLE);

        name.setText(service.getName());
        description.setText(service.getDescription());

        String priceStr = String.format(Locale.getDefault(), "%d \u20BD / усл.", service.getPrice());
        price.setText(priceStr);

        List<String> photoUrls = service.getPhotos();
        for (String url: photoUrls) {
            ImageView image = new ImageView(getContext());
            image.setImageResource(R.drawable.default_pic);
            //Glide.with(this).load(BASE_URL + url).into(image);
            container.addView(image);
        }
    }

    @Override
    public void dataLoadingFailed() {
        controller.setServiceInfoListener(null);
        progress.setVisibility(View.GONE);
        Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
    }
}
