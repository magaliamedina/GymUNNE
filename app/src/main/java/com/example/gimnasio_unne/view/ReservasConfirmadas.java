package com.example.gimnasio_unne.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservas_confirmadas);
        list = findViewById(R.id.lv_reservasConfirmadas);
        TextView tv= findViewById(R.id.tvAlumnosInscriptosAlCupo);
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
                    list.setVisibility(View.VISIBLE);
                        for (int i=0;i<jsonArray.length();i++) {
                            String id= jsonArray.getJSONObject(i).getString("personas_id");
                            String apellido= jsonArray.getJSONObject(i).getString("apellido");
                            String nombres= jsonArray.getJSONObject(i).getString("nombres");
                            String dni = jsonArray.getJSONObject(i).getString("dni");
                            personas = new Personas(id, apellido, nombres, dni);
                            arrayList.add(personas);
                            adaptador.notifyDataSetChanged();
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
}
