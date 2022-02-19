package com.example.gimnasio_unne.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gimnasio_unne.AlumnoActivity;
import com.example.gimnasio_unne.Login;
import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.Utiles;
import com.example.gimnasio_unne.view.fragments.FragmentListarCuposLibres;

import java.util.HashMap;
import java.util.Map;

public class Reservar extends AppCompatActivity {
    int position;
    TextView tvDescripcionGrupo, tvProfesor, tvDiayHora, tvTotalCuposLibres,
            tvNombreEstudiante, tvLUEstudiante, tvDiaYHoraActual;
    Button btnReservarCupoLibre;
    RequestQueue requestQueue;
    String idcupolibre, alumno_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvNombreEstudiante= findViewById(R.id.tvReservarNombreAlumno);
        tvLUEstudiante= findViewById(R.id.tvReservarLUEstudiante);
        tvDescripcionGrupo= findViewById(R.id.tvReservarDescripcionGrupo);
        tvProfesor = findViewById(R.id.tvReservarNombreProfesor);
        tvDiayHora= findViewById(R.id.tvReservarDiayHora);
        tvTotalCuposLibres = findViewById(R.id.tvReservarCuposLibresDisponible);
        btnReservarCupoLibre = findViewById(R.id.btnReservarCupoLibre);
        tvDiaYHoraActual = findViewById(R.id.tvReservarFechaReserva);

        Intent intent=getIntent();
        position= intent.getExtras().getInt("position");

        //caso normal sin recordar usuario y contraseña
        tvNombreEstudiante.setText("Nombre y apellido del estudiante: " + Login.nombres + " " + Login.apellido);
        alumno_id= Login.personas_id;
        tvLUEstudiante.setText("LU: " +Login.lu);
        if(Login.nombres.equals("")) {
            getSharedPreferences();
        }
        tvDescripcionGrupo.setText(FragmentListarCuposLibres.arrayCuposLibres.get(position).getGrupo_descripcion());
        tvProfesor.setText(FragmentListarCuposLibres.arrayCuposLibres.get(position).getProfesor_nombreYapellido());
        tvDiayHora.setText(FragmentListarCuposLibres.arrayCuposLibres.get(position).getHorarios_inicio_fin());
        idcupolibre= FragmentListarCuposLibres.arrayCuposLibres.get(position).getId_cupolibre();
        tvTotalCuposLibres.setText(FragmentListarCuposLibres.arrayCuposLibres.get(position).getCupolibre_total());
        tvDiaYHoraActual.setText("Fecha: " + Utiles.obtenerFechaActual("GMT-3")
                + " Hora: " + Utiles.obtenerHoraActual("GMT-3"));

        ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()) {
            btnReservarCupoLibre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reservarCupoLibre("https://medinamagali.com.ar/gimnasio_unne/reservar_cupo_libre.php");
                    descontarCupoLibre("https://medinamagali.com.ar/gimnasio_unne/descontar_cupo_libre.php");
                    finish();
                    Intent i = new Intent(getApplicationContext(), AlumnoActivity.class);
                    startActivity(i);
                }
            });
        }
        else {
            Toast.makeText(Reservar.this, "No se pudo conectar, revise el " +
                    "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
        }
    }

    private void reservarCupoLibre(String URL) {
        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Reserva exitosa", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //id del alumno (sesion), id del cupo libre, estado, fecha
                Map<String, String> parametros = new HashMap<String, String>();
                //Se trae el dato desde login con la clase SharedPreferences
                parametros.put("alumno_id", alumno_id);
                parametros.put("cupo_id", idcupolibre);
                parametros.put("estado", "0"); //estado pendiente
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void descontarCupoLibre(String URL) {
        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Se descontó exitosamente", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // id del cupo libre, cupototal
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("cupo_id", idcupolibre);
                //DESCUENTO DEL CUPO TOTAL - 1
                String cupoTotal_String = tvTotalCuposLibres.getText().toString();
                Integer cupoTotal_int= Integer.parseInt(cupoTotal_String);
                cupoTotal_int=cupoTotal_int -1;
                String cupoTotal_string = cupoTotal_int+"";
                parametros.put("cupo_disponible", cupoTotal_string);
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void getSharedPreferences() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("datosusuario",Context.MODE_PRIVATE);
        tvNombreEstudiante.setText("Nombre y apellido del estudiante: " + sharedPreferences.getString("nya", ""));
        alumno_id= sharedPreferences.getString("personas_id", "");
        tvLUEstudiante.setText("LU: " +sharedPreferences.getString("lu", ""));
    }

    //para el boton atras
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
