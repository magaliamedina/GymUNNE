package com.example.gimnasio_unne.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class RecuperarContrasenia extends AppCompatActivity {

    private TextInputEditText etCorreo;
    private Button btn;
    private String URL= "https://medinamagali.com.ar/gimnasio_unne/olvido_contrasenia.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasenia);
        etCorreo=findViewById(R.id.etCorreoRecuperarPass);
        btn= findViewById(R.id.btn_enviar_correo);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = con.getActiveNetworkInfo();
                if(networkInfo!=null && networkInfo.isConnected()) {
                    if(etCorreo.getText().toString().isEmpty()) {
                        etCorreo.setError("Ingrese un correo electrónico");
                    } else  enviarCorreo();
                    //Intent intent = new Intent(getApplicationContext(), RecuperarContrasenia.class);
                    //startActivity(intent);
                }
                else {
                    // no hay internet
                    Toast.makeText(RecuperarContrasenia.this, "No se pudo conectar, revise el " +
                            "acceso a Internet e intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void enviarCorreo() {
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("SUCESS")) {
                    Toast.makeText(RecuperarContrasenia.this, "Mire su casilla de mensajes", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RecuperarContrasenia.this, "No se encuentra registrado ese correo electrónico", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RecuperarContrasenia.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) //INICIO DEL POST
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros= new HashMap<String, String>();
                parametros.put("email", etCorreo.getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(RecuperarContrasenia.this);
        requestQueue.add(request);
    }
}