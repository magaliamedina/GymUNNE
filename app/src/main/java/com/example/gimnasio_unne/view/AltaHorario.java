package com.example.gimnasio_unne.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gimnasio_unne.R;

import java.util.HashMap;
import java.util.Map;

public class AltaHorario extends AppCompatActivity {

    EditText etHoraInicio, etHoraFin;
    Button btnguardar;
    RequestQueue requestQueue;
    String url= "http://medinamagali.com.ar/gimnasio_unne/altahorario.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_horario);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etHoraInicio = findViewById(R.id.etHoraInicioAltaHorario);
        etHoraFin=findViewById(R.id.etHoraFinAltaHorario);
        btnguardar = findViewById(R.id.btnAltaHorario);
        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = con.getActiveNetworkInfo();
                if(networkInfo!=null && networkInfo.isConnected()) {
                    if (validarCampos()) {
                        altaHorario(url);
                    }
                }
                else {
                    Toast.makeText(AltaHorario.this, "No se pudo conectar, revise el " +
                            "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean validarCampos() {
        if(etHoraInicio.getText().toString().isEmpty()) {
            etHoraInicio.setError("Ingrese una hora de inicio");
            return false;
        }
        if(etHoraFin.getText().toString().isEmpty()) {
            etHoraFin.setError("Ingrese una hora de fin");
            return false;
        }
        return true;
    }

    private void altaHorario(String URL) {
        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.length()==0) {
                    Toast.makeText(getApplicationContext(), "Alta exitosa", Toast.LENGTH_SHORT).show();
                    etHoraInicio.setText("");
                    etHoraFin.setText("");
                }
                else {
                    Toast.makeText(getApplicationContext(), "Ya existe un horario asignado", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("hora_inicio", etHoraInicio.getText().toString());
                parametros.put("hora_fin", etHoraFin.getText().toString());
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(AltaHorario.this);
        requestQueue.add(stringRequest);
    }

}
