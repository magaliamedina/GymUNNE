package com.example.gimnasio_unne.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
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
import com.example.gimnasio_unne.AdministradorActivity;
import com.example.gimnasio_unne.PersonalActivity;
import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.Utiles;
import com.example.gimnasio_unne.model.Provincias;
import com.example.gimnasio_unne.view.fragments.FragmentListarAlumnos;
import com.example.gimnasio_unne.view.fragments.FragmentListarAlumnos;
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

public class EditarAlumno extends AppCompatActivity {

    EditText etdni,etapellido, etnombres, etestadocivil,etemail, etpassword, etestado, etlu;
    Spinner spinnerProvincias, spinnerSexos;
    Button btn, btn_date_editarAlumno;
    private AsyncHttpClient cliente;
    int position;
    private String idprovincia, sexoBD, id;
    String [] sexos;
    DatePickerDialog datePickerDialog;
    String fechaNacimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_alumno);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etdni = findViewById(R.id.etDnieditaralumno);
        etapellido= findViewById(R.id.etApellidoeditaralumno);
        etnombres= findViewById(R.id.etNombreseditaralumno);
        etestadocivil= findViewById(R.id.etEstadoCivilEditaralumno);
        etemail= findViewById(R.id.etEmailEditarAlumno);
        etpassword=findViewById(R.id.etPasswordEditarAlumno);
        etestado= findViewById(R.id.etEstadoEditarAlumno);
        etlu=findViewById(R.id.etLueditaralumno);
        spinnerProvincias = findViewById(R.id.spinnerProvinciaEditarAlumno);
        spinnerSexos = findViewById(R.id.spinnerSexosEditarAlumno);
        btn= findViewById(R.id.btneditaralumno);
        btn_date_editarAlumno= findViewById(R.id.btn_date_editarAlumno);
        cliente = new AsyncHttpClient();

        Intent intent =getIntent();
        position=intent.getExtras().getInt("position");
        id= FragmentListarAlumnos.persons.get(position).getId();
        fechaNacimiento= FragmentListarAlumnos.persons.get(position).getFechaNac();

        etdni.setText(FragmentListarAlumnos.persons.get(position).getDni());
        etapellido.setText(FragmentListarAlumnos.persons.get(position).getApellido());
        etnombres.setText(FragmentListarAlumnos.persons.get(position).getNombres());
        etestadocivil.setText(FragmentListarAlumnos.persons.get(position).getEstadoCivil());
        etemail.setText(FragmentListarAlumnos.persons.get(position).getEmail());
        etpassword.setText(FragmentListarAlumnos.persons.get(position).getPassword());
        etestado.setText(FragmentListarAlumnos.persons.get(position).getEstado());
        etlu.setText(FragmentListarAlumnos.persons.get(position).getLu());
        btn_date_editarAlumno.setText(FragmentListarAlumnos.persons.get(position).getFechaNac());

        String sexo_guardado= FragmentListarAlumnos.persons.get(position).getSexo();
        //lo siguiente es para mostrar en orden como esta guardado
        if(sexo_guardado.equals("2")) {
            sexos= new String [] {"Femenino", "Masculino"};
        } else if(sexo_guardado.equals("1")) {
            sexos = new String [] {"Masculino", "Femenino"};
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sexos);
        spinnerSexos.setAdapter(adapter);

        llenarSpinnerProvincias();

        try {
            initDatePicker();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        btn_date_editarAlumno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = con.getActiveNetworkInfo();
                if(networkInfo!=null && networkInfo.isConnected()) {
                    if (validarCampos()) {
                        actualizar("http://medinamagali.com.ar/gimnasio_unne/editarestudiante.php");
                    }
                }
                else {
                    Toast.makeText(EditarAlumno.this, "No se pudo conectar, revise el " +
                            "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
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
                btn_date_editarAlumno.setText(date);
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
        if(etlu.getText().toString().isEmpty()) {
            etlu.setError("Ingrese Libreta Universitaria");
            return false;
        }
        if (etemail.getText().toString().isEmpty()) {
            etemail.setError("Ingrese email");
            return false;
        }
        if (etpassword.getText().toString().isEmpty())  {
            etpassword.setError("Ingrese contrase??a");
            return false;
        }
        if (etestado.getText().toString().isEmpty())  {
            etestado.setError("Ingrese estado");
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(etemail.getText()).matches()) { //si no es correo electronico valido
            etemail.setError("Ingrese un correo v??lido");
            return false;
        }
        return true;
    }

    public void actualizar(String URL) {
        String seleccion = spinnerSexos.getSelectedItem().toString();
        if(seleccion.equals("Masculino")) {
            sexoBD= "1";
        }
        else if(seleccion.equals("Femenino")) {
            sexoBD="2";
        }
        final ProgressDialog progressDialog= new ProgressDialog(this);

        StringRequest request=new StringRequest(Request.Method.POST, URL
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.length()==0) {
                    Toast.makeText(EditarAlumno.this, "Estudiante modificado correctamente", Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Usuario existente con ese DNI", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditarAlumno.this, error.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros= new HashMap<String, String>();
                parametros.put("persona_id", id);
                parametros.put("dni", etdni.getText().toString());
                parametros.put("apellido", etapellido.getText().toString());
                parametros.put("nombres", etnombres.getText().toString());
                parametros.put("sexo_id", sexoBD);
                parametros.put("fecha_nac", btn_date_editarAlumno.getText().toString());
                parametros.put("provincia", idprovincia);
                parametros.put("estado", etestado.getText().toString());
                parametros.put("estado_civil", etestadocivil.getText().toString());
                parametros.put("email", etemail.getText().toString());
                parametros.put("password", etpassword.getText().toString());
                parametros.put("lu", etlu.getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(EditarAlumno.this);
        requestQueue.add(request);
    }

    private void llenarSpinnerProvincias() {
        String url = "https://medinamagali.com.ar/gimnasio_unne/consultarprovincias.php?persona_id="+id+"";
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