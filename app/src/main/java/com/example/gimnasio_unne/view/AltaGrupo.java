package com.example.gimnasio_unne.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.model.Horarios;
import com.example.gimnasio_unne.model.Personas;
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class AltaGrupo extends AppCompatActivity {
    EditText ettotalcupos, etdescripcion;
    Button btnguardar;
    RequestQueue requestQueue;
    private Spinner spinnerprof, spinnerhorario;
    private AsyncHttpClient cliente, cliente2;
    private String idprof, idhorario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_grupo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etdescripcion = findViewById(R.id.etnombrealtagrupo);
        spinnerhorario= findViewById(R.id.spinnerhorarioaltagrupo);
        spinnerprof = findViewById(R.id.spinnerProfaltagrupo);
        cliente = new AsyncHttpClient();
        cliente2 = new AsyncHttpClient();

        ettotalcupos = findViewById(R.id.ettotalcuposaltagrupo);
        btnguardar = findViewById(R.id.btnaltagrupo);
        llenarSpinner();
        llenarSpinnerHorario();
        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = con.getActiveNetworkInfo();
                if(networkInfo!=null && networkInfo.isConnected()) {
                    if (validarCampos()) {
                        altagrupo("http://medinamagali.com.ar/gimnasio_unne/altagrupo.php");
                    }
                } //FIN IF TIENE CONEXION A INTERNET
                else {
                    Toast.makeText(AltaGrupo.this, "No se pudo conectar, revise el " +
                            "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public boolean validarCampos() {
        if(ettotalcupos.getText().toString().isEmpty()) {
            ettotalcupos.setError("Ingrese cupo total");
            return false;
        }
        if(etdescripcion.getText().toString().isEmpty()) {
            etdescripcion.setError("Ingrese una descripción");
            return false;
        }
        return true;
    }

    private void llenarSpinner() {
        String url = "https://medinamagali.com.ar/gimnasio_unne/consultar_profesor_altagrupo.php";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode== 200) {
                    cargarSpinnerProfesor(new String(responseBody));
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) { }
        });
    }

    private void llenarSpinnerHorario() {
        String url = "https://medinamagali.com.ar/gimnasio_unne/consultarhorarios.php";
        cliente2.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode== 200) {
                    cargarSpinnerHorario(new String(responseBody));
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) { }
        });
    }

    private void cargarSpinnerProfesor(String respuesta) {
        final ArrayList<Personas> listaPersonas = new ArrayList<Personas>();
        try {
            JSONArray jsonArray = new JSONArray(respuesta);
            for (int i= 0; i< jsonArray.length();i++){
                Personas p = new Personas();
                p.setNombres(jsonArray.getJSONObject(i).getString("nombres"));
                p.setApellido(jsonArray.getJSONObject(i).getString("apellido"));
                p.setId(jsonArray.getJSONObject(i).getString("personas_id"));
                //en el metodo tostring de la clase persona se define lo que se va a mostrar
                listaPersonas.add(p);
            }
            ArrayAdapter<Personas> personas = new ArrayAdapter<Personas>(this, android.R.
                    layout.simple_dropdown_item_1line, listaPersonas);
            spinnerprof.setAdapter(personas);
            spinnerprof.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                //para saber la posicion del elemento seleccionado en el spinner
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    idprof= listaPersonas.get(position).getId();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarSpinnerHorario(String respuesta) {
        final ArrayList<Horarios> listaHorarios = new ArrayList<Horarios>();
        try {
            JSONArray jsonArray = new JSONArray(respuesta);

            for (int i= 0; i< jsonArray.length();i++){
                Horarios p = new Horarios();
                p.setHoraInicio(jsonArray.getJSONObject(i).getString("hora_inicio"));
                p.setHoraFin(jsonArray.getJSONObject(i).getString("hora_fin"));
                p.setId(jsonArray.getJSONObject(i).getString("horario_id"));
                //en el metodo tostring de la clase persona se define lo que se va a mostrar
                listaHorarios.add(p);
            }
            ArrayAdapter<Horarios> horarios = new ArrayAdapter<Horarios>(this, android.R.
                    layout.simple_dropdown_item_1line, listaHorarios);
            spinnerhorario.setAdapter(horarios);
            spinnerhorario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    idhorario= listaHorarios.get(position).getId();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void altagrupo(String URL) {
        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.length()==0) {
                    Toast.makeText(getApplicationContext(), "Alta de grupo exitosa", Toast.LENGTH_SHORT).show();
                    etdescripcion.setText("");
                    ettotalcupos.setText("");
                }
                else {
                    Toast.makeText(getApplicationContext(), "Ya existe un grupo en ese horario", Toast.LENGTH_LONG).show();
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
                parametros.put("horario_id", idhorario);
                parametros.put("profesor_id", idprof);
                parametros.put("total_cupos", ettotalcupos.getText().toString());
                parametros.put("descripcion", etdescripcion.getText().toString());
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(AltaGrupo.this);
        requestQueue.add(stringRequest);
    }
}
