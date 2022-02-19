package com.example.gimnasio_unne.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gimnasio_unne.AdministradorActivity;
import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.model.Horarios;
import com.example.gimnasio_unne.model.Personas;
import com.example.gimnasio_unne.view.fragments.FragmentListarGrupos;
import com.example.gimnasio_unne.view.fragments.FragmentListarProfesores;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class EditarGrupos extends AppCompatActivity {
    EditText etnombre,etcupototal, etestado;
    TextView tvid;
    Spinner spinnerProf, spinnerHorario;
    Button btn1;
    private AsyncHttpClient cliente, cliente2;
    int position;
    private String idprof, idhorario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_grupos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvid = findViewById(R.id.tvideditargrupo);
        etnombre = findViewById(R.id.etnombreeditargrupo);
        spinnerProf = findViewById(R.id.spinnerProfeditargrupo);
        spinnerHorario = findViewById(R.id.spinnerhorarioeditargrupo);
        etcupototal = findViewById(R.id.ettotaleditargrupo);
        etestado= findViewById(R.id.etEditarGrupoEstado);
        btn1= findViewById(R.id.btneditargrupo);
        cliente = new AsyncHttpClient();
        cliente2 = new AsyncHttpClient();

        Intent intent =getIntent();
        position=intent.getExtras().getInt("position");
        tvid.setText(FragmentListarGrupos.groups.get(position).getId());
        etnombre.setText(FragmentListarGrupos.groups.get(position).getDescripcion());
        etcupototal.setText(FragmentListarGrupos.groups.get(position).getCupototal());
        etestado.setText(FragmentListarGrupos.groups.get(position).getEstado());

        llenarSpinnerProfesor();
        llenarSpinnerHorario();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = con.getActiveNetworkInfo();
                if(networkInfo!=null && networkInfo.isConnected()) {
                    if (validarCampos()) {
                        actualizar("https://medinamagali.com.ar/gimnasio_unne/editargrupo.php");
                    }
                }
                else {
                    Toast.makeText(EditarGrupos.this, "No se pudo conectar, revise el " +
                            "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean validarCampos() {
        if(etcupototal.getText().toString().isEmpty()) {
            etcupototal.setError("Ingrese cupo total");
            return false;
        }
        if(etnombre.getText().toString().isEmpty()) {
            etnombre.setError("Ingrese una descripción");
            return false;
        }
        if(etestado.getText().toString().isEmpty()) {
            etestado.setError("Ingrese un estado");
            return false;
        }
        if(Integer.parseInt(etestado.getText().toString()) > 1) {
            etestado.setError("Ingrese '0': inactivo o '1': activo");
            return false;
        }
        return true;
    }

    public void actualizar(String URL) {
        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Cargando....");
        progressDialog.show();

        StringRequest request=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(EditarGrupos.this, "Grupo modificado correctamente", Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(getApplicationContext(), AdministradorActivity.class));
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditarGrupos.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params= new HashMap<String, String>();
                params.put("grupo_id", tvid.getText().toString());
                params.put("horario_id", idhorario);
                params.put("profesor_id", idprof);
                params.put("total_cupos", etcupototal.getText().toString());
                params.put("descripcion", etnombre.getText().toString());
                params.put("estado", etestado.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(EditarGrupos.this);
        requestQueue.add(request);
    }

    private void llenarSpinnerHorario() {
        String url = "https://medinamagali.com.ar/gimnasio_unne/consultarhorarios.php?grupo_id="+tvid.getText().toString()+"";
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

            spinnerHorario.setAdapter(horarios);
            spinnerHorario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void llenarSpinnerProfesor() {
        String url = "https://medinamagali.com.ar/gimnasio_unne/consultarProfesor.php?grupo_id="+tvid.getText().toString()+"";
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
            spinnerProf.setAdapter(personas);
            spinnerProf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
