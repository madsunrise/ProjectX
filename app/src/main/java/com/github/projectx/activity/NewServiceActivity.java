package com.github.projectx.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.projectx.R;
import com.github.projectx.model.NewServiceRequest;
import com.github.projectx.model.Service;
import com.github.projectx.network.ServiceController;
import com.github.projectx.utils.UiThread;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by igor on 17.04.17.
 */

public class NewServiceActivity extends AppCompatActivity implements ServiceController.ServiceEditListener {
    public static final int PICK_IMAGE_GALLERY_REQUEST = 0;
    private static final String TAG = NewServiceActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 101;
    private final Service service = new Service();
    private final List<String> encodedPhotos = new ArrayList<>();
    private final ExecutorService photoProcessor = Executors.newSingleThreadExecutor();
    @BindView(R.id.name)
    public TextInputEditText name;
    @BindView(R.id.description)
    public TextInputEditText description;
    @BindView(R.id.price)
    public TextInputEditText price;
    @BindView(R.id.save)
    public Button saveButton;
    @BindView(R.id.default_photo)
    public ImageView addPhotoIV;
    @BindView(R.id.photo_container)
    LinearLayout photoContainer;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private ServiceController serviceController;
    private ArrayList<String> uris = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_service_activity);
        ButterKnife.bind(this);
        serviceController = ServiceController.getInstance(getApplicationContext());
        serviceController.setServiceEditListener(this);

        addPhotoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkAccess()) {
                    requestAccess();
                    return;
                }

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_photo)), PICK_IMAGE_GALLERY_REQUEST);
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putStringArrayList("uris", uris);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            uris = savedInstanceState.getStringArrayList("uris");
        }

        if (uris == null) {
            uris = new ArrayList<>();
        }
        for (ListIterator<String> i = uris.listIterator(); i.hasNext(); ) {
            String uri = i.next();
            try {
                addURI(Uri.parse(uri), true);
            } catch (IOException e) {
                i.remove();
            }
        }
    }

    @OnClick(R.id.save)
    public void sendService() {
        String nameStr = name.getText().toString();
        String descriptionStr = description.getText().toString();
        String priceStr = price.getText().toString();
        if (nameStr.isEmpty() || descriptionStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        saveButton.setEnabled(false);

        service.setName(nameStr);
        service.setDescription(descriptionStr);
        service.setPrice(Integer.valueOf(priceStr));
        service.setPhotos(encodedPhotos);

        NewServiceRequest request = new NewServiceRequest(service);
        serviceController.createService(request);
    }

    @Override
    public void onRequestComplete(boolean success) {
        progressBar.setVisibility(View.GONE);
        saveButton.setEnabled(true);
        if (!success) {
            Toast.makeText(getApplicationContext(), R.string.error_occured, Toast.LENGTH_SHORT).show();
            return;
        }
        finish();
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
                addURI(uri, false);
                saveButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                Log.e(TAG, "Failed to pick image: " + e.getMessage());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceController.setServiceEditListener(null);
    }

    private void addURI(Uri uri, boolean restore) throws IOException {
        final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        addPhotoToScreen(bitmap);
        photoProcessor.execute(new PhotoEncoder(bitmap));
        if (!restore) {
            uris.add(uri.toString());
        }
    }

    private void addPhotoToScreen(Bitmap bitmap) {
        ImageView image = new ImageView(getApplicationContext());
        int size = (int) getResources().getDimension(R.dimen.image_size);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float k = (float) size / height;
        image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, Math.round(width * k), Math.round(height * k), false));
        photoContainer.addView(image);
    }

    private void onPhotoProcessed() {
        UiThread.run(new Runnable() {
            @Override
            public void run() {
                saveButton.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean checkAccess() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestAccess() {
        if (!checkAccess()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_photo)), PICK_IMAGE_GALLERY_REQUEST);
            } else {
                Toast.makeText(this, R.string.need_permission, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class PhotoEncoder implements Runnable {
        private Bitmap bitmap;

        private PhotoEncoder(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        public void run() {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                String encoded = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);
                stream.close();
                encodedPhotos.add(encoded);
            } catch (IOException e) {
                Log.e(TAG, "Failed to compress image: " + e.getMessage());
            }
            onPhotoProcessed();
        }
    }
}

