package com.github.projectx.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.github.projectx.R;
import com.github.projectx.model.NewServiceRequest;
import com.github.projectx.network.ServiceController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ivan on 17.04.17.
 */

public class ServiceEditorFragment extends Fragment implements ServiceController.ServiceEditCallback {

    private ServiceController serviceController;

    @BindView(R.id.service_name_ET)
    public EditText nameET;
    @BindView(R.id.service_description_ET)
    public EditText descriptionET;
    @BindView(R.id.service_price_ET)
    public EditText priceET;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceController = ServiceController.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        serviceController.setServiceEditCallback(this);
        View view = inflater.inflate(R.layout.service_editor_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onRequestComplete(boolean success) {
        if (!success) {
            Toast.makeText(getContext(), R.string.error_occured, Toast.LENGTH_SHORT).show();
            return;
        }
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @OnClick(R.id.save)
    public void sendService() {
        String name = nameET.getText().toString();
        String description = descriptionET.getText().toString();
        String price = priceET.getText().toString();
        if (name.isEmpty() || description.isEmpty() || price.isEmpty()) {
            Toast.makeText(getContext(), R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        NewServiceRequest request = new NewServiceRequest(name, description, Integer.valueOf(price));
        serviceController.sendNewService(request);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        serviceController.setServiceEditCallback(null);
    }


    private static final String TAG = ServiceEditorFragment.class.getSimpleName();
}
