package com.example.gimnasio_unne.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.gimnasio_unne.Login;
import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.model.Provincias;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class FragmentMiPerfil extends Fragment {

    TextInputLayout etlu,etdni,etnya, etestadocivil,etemail, etFacultades;
    Spinner spinnerProvincias, spinnerSexos;
    Button btn;
    private String idprovincia, sexoBD,idpersona, facultad;
    String [] sexos;
    private AsyncHttpClient cliente;
    //DatePickerDialog datePickerDialog;
    public FragmentMiPerfil() {   }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_mi_perfil, container, false);
        etlu = view.findViewById(R.id.etLuMiPerfil);
        etdni = view.findViewById(R.id.etDniMiPerfil);
        etnya= view.findViewById(R.id.etAyNMiPerfil);
        etestadocivil= view.findViewById(R.id.etEstadoCivilMiPerfil);
        etemail= view.findViewById(R.id.etEmailMiPerfil);
        spinnerProvincias = view.findViewById(R.id.spPciaMiPerfil);
        spinnerSexos = view.findViewById(R.id.spSexoMiPerfil);
        etFacultades= view.findViewById(R.id.etFacultadMiPerfil);
        btn= view.findViewById(R.id.btnModificarMiPerfil);

        getSharedPreferences();
        if(sexoBD.equals("2")) {
            sexos= new String [] {"Femenino", "Masculino"};
        } else if(sexoBD.equals("1")) {
            sexos = new String [] {"Masculino", "Femenino"};
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, sexos);
        spinnerSexos.setAdapter(adapter);

        //llenarSpinnerProvincias();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = con.getActiveNetworkInfo();
                if(networkInfo!=null && networkInfo.isConnected()) {
                    /*if (validarCampos()) {
                        actualizar("http://medinamagali.com.ar/gimnasio_unne/editarpersona.php");
                    }*/
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "No se pudo conectar, revise el " +
                            "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    //que pasa con el recordar usuario y contraseña y sin??
    public void getSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("datosusuario",Context.MODE_PRIVATE);
        etnya.getEditText().setText(sharedPreferences.getString("nya", ""));
        etdni.getEditText().setText(sharedPreferences.getString("dni", ""));
        etlu.getEditText().setText(sharedPreferences.getString("lu", ""));
        etestadocivil.getEditText().setText(sharedPreferences.getString("estado_civil", ""));
        sexoBD = sharedPreferences.getString("sexo", "");
        //idprovincia = sharedPreferences.getString("provincia", "");
        etemail.getEditText().setText(sharedPreferences.getString("email",""));
        idpersona= sharedPreferences.getString("personas_id", "");
        facultad= sharedPreferences.getString("facultad_id", "");
        if(facultad.equals("5")) {
            etFacultades.getEditText().setText("Humanidades");
        } else  {
            etFacultades.getEditText().setText("Ciencias Exactas");
        }
    }

    public void actualizar(String URL) {
        String seleccion = spinnerSexos.getSelectedItem().toString();
        if(seleccion.equals("Masculino")) {
            sexoBD= "1";
        }
        else if(seleccion.equals("Femenino")) {
            sexoBD="2";
        }
        StringRequest request=new StringRequest(Request.Method.POST, URL
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.length()==0) {
                    Toast.makeText(getActivity().getApplicationContext(), "Modificado correctamente", Toast.LENGTH_LONG).show();

                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "Usuario existente con ese DNI", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros= new HashMap<String, String>();
                parametros.put("persona_id", idpersona);
                parametros.put("sexo_id", sexoBD);
                parametros.put("provincia", idprovincia);
                //parametros.put("estado_civil", etestadocivil.getText().toString());
                //parametros.put("email", etemail.getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(request);
    }

    private void llenarSpinnerProvincias() {
        String url = "https://medinamagali.com.ar/gimnasio_unne/consultarprovincias.php?persona_id="+idpersona+"";
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
            ArrayAdapter<Provincias> provincias = new ArrayAdapter<Provincias>(getActivity().getApplicationContext(), android.R.
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

   public boolean validarCampos() {
        if (etestadocivil.toString().isEmpty()) {
            etestadocivil.setError("Ingrese estado civil");
            return false;
        }
        if (etemail.toString().isEmpty()) {
            etemail.setError("Ingrese email");
            return false;
        }
        return true;
    }
}