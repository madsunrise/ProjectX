package com.github.projectx.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.github.projectx.R;
import com.github.projectx.fragment.FeedFragment;
import com.github.projectx.model.Service;
import com.github.projectx.network.ServiceController;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.github.projectx.utils.Constants.NAV_DRAWER_ARTISTS_SEARCH;
import static com.github.projectx.utils.Constants.NAV_DRAWER_MESSAGES;
import static com.github.projectx.utils.Constants.NAV_DRAWER_SETTINGS;

public class MainActivity extends AppCompatActivity implements ServiceController.ServiceListCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Drawer drawer;
    private FeedFragment feedFragment = new FeedFragment();
    private ServiceController serviceController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceController = ServiceController.getInstance(getApplicationContext());
        serviceController.setServiceListCallback(this);
        ButterKnife.bind(this);

        setUpNavDrawer();

        changeFragment(feedFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        serviceController.queryForServiceList(null, null, 1, 15);
    }

    @Override
    public void onDataLoaded(List<Service> services) {
        Log.d(TAG, "Service list has been loaded");
        feedFragment.updateDataSet(services);
    }

    @Override
    public void dataLoadingFailed() {
        Log.d(TAG, "Loading service list from network failed!");
        Toast.makeText(this, R.string.error_occured, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serviceController.setServiceListCallback(null);
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commitAllowingStateLoss();
    }

    private void setUpNavDrawer() {
        PrimaryDrawerItem artists = new PrimaryDrawerItem()
                .withName(R.string.artists_search)
                .withIdentifier(NAV_DRAWER_ARTISTS_SEARCH);
        PrimaryDrawerItem messages = new PrimaryDrawerItem()
                .withName(R.string.my_messages)
                .withIdentifier(NAV_DRAWER_MESSAGES);
        PrimaryDrawerItem settings = new PrimaryDrawerItem()
                .withName(R.string.settings)
                .withIdentifier(NAV_DRAWER_SETTINGS);

        drawer = new DrawerBuilder()
                .withToolbar(toolbar)
                .withActivity(this)
                .addDrawerItems(
                        artists,
                        messages,
                        new DividerDrawerItem(),
                        settings
                ).build();
    }
}
