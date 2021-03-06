package com.github.projectx.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.projectx.R;
import com.github.projectx.model.Service;
import com.github.projectx.network.ServiceController;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.github.projectx.network.NetHelper.BASE_URL;

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
    @BindView(R.id.connect_with_author)
    Button button;

    private Service service;

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
        button.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onDataLoaded(Service service) {
        this.service = service;
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
        for (String url : photoUrls) {
            ImageView image = new ImageView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 10, 0, 0);
            image.setLayoutParams(lp);
            Glide.with(this).load(BASE_URL + url).into(image);
            container.addView(image);
        }
        button.setVisibility(View.VISIBLE);
    }

    @Override
    public void dataLoadingFailed() {
        controller.setServiceInfoListener(null);
        progress.setVisibility(View.GONE);
        Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.connect_with_author)
    public void composeEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {service.getUserEmail()});
        intent.putExtra(Intent.EXTRA_SUBJECT, service.getName());
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            getActivity().startActivity(intent);
        }
    }
}
