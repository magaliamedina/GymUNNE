package com.example.gimnasio_unne.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gimnasio_unne.AlumnoActivity;
import com.example.gimnasio_unne.Login;
import com.example.gimnasio_unne.R;
import com.example.gimnasio_unne.view.Reservar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

//PARA TODOS LOS PERFILES DE USUARIOS
public class FragmentCambiarPassword extends Fragment {

    TextInputLayout etActual, etNueva, etConfirmar;
    Button btn;
    String persona_id, password;
    String URL=  "https://medinamagali.com.ar/gimnasio_unne/cambiar_password.php";
    public FragmentCambiarPassword() {    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_cambiar_password, container, false);
        etActual = v.findViewById(R.id.etPasswordAnterior);
        etNueva=v.findViewById(R.id.etNuevaPassword);
        etConfirmar=v.findViewById(R.id.etConfirmarPassword);
        btn = v.findViewById(R.id.btnCambiarPassword);


        //caso normal sin recordar usuario y contraseña
        password=Login.password;
        persona_id= Login.personas_id;
        if(Login.password.equals("")) {
            getSharedPreferences();
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etActual.getEditText().getText().toString().isEmpty() || etNueva.getEditText().toString().isEmpty()
                       || etConfirmar.getEditText().getText().toString().isEmpty()) {
                   Toast.makeText(getActivity().getApplicationContext(), "Algunos campos están vacios", Toast.LENGTH_SHORT).show();
                } else {
                    validarPassword();
                }
            }
        });
        return v;
    }

    private void validarPassword() {
        StringRequest request=new StringRequest(Request.Method.POST, URL
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String mensaje="Error 301";
                if (response.equals("No coincide a la actual"))
                    mensaje= "No coincide a la contraseña actual";
                if(response.equals("No son iguales"))
                    mensaje ="Las contraseñas no son iguales";
                if(response.equals("Modificado correctamente"))
                    mensaje ="Contraseña modificada correctamente";
                Toast.makeText(getActivity().getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                limpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "No se pudo conectar, revise el " +
                        "acceso a Internet e intente nuevamente", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros= new HashMap<String, String>();
                parametros.put("persona_id", persona_id);
                parametros.put("actual", etActual.getEditText().getText().toString());
                parametros.put("nueva", etNueva.getEditText().getText().toString());
                parametros.put("repetircontrasena", etConfirmar.getEditText().getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(request);
    }

    public void getSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("datosusuario", Context.MODE_PRIVATE);
        password= sharedPreferences.getString("password", "");
        persona_id= sharedPreferences.getString("personas_id", "");
    }

    private void limpiarCampos() {
        etActual.getEditText().getText().clear();
        etNueva.getEditText().getText().clear();
        etConfirmar.getEditText().getText().clear();
    }
}