package com.example.gimnasio_unne.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.model.Personas;
import com.example.gimnasio_unne.view.adapter.AdaptadorPersonas;
import com.example.gimnasio_unne.view.fragments.FragmentPersonalCuposLibres;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReservasConfirmadas extends AppCompatActivity {
    private ListView list;
    public static ArrayList<Personas> arrayList= new ArrayList<>();
    String url;
    AdaptadorPersonas adaptador;
    Personas personas;
    TextView tv;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservas_confirmadas);
        list = findViewById(R.id.lv_reservasConfirmadas);
        tv= findViewById(R.id.tvAlumnosInscriptosAlCupo);
        progressBar=findViewById(R.id.progressBarReservasConfirmadas);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        adaptador= new AdaptadorPersonas(getApplicationContext(), arrayList);
        list.setAdapter(adaptador);
        Intent intent =getIntent();
        Integer position=intent.getExtras().getInt("position");
        String cupolibre= FragmentPersonalCuposLibres.arrayCuposLibres.get(position).getId_cupolibre();
        url= "https://medinamagali.com.ar/gimnasio_unne/reservas_confirmadas.php?cupolibre_id="+cupolibre;
        mostrarDatos();
    }

    public void mostrarDatos() {
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                arrayList.clear();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    progressBar.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                        for (int i=0;i<jsonArray.length();i++) {
                            String id= jsonArray.getJSONObject(i).getString("personas_id");
                            String apellido= jsonArray.getJSONObject(i).getString("apellido");
                            String nombres= jsonArray.getJSONObject(i).getString("nombres");
                            String dni = jsonArray.getJSONObject(i).getString("dni");
                            String estado=jsonArray.getJSONObject(i).getString("estado");
                            personas = new Personas(id, apellido, nombres, dni, estado);
                            arrayList.add(personas);
                            adaptador.notifyDataSetChanged();
                        }
                    if(jsonArray.length()==0) {
                        tv.setVisibility(View.VISIBLE);
                        list.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
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
