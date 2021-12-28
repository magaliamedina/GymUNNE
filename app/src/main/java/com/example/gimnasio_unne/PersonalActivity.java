package com.example.gimnasio_unne;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;

import com.example.gimnasio_unne.view.fragments.FragmentReservasPendientes;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PersonalActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    ActionBarDrawerToggle toggle;
    //NO FUNCIONA PARA LO QUE NECESITO
    FragmentManager fragmentManager;
    FragmentReservasPendientes fragmentReservasPendientes= new FragmentReservasPendientes();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //para cerrar sesion con shared preferences
        preferences=getSharedPreferences("sesiones", Context.MODE_PRIVATE);
        editor = preferences.edit();

        //drawer layout ver nombre
        DrawerLayout drawer = findViewById(R.id.drawer_layout_personal);
        //id del navigation en activity_personal
        NavigationView navigationView = findViewById(R.id.nav_view_personal);

        navigationView.getMenu().findItem(R.id.logoutPersonal).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                PersonalActivity.this.logout();
                return true;
            }
        });

        //clase para implementar el icono hamburguesa
        toggle = new ActionBarDrawerToggle(this, drawer,toolbar, R.string.nav_app_bar_open_drawer_description, R.string.drawer_close);
        drawer.addDrawerListener(toggle);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                //identificador del menu que se encuentra en menu_personal y mobile_navigation_personal
                R.id.fragmentReservasPendientes, R.id.fragmentAltaCuposLibres, R.id.fragmentPersonalCuposLibres,
                R.id.logoutPersonal)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_personal);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //NO FUNCIONA
        fragmentManager= getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .add(R.id.fragment_reservas_pendientes, fragmentReservasPendientes)
                .show(fragmentReservasPendientes)
                .commit();
    }

    private void logout() {
        editor.putBoolean("sesion", false);
        editor.apply();
        finish();
        startActivity(new Intent(this, Login.class));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_personal);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}
