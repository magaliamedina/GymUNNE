package com.example.gimnasio_unne.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import com.example.gimnasio_unne.PersonalActivity;
import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.model.Grupos;
import com.example.gimnasio_unne.view.fragments.FragmentListarCuposLibres;
import com.example.gimnasio_unne.view.fragments.FragmentPersonalCuposLibres;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class EditarCupoLibre extends AppCompatActivity {
    private AsyncHttpClient cliente;
    TextView tvFechaReserva;
    Spinner spinnerGrupos;
    String idgrupo, idcupolibre;
    EditText etTotalCupos, etEstado, etmes, etanio;
    Button btnguardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cupo_libre);
        btnguardar=findViewById(R.id.btnEditarCupoLibre);
        tvFechaReserva=findViewById(R.id.tvEditarCuposLibresFechaReserva);
        etTotalCupos=findViewById(R.id.etEditarCuposLibresTotalCupos);
        etmes=findViewById(R.id.etEditarCuposLibresMes);
        etanio=findViewById(R.id.etEditarCuposLibresAnio);
        spinnerGrupos=findViewById(R.id.spinnerEditarCuposLibresGrupos);
        etEstado = findViewById(R.id.etEditarCuposLibresEstado);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        cliente = new AsyncHttpClient();
        Intent intent =getIntent();
        Integer position=intent.getExtras().getInt("position");
        idcupolibre= FragmentPersonalCuposLibres.arrayCuposLibres.get(position).getId_cupolibre();
        llenarSpinnerGrupo();
        tvFechaReserva.setText(FragmentPersonalCuposLibres.arrayCuposLibres.get(position).getFecha_reserva());
        etEstado.setText(FragmentPersonalCuposLibres.arrayCuposLibres.get(position).getEstado());
        etmes.setText(FragmentPersonalCuposLibres.arrayCuposLibres.get(position).getMes());
        etanio.setText(FragmentPersonalCuposLibres.arrayCuposLibres.get(position).getAnio());
        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = con.getActiveNetworkInfo();
                if(networkInfo!=null && networkInfo.isConnected()) {
                    if (validarCampos()) {
                        actualizar();
                    }
                }
                else {
                    Toast.makeText(EditarCupoLibre.this, "No se pudo conectar, revise el " +
                            "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean validarCampos() {
        if(etTotalCupos.getText().toString().isEmpty()) {
            etTotalCupos.setError("Ingrese cupo total");
            return false;
        }
        if(etEstado.getText().toString().isEmpty()) {
            etEstado.setError("Ingrese un estado");
            return false;
        }
        if(etmes.getText().toString().isEmpty()) {
            etmes.setError("Ingrese un mes");
            return false;
        }
        if(etanio.getText().toString().isEmpty()) {
            etanio.setError("Ingrese un año");
            return false;
        }
        if(Integer.parseInt(etEstado.getText().toString()) > 1) {
            etEstado.setError("Ingrese '0': inactivo o '1': activo");
            return false;
        }
        return true;
    }

    private void llenarSpinnerGrupo() {
        String url = "https://medinamagali.com.ar/gimnasio_unne/gruposdisponibles.php?id_cupolibre="+idcupolibre+"";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode== 200) {
                    cargarSpinnerGrupos(new String(responseBody));
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) { }
        });
    }

    private void cargarSpinnerGrupos(String respuesta) {
        final ArrayList<Grupos> listaGrupos = new ArrayList<Grupos>();
        try {
            JSONArray jsonArray = new JSONArray(respuesta);
            for (int i= 0; i< jsonArray.length();i++){
                Grupos g = new Grupos();
                JSONObject object= jsonArray.getJSONObject(i);
                g.setDescripcion(object.getString("descripcion"));
                g.setId(object.getString("grupo_id"));
                g.setHorario("de " + object.getString("hora_inicio") + " a " + object.getString("hora_fin"));
                g.setCupototal(object.getString("total_cupos"));
                //en el metodo tostring de la clase grupo se define lo que se va a mostrar
                listaGrupos.add(g);
            }
            ArrayAdapter<Grupos> grupos = new ArrayAdapter<Grupos>(this, android.R.
                    layout.simple_list_item_1, listaGrupos);
            spinnerGrupos.setAdapter(grupos);
            spinnerGrupos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                //para obtener la posicion del elemento seleccionado en el spinner
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    idgrupo= listaGrupos.get(position).getId();
                    etTotalCupos.setText(listaGrupos.get(position).getCupototal());
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actualizar() {
        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Cargando....");
        progressDialog.show();

        StringRequest request=new StringRequest(Request.Method.POST, "https://medinamagali.com.ar/gimnasio_unne/editar_cupolibre.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(EditarCupoLibre.this, "Modificado exitosamente", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(getApplicationContext(), PersonalActivity.class));
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditarCupoLibre.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros= new HashMap<String, String>();
                parametros.put("cupolibre_id", idcupolibre);
                parametros.put("grupo_id", idgrupo);
                parametros.put("total", etTotalCupos.getText().toString());
                parametros.put("estado", etEstado.getText().toString());
                parametros.put("mes", etmes.getText().toString());
                parametros.put("anio", etanio.getText().toString());

                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(EditarCupoLibre.this);
        requestQueue.add(request);
    }
}
