package com.example.gimnasio_unne.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.Utiles;
import com.example.gimnasio_unne.model.Provincias;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class AltaAlumno extends AppCompatActivity {

    EditText etdni,etapellido, etnombres, etestadocivil,etemail, etpassword;
    Spinner spinnerProvincias, spinnerSexos;
    Button btn, btnDate;
    private AsyncHttpClient cliente;
    private String idprovincia, sexoBD;
    RequestQueue requestQueue;
    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_alumno);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etdni = findViewById(R.id.etdnialtaalumno);
        etapellido= findViewById(R.id.etapellidoaltaalumno);
        etnombres= findViewById(R.id.etnombresaltaalumno);
        etestadocivil= findViewById(R.id.etestadocivilaltaalumno);
        etemail= findViewById(R.id.etemailaltaalumno);
        etpassword=findViewById(R.id.etpassaltaalumno);
        spinnerProvincias = findViewById(R.id.spinnerProvinciaAltaAlumno);
        spinnerSexos=findViewById(R.id.spsexoaltaalumno);
        btnDate= findViewById(R.id.btn_date);
        btn= findViewById(R.id.btnaltaalumno);
        cliente = new AsyncHttpClient();

        llenarSpinnerProvincias();

        String [] sexos = {"Masculino", "Femenino"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sexos);
        spinnerSexos.setAdapter(adapter);
        try {
            initDatePicker();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarCampos()) {
                    altapersona("http://medinamagali.com.ar/gimnasio_unne/altapersona.php");
                }
            }
        });
    }

    private void initDatePicker() throws ParseException {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month= month+1;
                String date= year+"-"+month+"-"+day;
                btnDate.setText(date);
            }
        };
        Calendar calendar = Calendar.getInstance();
        int style= AlertDialog.THEME_HOLO_LIGHT;
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this, style,dateSetListener,year,month,day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long dateMillis;
        Date dateHoy;
        dateHoy= sdf.parse(Utiles.obtenerFechaActual("GMT -3"));
        dateMillis=dateHoy.getTime();
        datePickerDialog.getDatePicker().setMaxDate(dateMillis);

        datePickerDialog.getDatePicker().init(1990, 00, 01, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
            }
        });
    }

    private void llenarSpinnerProvincias() {
        String url = "https://medinamagali.com.ar/gimnasio_unne/consultarprovincias.php";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode== 200) {
                    cargarSpinnerProvincias(new String(responseBody));
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) { }
        });
    }

    private void cargarSpinnerProvincias(String respuesta) {
        final ArrayList<Provincias> listaProvincias = new ArrayList<Provincias>();
        try {
            JSONArray jsonArray = new JSONArray(respuesta);

            for (int i= 0; i< jsonArray.length();i++){
                Provincias p = new Provincias();
                //las claves son los nombres de los campos de la BD
                p.setId(jsonArray.getJSONObject(i).getString("provincia_id"));
                p.setProvincia(jsonArray.getJSONObject(i).getString("descripcion"));
                //en el metodo tostring de la clase provincia se define lo que se va a mostrar
                listaProvincias.add(p);
            }
            ArrayAdapter<Provincias> provincias = new ArrayAdapter<Provincias>(this, android.R.
                    layout.simple_dropdown_item_1line, listaProvincias);
            spinnerProvincias.setAdapter(provincias);
            spinnerProvincias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    idprovincia= listaProvincias.get(position).getId();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void altapersona(String URL) {
        String seleccion = spinnerSexos.getSelectedItem().toString();
        if(seleccion.equals("Masculino")) {
            sexoBD= "1";
        }
        if(seleccion.equals("Femenino")) {
            sexoBD="2";
        }
        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.length()==0){
                    Toast.makeText(getApplicationContext(), "Alta exitosa", Toast.LENGTH_SHORT).show();
                    etdni.setText("");
                    etapellido.setText("");
                    etnombres.setText("");
                    etestadocivil.setText("");
                    etemail.setText("");
                    etpassword.setText("");
                }
                else {
                    Toast.makeText(getApplicationContext(), "Usuario existente con ese DNI", Toast.LENGTH_SHORT).show();
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
                parametros.put("dni", etdni.getText().toString());
                parametros.put("apellido", etapellido.getText().toString());
                parametros.put("nombres", etnombres.getText().toString());
                parametros.put("sexo_id", sexoBD);
                parametros.put("fecha_nac", btnDate.getText().toString());
                parametros.put("provincia", idprovincia);
                parametros.put("estado", "1");
                parametros.put("estado_civil", etestadocivil.getText().toString());
                parametros.put("usuario_id", "3"); //usuario alumno
                parametros.put("email", etemail.getText().toString());
                parametros.put("password", etpassword.getText().toString());
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(AltaAlumno.this);
        requestQueue.add(stringRequest);
    }

    public boolean validarCampos() {
        if(etdni.getText().toString().isEmpty()) {
            etdni.setError("Ingrese DNI");
            return false;
        }
        if(etapellido.getText().toString().isEmpty()) {
            etapellido.setError("Ingrese apellido");
            return false;
        }
        if(etnombres.getText().toString().isEmpty()) {
            etnombres.setError("Ingrese nombres");
            return false;
        }
        if (etemail.getText().toString().isEmpty()) {
            etemail.setError("Ingrese email");
            return false;
        }
        if (etpassword.getText().toString().isEmpty())  {
            etpassword.setError("Ingrese contraseña");
            return false;
        }
        return true;
    }


}