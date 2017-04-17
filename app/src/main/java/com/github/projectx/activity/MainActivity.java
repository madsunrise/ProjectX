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
import com.github.projectx.fragment.ServiceFragment;
import com.github.projectx.model.Service;
import com.github.projectx.network.BaseController;
import com.github.projectx.utils.Constants;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements FeedFragment.CHF {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Drawer drawer;
    private FeedFragment feedFragment = new FeedFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        changeFragment(feedFragment, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        feedFragment.update();
        setupNavDrawer();
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


    private void onMenuItemClick(int itemId) {
        Constants.Menu item = Constants.Menu.values()[itemId];
        switch (item) {
            case SEARCH_SERVICE:
                changeFragment(feedFragment, false);
                break;
            case ADD_SERVICE:
                if (!BaseController.isAuthorized(getApplicationContext())) {
                    startActivityForResult(new Intent(this, AuthActivity.class), 0);
                } else {
                    startActivity(new Intent(this, NewServiceActivity.class));
                }
                break;
            case MESSAGES:
                break;
            case SETTINGS:
                break;
            case LOGOUT:
                BaseController.resetAuth(getApplicationContext());
                setupNavDrawer();
                break;
            default:
                break;
        }
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            startActivity(new Intent(this, NewServiceActivity.class));
        }
    }

    private void setupNavDrawer() {
        String[] rows = getResources().getStringArray(R.array.menu);
        List<IDrawerItem> list = new ArrayList<>();
        int index = 0;
        for (String row : rows) {
            if (row.equals("-")) {
                list.add(new DividerDrawerItem());
            } else if (index == Constants.Menu.LOGOUT.ordinal() && !BaseController.isAuthorized(getApplicationContext())) {
                index++;
            } else {
                list.add(new PrimaryDrawerItem()
                        .withName(row)
                        .withIdentifier(index++));
            }
        }
        IDrawerItem[] items = new IDrawerItem[list.size()];
        list.toArray(items);
        toolbar.setTitle(BaseController.isAuthorized(getApplicationContext()) ? "Authorized" : "Not authorized");
        drawer = new DrawerBuilder()
                .withToolbar(toolbar)
                .withActivity(this)
                .addDrawerItems(items)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        onMenuItemClick((int) drawerItem.getIdentifier());
                        return false;
                    }
                })
                .build();
    }

    @Override
    public void openService(Service service) {
        ServiceFragment sf = new ServiceFragment();
        Bundle args = new Bundle();
        args.putLong("id", service.getId());
        sf.setArguments(args);
        changeFragment(sf, true);
    }
}
