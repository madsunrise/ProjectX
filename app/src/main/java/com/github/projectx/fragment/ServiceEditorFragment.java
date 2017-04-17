package com.github.projectx.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.github.projectx.R;
import com.github.projectx.model.NewServiceRequest;
import com.github.projectx.model.Service;
import com.github.projectx.network.ServiceController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @BindView(R.id.default_photo)
    public ImageView addPhotoIV;
    @BindView(R.id.photo_container)
    LinearLayout photoContainer;

    private final Service service = new Service();
    private final List<ImageView> imageViews = new ArrayList<>();
    private final List<String> photos = new ArrayList<>();


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
        addPhotoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_photo)), PICK_IMAGE_GALLERY_REQUEST);
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

        service.setName(name);
        service.setDescription(description);
        service.setPrice(Integer.valueOf(price));
        service.setPhotos(photos);

        NewServiceRequest request = new NewServiceRequest(service);
        serviceController.sendNewService(request);
    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_GALLERY_REQUEST && resultCode == RESULT_OK) {
            if (data == null || data.getData() == null) {
                return;
            }

            Uri uri = data.getData();
            try {
                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                addPhotoToScreen(bitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                String encoded = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);
                stream.close();
                photos.add(encoded);

            } catch (IOException e) {
                Log.e(TAG, "Failed to pick image: " + e.getMessage());
            }
        }
    }


    private void addPhotoToScreen(Bitmap bitmap) {
        ImageView image = new ImageView(getContext());
        image.setMaxHeight(convertToPx(75));
        image.setAdjustViewBounds(true);
        image.setImageBitmap(bitmap);

        imageViews.add(image);
        photoContainer.addView(image, 0);
    }

    private int convertToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        serviceController.setServiceEditCallback(null);
    }

    private static final String TAG = ServiceEditorFragment.class.getSimpleName();
}
