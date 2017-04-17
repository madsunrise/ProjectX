package com.github.projectx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.projectx.R;
import com.github.projectx.fragment.FeedFragment;
import com.github.projectx.fragment.ServiceEditorFragment;
import com.github.projectx.network.BaseController;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.github.projectx.utils.Constants.NAV_DRAWER_ADD_SERVICE;
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
        changeFragment(feedFragment, false);
    }


    private void changeFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
    }


    private void onDrawerPushed(int itemId) {
        switch (itemId) {
            case NAV_DRAWER_ARTISTS_SEARCH: {
                changeFragment(feedFragment, false);
                break;
            }
            case NAV_DRAWER_ADD_SERVICE: {
                if (BaseController.authorized(getApplicationContext())) {
                    changeFragment(new ServiceEditorFragment(), true);
                } else {
                    Intent intent = new Intent(this, AuthActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                break;
            }
            default:
                break;
        }
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        }
    }

    private void setUpNavDrawer() {
        PrimaryDrawerItem artists = new PrimaryDrawerItem()
                .withName(R.string.artists_search)
                .withIdentifier(NAV_DRAWER_ARTISTS_SEARCH);
        PrimaryDrawerItem messages = new PrimaryDrawerItem()
                .withName(R.string.my_messages)
                .withIdentifier(NAV_DRAWER_MESSAGES);
        PrimaryDrawerItem addService = new PrimaryDrawerItem()
                .withName(R.string.add_service)
                .withSelectable(false)
                .withIdentifier(NAV_DRAWER_ADD_SERVICE);
        PrimaryDrawerItem settings = new PrimaryDrawerItem()
                .withName(R.string.settings)
                .withIdentifier(NAV_DRAWER_SETTINGS);

        drawer = new DrawerBuilder()
                .withToolbar(toolbar)
                .withActivity(this)
                .addDrawerItems(
                        artists,
                        messages,
                        addService,
                        new DividerDrawerItem(),
                        settings
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        onDrawerPushed((int) drawerItem.getIdentifier());
                        return false;
                    }
                })
                .build();
    }
}
