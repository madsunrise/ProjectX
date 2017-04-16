package com.github.projectx.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.projectx.R;
import com.github.projectx.fragment.FeedFragment;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.github.projectx.utils.Constants.NAV_DRAWER_ARTISTS_SEARCH;
import static com.github.projectx.utils.Constants.NAV_DRAWER_MESSAGES;
import static com.github.projectx.utils.Constants.NAV_DRAWER_SETTINGS;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Drawer drawer;
    private FeedFragment feedFragment = new FeedFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpNavDrawer();
        changeFragment(feedFragment);
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
