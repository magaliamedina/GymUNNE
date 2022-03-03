package com.example.gimnasio_unne.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gimnasio_unne.AlumnoActivity;
import com.example.gimnasio_unne.R;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Map;

public class FragmentMiPerfil extends Fragment {

    TextInputLayout etlu,etdni,etnya, etestadocivil,etemail, etFacultades;
    Spinner spinnerSexos;
    Button btn;
    private String  sexoBD,idpersona, facultad;
    String [] sexos;
    private AsyncHttpClient cliente;
    String url="https://medinamagali.com.ar/gimnasio_unne/consulta_mi_perfil.php";
    public FragmentMiPerfil() {   }

    @Override
    public void onResume() {
        super.onResume();
        mostrarDatos();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_mi_perfil, container, false);
        etlu = view.findViewById(R.id.etLuMiPerfil);
        etdni = view.findViewById(R.id.etDniMiPerfil);
        etnya= view.findViewById(R.id.etAyNMiPerfil);
        etestadocivil= view.findViewById(R.id.etEstadoCivilMiPerfil);
        etemail= view.findViewById(R.id.etEmailMiPerfil);
        spinnerSexos = view.findViewById(R.id.spSexoMiPerfil);
        etFacultades= view.findViewById(R.id.etFacultadMiPerfil);
        btn= view.findViewById(R.id.btnModificarMiPerfil);
        cliente = new AsyncHttpClient();

        getSharedPreferences();

        if(sexoBD.equals("2")) {
            sexos= new String [] {"Femenino", "Masculino"};
        } else if(sexoBD.equals("1")) {
            sexos = new String [] {"Masculino", "Femenino"};
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, sexos);
        spinnerSexos.setAdapter(adapter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = con.getActiveNetworkInfo();
                if(networkInfo!=null && networkInfo.isConnected()) {
                    if (validarCampos()) {
                        actualizar("http://medinamagali.com.ar/gimnasio_unne/editar_miperfil.php");
                    }
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
                sexoBD = sharedPreferences.getString("sexo", "");
        idpersona= sharedPreferences.getString("personas_id", "");
        facultad= sharedPreferences.getString("facultad_id", "");
        asignarFacultades();
    }

    private void asignarFacultades() {
        switch (facultad){
            case "1":
                etFacultades.getEditText().setText("Arquitectura y Urbanismo");
                break;
            case "2":
                etFacultades.getEditText().setText("Artes, Diseño y Ciencias de la Cultura");
                break;
            case "3":
                etFacultades.getEditText().setText("Ciencias Agrarias");
                break;
            case "4":
                etFacultades.getEditText().setText("Ciencias Económicas");
                break;
            case "5":
                etFacultades.getEditText().setText("Ciencias Exactas y Naturales y Agrimensura");
                break;
            case "6":
                etFacultades.getEditText().setText("Ciencias Veterinarias");
                break;
            case "7":
                etFacultades.getEditText().setText("Derecho y Ciencias Sociales y Políticas");
                break;
            case "8":
                etFacultades.getEditText().setText("Humanidades");
                break;
            case "9":
                etFacultades.getEditText().setText("Ingeniería");
                break;
            case "10":
                etFacultades.getEditText().setText("Medicina");
                break;
            case "11":
                etFacultades.getEditText().setText("Odontología");
                break;
            case "12":
                etFacultades.getEditText().setText("Instituto de Ciencias Criminalísticas y Criminología");
                break;
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
                    startActivity(new Intent(getActivity().getApplicationContext(), AlumnoActivity.class));
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
                parametros.put("estado_civil", etestadocivil.getEditText().getText().toString());
                parametros.put("email", etemail.getEditText().getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(request);
    }

    public boolean validarCampos() {
        if (etestadocivil.getEditText().getText().toString().isEmpty()) {
            etestadocivil.setError("Ingrese estado civil");
            return false;
        }
        if (etemail.getEditText().getText().toString().isEmpty()) {
            etemail.setError("Ingrese email");
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(etemail.getEditText().getText()).matches()) { //si no es correo electronico valido
            etemail.setError("Formato de correo electrónico incorrecto");
            return false;
        }
        return true;
    }

    public void mostrarDatos() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int i=0;
                    etdni.getEditText().setText(jsonArray.getJSONObject(i).getString("dni"));
                    etnya.getEditText().setText(jsonArray.getJSONObject(i).getString("apellido")+" " +
                            jsonArray.getJSONObject(i).getString("nombres"));
                    sexoBD = jsonArray.getJSONObject(i).getString("sexo_id");
                    etestadocivil.getEditText().setText(jsonArray.getJSONObject(i).getString("estado_civil"));
                    etemail.getEditText().setText(jsonArray.getJSONObject(i).getString("email"));
                    etlu.getEditText().setText(jsonArray.getJSONObject(i).getString("lu"));
                    facultad = jsonArray.getJSONObject(i).getString("facultad_id");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("persona_id", idpersona);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(request);
    }
}