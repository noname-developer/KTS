package com.example.kts.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.kts.CenteredTitleToolbar;
import com.example.kts.R;
import com.example.kts.ui.login.choiceOfGroup.ChoiceOfGroupSharedViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements OnScrollListener {

    private DrawerLayout drawerLayout;
    private MainViewModel viewModel;
    private AppBarLayout appBarLayout;
    private CenteredTitleToolbar toolbar;
    private AppBarLayout.LayoutParams params;
    private BottomNavigationView bottomNavView;
    private int optionsMenuId;
    private Menu toolbarMenu;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        bottomNavView = findViewById(R.id.bottom_nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.menu_timetable, R.id.menu_home, R.id.menu_group)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavView, navController);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar_main);
        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        appBarLayout = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        bottomNavView.setOnNavigationItemReselectedListener(item -> {
            //Nothing
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                viewModel.onDestinationChanged(destination.getId());
        });

        viewModel.appbarExpand.observe(this, expanded -> {
            appBarLayout.setExpanded(expanded);
        });

        viewModel.appbarShadowVisibility.observe(this, visibility -> appBarLayout.setSelected(visibility));

        viewModel.appBarFlags.observe(this, scrollFlags -> {
            toolbar.postDelayed(() -> {
                params.setScrollFlags(scrollFlags);
                toolbar.setLayoutParams(params);
            }, 400);
        });

        viewModel.currentMenuOptions.observe(this, optionsMenuId -> {
            Toast.makeText(this, "change menu", Toast.LENGTH_SHORT).show();
            this.optionsMenuId = optionsMenuId;
            invalidateOptionsMenu();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        viewModel.onOptionItemSelect(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(@NotNull Menu menu) {
        menu.clear();
        getMenuInflater().inflate(optionsMenuId, menu);
        boolean optionsMenu = super.onPrepareOptionsMenu(menu);
        toolbarMenu = toolbar.getMenu();
        return optionsMenu;
    }

    @Override
    public void onScroll(boolean scrollVertically, int range, int height) {
        viewModel.onScroll(scrollVertically, range, height);
    }
}