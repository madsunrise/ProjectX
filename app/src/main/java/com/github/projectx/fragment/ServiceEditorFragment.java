package com.github.projectx.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.projectx.R;
import com.github.projectx.model.NewServiceRequest;
import com.github.projectx.network.ServiceController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ivan on 17.04.17.
 */

public class ServiceEditorFragment extends Fragment implements ServiceController.ServiceEditCallback {

    private ServiceController serviceController;

    public static final int PICK_IMAGE_GALLERY_REQUEST = 0;

    @BindView(R.id.service_name_ET)
    public EditText nameET;
    @BindView(R.id.service_description_ET)
    public EditText descriptionET;
    @BindView(R.id.service_price_ET)
    public EditText priceET;
    @BindView(R.id.photo1)
    public ImageView photo1;

    private String mPhoto;


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
        photo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(v.getId());
            }
        });
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
        request.addPhoto(mPhoto);
        serviceController.sendNewService(request);
    }





    private void showDialog(long id) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_GALLERY_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_GALLERY_REQUEST &&
                resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri == null) {
                return;
            }
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                photo1.setImageBitmap(bitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                mPhoto = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);
                stream.close();



            } catch (IOException e) {
                Log.e(TAG, "Failed to pick image: " + e.getMessage());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        serviceController.setServiceEditCallback(null);
    }

    private static final String TAG = ServiceEditorFragment.class.getSimpleName();
}
