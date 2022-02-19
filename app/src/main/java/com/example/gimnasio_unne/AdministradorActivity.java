package com.example.gimnasio_unne;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AdministradorActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    ActionBarDrawerToggle toggle;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //para cerrar sesion con shared preferences
        preferences=getSharedPreferences("sesiones", Context.MODE_PRIVATE);
        editor = preferences.edit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.getMenu().findItem(R.id.logoutadmin).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                AdministradorActivity.this.logout();
                return true;
            }
        });

        //clase para implementar el icono hamburguesa
        toggle = new ActionBarDrawerToggle(this, drawer,toolbar,
                R.string.nav_app_bar_open_drawer_description, R.string.drawer_close);
        drawer.addDrawerListener(toggle);

        //se cargan los id de los fragments que se van a mostrar
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.fragmentListarGrupos, R.id.fragmentListarPersonas,
                R.id.fragmentListarPersonal, R.id.fragmentHorarios, R.id.fragmentCupos, R.id.fragmentReportesAlumnosPorGenero,
                R.id.fragmentReportesAlumnosPorFacultad,R.id.fragmentReporteAlumnosPorGrupo,
                R.id.fragmentCambiarPassword, R.id.logoutadmin)
                .setDrawerLayout(drawer)
                .build();

        //Navigation component:
        //NavController para establecer los llamados a las diferentes pantallas
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
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
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                editor.putBoolean("sesion", false);
                editor.apply();
                finish();
                startActivity(new Intent(this, Login.class));
                break;
        }
        if(toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return true;
    }*/
}
